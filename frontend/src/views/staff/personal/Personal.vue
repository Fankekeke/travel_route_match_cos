
<template>
  <a-row :gutter="24" style="width: 100%; padding: 20px;">
    <a-col :span="16" :offset="4">
      <div class="profile-container">
        <!-- 顶部标题区域 -->
        <div class="profile-header">
          <h2 class="profile-title">
            <a-icon type="user" /> 车主信息管理
          </h2>
          <p class="profile-subtitle">完善您的车主信息，提升账户安全性</p>
        </div>

        <a-row :gutter="24">
          <!-- 左侧信息编辑区域 -->
          <a-col :span="12">
            <a-card class="info-card" :bordered="false">
              <a-spin :spinning="dataLoading">
                <a-form :form="form" layout="vertical" class="info-form">
                  <a-row :gutter="16">
                    <a-col :span="12">
                      <a-form-item label='车主姓名' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="[
                            'name',
                            { rules: [{ required: true, message: '请输入名称!' }] }
                          ]"
                          placeholder="请输入车主姓名"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='车主性别' v-bind="formItemLayout" class="form-item-custom">
                        <a-select
                          v-decorator="[
                            'sex',
                            { rules: [{ required: true, message: '请输入车主性别!' }] }
                          ]"
                          placeholder="请选择性别"
                        >
                          <a-select-option value="1">男</a-select-option>
                          <a-select-option value="2">女</a-select-option>
                        </a-select>
                      </a-form-item>
                    </a-col>

                    <a-col :span="12" v-if="staffInfo && staffInfo.status !== '-1'">
                      <a-form-item label='车主状态' v-bind="formItemLayout" class="form-item-custom">
                        <a-radio-group
                          button-style="solid"
                          v-decorator="[
                            'status',
                            { rules: [{ required: true, message: '请输入车主状态!' }] }
                          ]"
                        >
                          <a-radio-button value="1">
                            接单中
                          </a-radio-button>
                          <a-radio-button value="2">
                            离开
                          </a-radio-button>
                        </a-radio-group>
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='真实姓名' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="[
                            'realName',
                            { rules: [{ required: true, message: '请输入真实姓名!' }] }
                          ]"
                          placeholder="请输入真实姓名"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='出生日期' v-bind="formItemLayout" class="form-item-custom">
                        <a-date-picker                          style="width: 100%"
                                                                v-decorator="[
                            'birthDate',
                            { rules: [{ required: true, message: '请选择出生日期!' }] }
                          ]"
                                                                placeholder="请选择出生日期"
                                                                format="YYYY-MM-DD"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='联系方式' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="[
                            'phone',
                            {
                              rules: [
                                { required: true, message: '请输入联系方式!' },
                                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码!' }
                              ]
                            }
                          ]"
                          placeholder="请输入联系方式"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='邮箱地址' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="[
                            'email',
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
                      <a-form-item label='身份证号码' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="[
                            'idNumber',
                            {
                              rules: [
                                { required: true, message: '请输入身份证号码!' },
                                { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号码!' }
                              ]
                            }
                          ]"
                          placeholder="请输入身份证号码"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="12">
                      <a-form-item label='民族' v-bind="formItemLayout" class="form-item-custom">
                        <a-input
                          v-decorator="[
                            'ethnicity',
                            { rules: [{ required: true, message: '请输入民族!' }] }
                          ]"
                          placeholder="请输入民族"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="24">
                      <a-form-item label='身份证地址' v-bind="formItemLayout" class="form-item-custom">
                        <a-textarea
                          :rows="4"
                          v-decorator="[
                            'address',
                            { rules: [{ required: true, message: '请输入身份证地址!' }] }
                          ]"
                          placeholder="请输入身份证地址"
                        />
                      </a-form-item>
                    </a-col>

                    <a-col :span="24">
                      <a-form-item label='照片' v-bind="formItemLayout" class="form-item-custom">
                        <a-upload
                          name="avatar"
                          action="http://127.0.0.1:9527/file/fileUpload/"
                          list-type="picture-card"
                          :file-list="fileList"
                          @preview="handlePreview"
                          @change="picHandleChange"
                          :max-count="1"
                        >
                          <div v-if="fileList.length < 1">
                            <a-icon type="plus" />
                            <div class="ant-upload-text">
                              上传照片
                            </div>
                          </div>
                        </a-upload>
                        <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
                          <img alt="照片预览" style="width: 100%" :src="previewImage" />
                        </a-modal>
                      </a-form-item>
                    </a-col>

                    <a-col :span="24">
                      <a-form-item label='身份证正面' v-bind="formItemLayout" class="form-item-custom">
                        <a-upload
                          name="idCardFront"
                          action="http://127.0.0.1:9527/file/fileUpload/"
                          list-type="picture-card"
                          :file-list="idCardFrontFileList"
                          @preview="handleIdCardFrontPreview"
                          @change="idCardFrontHandleChange"
                          :max-count="1"
                        >
                          <div v-if="idCardFrontFileList.length < 1">
                            <a-icon type="plus" />
                            <div class="ant-upload-text">
                              上传身份证正面
                            </div>
                          </div>
                        </a-upload>
                        <a-modal :visible="idCardFrontPreviewVisible" :footer="null" @cancel="handleIdCardFrontCancel">
                          <img alt="身份证正面预览" style="width: 100%" :src="idCardFrontPreviewImage" />
                        </a-modal>
                      </a-form-item>
                    </a-col>

                    <a-col :span="24">
                      <a-form-item label='身份证反面' v-bind="formItemLayout" class="form-item-custom">
                        <a-upload
                          name="idCardReverse"
                          action="http://127.0.0.1:9527/file/fileUpload/"
                          list-type="picture-card"
                          :file-list="idCardReverseFileList"
                          @preview="handleIdCardReversePreview"
                          @change="idCardReverseHandleChange"
                          :max-count="1"
                        >
                          <div v-if="idCardReverseFileList.length < 1">
                            <a-icon type="plus" />
                            <div class="ant-upload-text">
                              上传身份证反面
                            </div>
                          </div>
                        </a-upload>
                        <a-modal :visible="idCardReversePreviewVisible" :footer="null" @cancel="handleIdCardReverseCancel">
                          <img alt="身份证反面预览" style="width: 100%" :src="idCardReversePreviewImage" />
                        </a-modal>
                      </a-form-item>
                    </a-col>
                  </a-row>

                  <div class="button-group">
                    <a-button type="primary" @click="handleSubmit" :loading="loading" class="submit-btn">
                      <a-icon type="save" /> 保存信息
                    </a-button>
                    <a-button @click="resetForm" style="margin-left: 12px;" class="reset-btn">
                      <a-icon type="sync" /> 重置
                    </a-button>
                  </div>
                </a-form>
              </a-spin>
            </a-card>
          </a-col>

          <!-- 右侧状态展示区域 -->
          <a-col :span="12">
            <a-card class="status-card" :bordered="false">
              <div class="status-container">
                <div v-if="staffInfo && staffInfo.status !== undefined">
                  <div v-if="staffInfo.status == -1" class="pending-status">
                    <div class="status-icon pending">
                      <a-icon type="exclamation-circle" style="font-size: 48px; color: #faad14;" />
                    </div>
                    <h3>人脸认证提醒</h3>
                    <p>您尚未完成人脸认证，请及时进行人脸认证以激活账户功能</p>
                    <a-button
                      type="primary"
                      size="large"
                      @click="faceView.visiable = true"
                      class="auth-btn"
                    >
                      进行人脸认证
                    </a-button>
                    <div class="verification-action" v-if="faceVerification">
                      <a-button
                        type="primary"
                        @click="updateStatusToWorking"
                      >
                        更新为接单中
                      </a-button>
                    </div>
                  </div>
                  <div v-else class="verified-status">
                    <div class="status-icon verified">
                      <a-icon type="check-circle" style="font-size: 48px; color: #52c41a;" />
                    </div>
                    <h3>人脸认证状态</h3>
                    <p>您已完成人脸认证，账户功能已激活</p>
                    <div class="status-info">
                      <div class="info-item">
                        <span class="info-label">当前状态：</span>
                        <span class="info-value">{{ getStatusText(staffInfo.status) }}</span>
                      </div>
                      <div class="info-item">
                        <span class="info-label">认证时间：</span>
                        <span class="info-value">{{ formatDate(staffInfo.authDate || new Date()) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else class="loading-status">
                  <a-spin size="large" tip="加载中..." />
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </div>

      <!-- 人脸识别模态框 -->
      <a-modal v-model="faceView.visiable" title="上传人脸照片" width="600px">
        <template slot="footer">
          <a-button key="back" @click="faceView.visiable = false">
            取消
          </a-button>
        </template>
        <div class="camera-container">
          <div class="camera-wrapper">
            <video id="videoCamera" :width="videoWidth" :height="videoHeight" autoplay></video>
            <canvas style="display:none;" id="canvasCamera" :width="videoWidth" :height="videoHeight"></canvas>
            <div v-if="imgSrc" class="img-preview">
              <img :src="imgSrc" alt="人脸预览" class="face-image">
            </div>
            <div class="camera-controls">
              <a-button
                size="small"
                type="primary"
                @click.stop.prevent="getCompetence"
                class="control-btn"
              >
                打开摄像头
              </a-button>
              <a-button
                size="small"
                @click.stop.prevent="stopNavigator"
                class="control-btn"
              >
                关闭摄像头
              </a-button>
              <a-button
                size="small"
                type="primary"
                @click.stop.prevent="setImage"
                class="control-btn"
              >
                识别
              </a-button>
            </div>
          </div>
        </div>
      </a-modal>
    </a-col>
  </a-row>
</template>

<script>import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}

export default {
  name: 'StaffProfile',
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
      courseInfo: [],
      dataLoading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      idCardFrontFileList: [],
      idCardFrontPreviewVisible: false,
      idCardFrontPreviewImage: '',
      idCardReverseFileList: [],
      idCardReversePreviewVisible: false,
      idCardReversePreviewImage: '',

      faceView: {
        visiable: false
      },
      videoWidth: 470,
      videoHeight: 300,
      imgSrc: '',
      staffInfo: null,
      thisCancas: null,
      thisContext: null,
      thisVideo: null,
      faceVerification: false
    }
  },
  mounted () {
    this.dataInit()
  },
  methods: {
    updateStatusToWorking () {
      this.$put('/business/staff-info', {
        id: this.staffInfo.id,
        status: 1
      }).then((r) => {
        this.$message.success('修改信息成功')
        this.dataInit()
      })
    },
    getCompetence () {
      var _this = this
      this.thisCancas = document.getElementById('canvasCamera')
      this.thisContext = this.thisCancas.getContext('2d')
      this.thisVideo = document.getElementById('videoCamera')
      if (navigator.mediaDevices === undefined) {
        navigator.mediaDevices = {}
      }
      if (navigator.mediaDevices.getUserMedia === undefined) {
        navigator.mediaDevices.getUserMedia = function (constraints) {
          var getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.getUserMedia
          if (!getUserMedia) {
            return Promise.reject(new Error('getUserMedia is not implemented in this browser'))
          }
          return new Promise(function (resolve, reject) {
            getUserMedia.call(navigator, constraints, resolve, reject)
          })
        }
      }
      var constraints = { audio: false, video: { width: this.videoWidth, height: this.videoHeight, transform: 'scaleX(-1)' } }
      navigator.mediaDevices.getUserMedia(constraints).then(function (stream) {
        if ('srcObject' in _this.thisVideo) {
          _this.thisVideo.srcObject = stream
        } else {
          _this.thisVideo.src = window.URL.createObjectURL(stream)
        }
        _this.thisVideo.onloadedmetadata = function (e) {
          _this.thisVideo.play()
        }
      }).catch(err => {
        console.log(err)
      })
    },
    setImage () {
      var _this = this
      _this.thisContext.drawImage(_this.thisVideo, 0, 0, _this.videoWidth, _this.videoHeight)
      var image = this.thisCancas.toDataURL('image/png')
      let data = { file: image.replace(/^data:image\/\w+;base64,/, ''), name: this.name }
      this.$post('/business/face/verification', data).then((r) => {
        if (r.data.msg !== '成功') {
          this.$message.error(r.data.msg)
        } else {
          this.$message.success('验证通过')
          setTimeout(() => {
            this.faceVerification = true
            this.faceView.visiable = false
          })
        }
      })
    },
    dataURLtoFile (dataurl, filename) {
      var arr = dataurl.split(',')
      var mime = arr[0].match(/:(.*?);/)[1]
      var bstr = atob(arr[1])
      var n = bstr.length
      var u8arr = new Uint8Array(n)
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n)
      }
      return new File([u8arr], filename, { type: mime })
    },
    stopNavigator () {
      this.thisVideo.srcObject.getTracks()[0].stop()
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handleIdCardFrontPreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await this.getBase64(file.originFileObj)
      }
      this.idCardFrontPreviewImage = file.url || file.preview
      this.idCardFrontPreviewVisible = true
    },
    handleIdCardFrontCancel () {
      this.idCardFrontPreviewVisible = false
    },
    idCardFrontHandleChange ({ fileList }) {
      this.idCardFrontFileList = fileList
    },
    async handleIdCardReversePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await this.getBase64(file.originFileObj)
      }
      this.idCardReversePreviewImage = file.url || file.preview
      this.idCardReversePreviewVisible = true
    },
    handleIdCardReverseCancel () {
      this.idCardReversePreviewVisible = false
    },
    idCardReverseHandleChange ({ fileList }) {
      this.idCardReverseFileList = fileList
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await this.getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    idCardFrontImagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.idCardFrontFileList = imageList
      }
    },
    idCardReverseImagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.idCardReverseFileList = imageList
      }
    },
    dataInit () {
      this.dataLoading = true
      this.$get(`/business/staff-info/queryDetailByStaffId/${this.currentUser.userId}`).then((r) => {
        this.rowId = r.data.data.id
        this.staffInfo = r.data.data
        this.setFormValues(r.data.data)
        this.dataLoading = false
      })
    },
    setFormValues ({...student}) {
      this.rowId = student.id
      let fields = ['name', 'status', 'sex', 'deptId', 'realName', 'birthDate', 'phone', 'email', 'idNumber', 'ethnicity', 'address']
      let obj = {}
      Object.keys(student).forEach((key) => {
        if (key === 'sex' || key === 'status') {
          student[key] = student[key].toString()
        }
        if (key === 'birthDate' && student[key] !== null) {
          student[key] = moment(student[key], 'YYYY-MM-DD')
        }
        if (key === 'images') {
          this.fileList = []
          this.imagesInit(student['images'])
        }
        if (key === 'idCardFrontImages') {
          this.idCardFrontFileList = []
          this.idCardFrontImagesInit(student['idCardFrontImages'])
        }
        if (key === 'idCardReverseImages') {
          this.idCardReverseFileList = []
          this.idCardReverseImagesInit(student['idCardReverseImages'])
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = student[key]
        }
      })
      this.form.setFieldsValue(obj)
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        values.id = this.rowId
        let images = []
        this.fileList.forEach(image => {
          if (image.response !== undefined) {
            images.push(image.response)
          } else {
            images.push(image.name)
          }
        })
        let idCardFrontImages = []
        this.idCardFrontFileList.forEach(image => {
          if (image.response !== undefined) {
            idCardFrontImages.push(image.response)
          } else {
            idCardFrontImages.push(image.name)
          }
        })
        let idCardReverseImages = []
        this.idCardReverseFileList.forEach(image => {
          if (image.response !== undefined) {
            idCardReverseImages.push(image.response)
          } else {
            idCardReverseImages.push(image.name)
          }
        })
        if (!err) {
          values.images = images.length > 0 ? images.join(',') : null
          values.idCardFrontImages = idCardFrontImages.length > 0 ? idCardFrontImages.join(',') : null
          values.idCardReverseImages = idCardReverseImages.length > 0 ? idCardReverseImages.join(',') : null
          if (values.birthDate) {
            values.birthDate = moment(values.birthDate).format('YYYY-MM-DD')
          }
          this.loading = true
          this.$put('/business/staff-info', {
            ...values
          }).then((r) => {
            this.$message.success('修改信息成功')
            this.dataInit()
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        }
      })
    },
    resetForm() {
      this.dataInit()
      this.form.resetFields()
    },
    getStatusText(status) {
      switch(status) {
        case '1': return '接单中'
        case '2': return '离开'
        default: return '未知'
      }
    },
    formatDate(dateString) {
      return moment(dateString).format('YYYY-MM-DD HH:mm:ss')
    },
    getBase64(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
      });
    }
  }
}
</script>

