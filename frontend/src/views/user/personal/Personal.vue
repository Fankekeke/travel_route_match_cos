
<template>
  <a-row :gutter="24" style="width: 100%; padding: 20px;">
    <a-col :span="24">
      <div class="profile-container">
        <!-- 顶部标题区域 -->
        <div class="profile-header">
          <h2 class="profile-title">
            <a-icon type="user" /> 个人资料管理
          </h2>
          <p class="profile-subtitle">完善您的个人信息，让账户更安全</p>
        </div>

        <!-- 个人信息卡片 -->
        <a-card class="profile-card" :bordered="false">
          <a-spin :spinning="dataLoading">
            <a-form :form="form" layout="vertical" class="profile-form">
              <a-row :gutter="24">
                <!-- 左侧头像区域 -->
                <a-col :span="8" class="avatar-section">
                  <div class="avatar-wrapper">
                    <a-upload
                      name="avatar"
                      :action="uploadUrl"
                      list-type="picture-card"
                      :file-list="fileList"
                      @preview="handlePreview"
                      @change="picHandleChange"
                      :headers="headers"
                      :max-count="1"
                    >
                      <div class="avatar-upload-area" v-if="fileList.length < 1">
                        <a-icon type="camera" class="avatar-camera-icon" />
                        <div class="avatar-upload-text">点击上传</div>
                      </div>
                    </a-upload>
                    <div class="avatar-actions">
                      <a-button
                        type="link"
                        size="small"
                        @click="clearAvatar"
                        v-if="fileList.length > 0"
                      >
                        <a-icon type="delete" /> 删除
                      </a-button>
                    </div>
                  </div>
                </a-col>

                <!-- 右侧信息区域 -->
                <a-col :span="16">
                  <a-row :gutter="16">
                    <a-col :span="24">
                      <a-form-item label='用户编号' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="['code', { initialValue: '' }]"
                          disabled
                          class="disabled-input"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='用户姓名' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="[
                            'name',
                            {
                              rules: [{ required: true, message: '请输入用户姓名!' }]
                            }
                          ]"
                          placeholder="请输入用户姓名"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='联系电话' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="[
                            'phone',
                            {
                              rules: [
                                { required: true, message: '请输入联系电话!' },
                                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码!' }
                              ]
                            }
                          ]"
                          placeholder="请输入联系电话"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='邮箱地址' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="[
                            'mail',
                            {
                              rules: [
                                { required: true, message: '请输入邮箱地址!' },
                                { type: 'email', message: '请输入正确的邮箱地址!' }
                              ]
                            }
                          ]"
                          placeholder="请输入邮箱地址"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='性别' v-bind="formItemLayout" class="form-item-custom">
                        <a-select
                          v-decorator="[
                            'sex',
                            {
                              rules: [{ required: true, message: '请选择性别!' }],
                              initialValue: '1'
                            }
                          ]"
                          placeholder="请选择性别"
                        >
                          <a-select-option value="1">男</a-select-option>
                          <a-select-option value="2">女</a-select-option>
                        </a-select>
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='积分' v-bind="formItemLayout" class="form-item-custom">
                        <a-input-number                          style="width: 100%"
                                                                 v-decorator="['integral', { initialValue: 0 }]"
                                                                 :min="0"
                                                                 :precision="2"
                                                                 disabled
                                                                 class="disabled-input"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='创建时间' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="['createDate', { initialValue: '' }]"
                          disabled
                          class="disabled-input"
                        />
                      </a-form-item>
                    </a-col>
                  </a-row>

                  <!-- 操作按钮区域 -->
                  <div class="button-group">
                    <a-button
                      type="primary"
                      @click="handleSubmit"
                      :loading="loading"
                      class="submit-btn"
                    >
                      <a-icon type="save" /> 保存信息
                    </a-button>
                    <a-button
                      @click="resetForm"
                      style="margin-left: 12px;"
                      class="reset-btn"
                    >
                      <a-icon type="sync" /> 重置
                    </a-button>
                  </div>
                </a-col>
              </a-row>
            </a-form>
          </a-spin>
        </a-card>
      </div>

      <!-- 头像预览模态框 -->
      <a-modal
        :visible="previewVisible"
        :footer="null"
        @cancel="handleCancel"
        centered
        class="preview-modal"
      >
        <img alt="头像预览" style="width: 100%; border-radius: 8px;" :src="previewImage" />
      </a-modal>
    </a-col>
  </a-row>
