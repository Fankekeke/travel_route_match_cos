<template>
  <a-modal v-model="show" title="新增车主" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        提交
      </a-button>
    </template>
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">
        <a-col :span="12">
          <a-form-item label='车主姓名' v-bind="formItemLayout">
            <a-input v-decorator="[
            'name',
            { rules: [{ required: true, message: '请输入名称!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='车主性别' v-bind="formItemLayout">
            <a-select v-decorator="[
              'sex',
              { rules: [{ required: true, message: '请输入车主性别!' }] }
              ]">
              <a-select-option value="1">男</a-select-option>
              <a-select-option value="2">女</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='车主状态' v-bind="formItemLayout">
            <a-radio-group button-style="solid" v-decorator="[
              'status',
              { rules: [{ required: true, message: '请输入车主状态!' }] }
              ]">
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
          <a-form-item label='真实姓名' v-bind="formItemLayout">
            <a-input v-decorator="[
            'realName',
            { rules: [{ required: true, message: '请输入真实姓名!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='出生日期' v-bind="formItemLayout">
            <a-date-picker
              style="width: 100%"
              v-decorator="[
                'birthDate',
                { rules: [{ required: true, message: '请选择出生日期!' }] }
              ]"
              placeholder="请选择出生日期"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='联系方式' v-bind="formItemLayout">
            <a-input v-decorator="[
            'phone',
            {
              rules: [
                { required: true, message: '请输入联系方式!' },
                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码!' }
              ]
            }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='邮箱地址' v-bind="formItemLayout">
            <a-input v-decorator="[
            'email',
            {
              rules: [
                { required: true, message: '请输入邮箱地址!' },
                { type: 'email', message: '请输入正确的邮箱地址!' }
              ]
            }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='身份证号码' v-bind="formItemLayout">
            <a-input v-decorator="[
            'idNumber',
            {
              rules: [
                { required: true, message: '请输入身份证号码!' },
                { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号码!' }
              ]
            }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='民族' v-bind="formItemLayout">
            <a-input v-decorator="[
            'ethnicity',
            { rules: [{ required: true, message: '请输入民族!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='身份证地址' v-bind="formItemLayout">
            <a-textarea
              :rows="4"
              v-decorator="[
                'address',
                { rules: [{ required: true, message: '请输入身份证地址!' }] }
              ]"
            />
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='照片' v-bind="formItemLayout">
            <a-upload
              name="avatar"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="fileList"
              @preview="handlePreview"
              @change="picHandleChange"
            >
              <div v-if="fileList.length < 2">
                <a-icon type="plus" />
                <div class="ant-upload-text">
                  Upload
                </div>
              </div>
            </a-upload>
            <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
              <img alt="example" style="width: 100%" :src="previewImage" />
            </a-modal>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import {mapState} from 'vuex'
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
export default {
  name: 'staffAdd',
  props: {
    staffAddVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.staffAddVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      pharmacyList: [],
      shopList: []
    }
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
      this.fileList = fileList
    },
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        // 获取图片List
        let images = []
        this.fileList.forEach(image => {
          images.push(image.response)
        })
        if (!err) {
          values.images = images.length > 0 ? images.join(',') : null
          values.pharmacyId = this.currentUser.userId
          this.loading = true
          this.$post('/business/staff-info', {
            ...values
          }).then((r) => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