<style scoped>.profile-container {
  min-height: calc(100vh - 40px);
  padding: 30px;
  border-radius: 12px;
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

.info-card {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  overflow: hidden;
  background: white;
  transition: all 0.3s ease;
}

.info-card:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.status-card {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  overflow: hidden;
  background: white;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.status-container {
  padding: 24px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pending-status {
  text-align: center;
  padding: 20px;
}

.verified-status {
  text-align: center;
  padding: 20px;
}

.status-icon {
  margin-bottom: 20px;
}

.status-icon.pending {
  color: #faad14;
}

.status-icon.verified {
  color: #52c41a;
}

.pending-status h3,
.verified-status h3 {
  color: #1d3557;
  margin: 10px 0;
}

.pending-status p,
.verified-status p {
  color: #6c757d;
  margin: 10px 0;
  line-height: 1.6;
}

.auth-btn {
  margin-top: 20px;
  min-width: 180px;
}

.verification-action {
  margin-top: 20px;
}

.status-info {
  margin-top: 25px;
  text-align: left;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.info-item {
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
}

.info-label {
  font-weight: 500;
  color: #495057;
}

.info-value {
  color: #6c757d;
}

.loading-status {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.info-form {
  padding: 20px;
}

.form-item-custom {
  margin-bottom: 20px;
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

.camera-container {
  text-align: center;
}

.camera-wrapper {
  padding: 15px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  background: #f8f9fa;
}

.img-preview {
  margin: 10px 0;
}

.face-image {
  max-width: 100%;
  border-radius: 8px;
  border: 1px solid #eee;
}

.camera-controls {
  margin-top: 15px;
}

.control-btn {
  margin: 0 5px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    padding: 15px;
  }

  .profile-title {
    font-size: 22px;
  }

  .info-card, .status-card {
    margin-bottom: 20px;
  }
}
</style>