</template>

<script>import { mapState } from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}

function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}

export default {
  name: 'PersonalInfo',
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  data () {
    return {
      rowId: null,
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      dataLoading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      uploadUrl: 'http://127.0.0.1:9527/file/fileUpload/',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    }
  },
  mounted () {
    this.dataInit()
  },
  methods: {
    handleCancel () {
      this.previewVisible = false
    },

    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },

    picHandleChange ({ fileList }) {
      this.fileList = fileList.slice(-1)
    },

    clearAvatar () {
      this.fileList = []
    },

    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({
            uid: index,
            name: image,
            status: 'done',
            url: 'http://127.0.0.1:9527/imagesWeb/' + image
          })
        })
        this.fileList = imageList
      }
    },

    dataInit () {
      this.dataLoading = true
      this.$get(`/business/user-info/detailByUserId/${this.currentUser.userId}`).then((r) => {
        this.rowId = r.data.data.id
        this.setFormValues(r.data.data)
        this.dataLoading = false
      }).catch(err => {
        console.error('获取用户信息失败:', err)
        this.dataLoading = false
      })
    },

    setFormValues ({...userInfo}) {
      this.rowId = userInfo.id
      let fields = ['code', 'name', 'mail', 'phone', 'userId', 'createDate', 'sex', 'integral']
      let obj = {}

      Object.keys(userInfo).forEach((key) => {
        if (key === 'sex' || key === 'status') {
          userInfo[key] = userInfo[key].toString()
        }

        if (key === 'images') {
          this.fileList = []
          this.imagesInit(userInfo['images'])
        }

        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = userInfo[key]
        }
      })

      if (obj.integral === undefined || obj.integral === null) {
        obj.integral = 0
      }

      this.form.setFieldsValue(obj)
    },

    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (err) {
          return
        }

        let images = []
        this.fileList.forEach(image => {
          if (image.response !== undefined) {
            images.push(image.response)
          } else if (image.url) {
            const urlParts = image.url.split('/')
            images.push(urlParts[urlParts.length - 1])
          }
        })

        values.id = this.rowId
        values.images = images.length > 0 ? images.join(',') : null

        this.loading = true
        this.$put('/business/user-info', {
          ...values
        }).then((r) => {
          this.$message.success('修改信息成功')
          this.dataInit()
          this.loading = false
        }).catch((error) => {
          console.error('更新失败:', error)
          this.$message.error('修改信息失败')
          this.loading = false
        })
      })
    },

    resetForm () {
      this.dataInit()
      this.form.resetFields()
    }
  }
}
</script>

<style scoped>.profile-container {
  padding: 30px;
  border-radius: 12px;
  width: 60%;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  margin-bottom: 30px;
}

.profile-title {
  color: #1d3557;
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.profile-subtitle {
  color: #6c757d;
  font-size: 14px;
  margin: 0;
}

.profile-card {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  overflow: hidden;
  background: white;
  transition: all 0.3s ease;
}

.profile-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.profile-form {
  padding: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.avatar-wrapper {
  position: relative;
  text-align: center;
}

.avatar-upload-area {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: #f8f9fa;
  border: 2px dashed #dee2e6;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-upload-area:hover {
  border-color: #1890ff;
  background: #e6f7ff;
}

.avatar-camera-icon {
  font-size: 24px;
  color: #6c757d;
  margin-bottom: 6px;
}

.avatar-upload-text {
  font-size: 12px;
  color: #6c757d;
}

.avatar-actions {
  margin-top: 10px;
}

.form-item-custom {
  margin-bottom: 20px;
}

.disabled-input {
  background-color: #fafafa;
  cursor: not-allowed;
}

.button-group {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.submit-btn {
  min-width: 100px;
  font-weight: 500;
}

.reset-btn {
  min-width: 80px;
  font-weight: 500;
}

.preview-modal .ant-modal-body {
  padding: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    padding: 15px;
  }

  .profile-title {
    font-size: 22px;
  }

  .avatar-upload-area {
    width: 100px;
    height: 100px;
  }
}
</style>
