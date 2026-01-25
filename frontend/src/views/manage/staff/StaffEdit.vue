<template>
  <a-modal v-model="show" title="修改车主" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        修改
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
              <div v-if="fileList.length < 1">
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
        <a-col :span="24">
          <a-form-item label='身份证正面' v-bind="formItemLayout">
            <a-upload
              name="idCardFront"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="idCardFrontFileList"
              @preview="handleIdCardFrontPreview"
              @change="idCardFrontHandleChange"
            >
              <div v-if="idCardFrontFileList.length < 1">
                <a-icon type="plus" />
                <div class="ant-upload-text">
                  Upload
                </div>
              </div>
            </a-upload>
            <a-modal :visible="idCardFrontPreviewVisible" :footer="null" @cancel="handleIdCardFrontCancel">
              <img alt="身份证正面预览" style="width: 100%" :src="idCardFrontPreviewImage" />
            </a-modal>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='身份证反面' v-bind="formItemLayout">
            <a-upload
              name="idCardReverse"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="idCardReverseFileList"
              @preview="handleIdCardReversePreview"
              @change="idCardReverseHandleChange"
            >
              <div v-if="idCardReverseFileList.length < 1">
                <a-icon type="plus" />
                <div class="ant-upload-text">
                  Upload
                </div>
              </div>
            </a-upload>
            <a-modal :visible="idCardReversePreviewVisible" :footer="null" @cancel="handleIdCardReverseCancel">
              <img alt="身份证反面预览" style="width: 100%" :src="idCardReversePreviewImage" />
            </a-modal>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')
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
  name: 'staffEdit',
  props: {
    staffEditVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.staffEditVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      rowId: null,
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      formLoading: false,
      fileList: [], // 原有照片
      previewVisible: false,
      previewImage: '',
      idCardFrontFileList: [], // 身份证正面照片
      idCardFrontPreviewVisible: false,
      idCardFrontPreviewImage: '',
      idCardReverseFileList: [], // 身份证反面照片
      idCardReversePreviewVisible: false,
      idCardReversePreviewImage: '',
      pharmacyList: [],
      shopList: []
    }
  },
  methods: {
    handleCancel () {
      this.previewVisible = false
    },
    // 身份证正面预览相关方法
    async handleIdCardFrontPreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
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

    // 身份证反面预览相关方法
    async handleIdCardReversePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
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
        file.preview = await getBase64(file.originFileObj)
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
    // 新增身份证照片初始化方法
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
    responsibleInit (responsible) {
      this.formLoading = false
      if (responsible !== null && responsible !== '') {
        let responsibleList = []
        responsible.split(',').forEach((id, index) => {
          responsibleList.push(parseInt(id))
        })
        this.checkedList = responsibleList
        console.log(JSON.stringify(this.checkedList))
        this.onChange(this.checkedList)
      }
      setTimeout(() => {
        this.formLoading = true
      }, 200)
    },
    setFormValues ({...staff}) {
      this.rowId = staff.id
      let fields = ['name', 'status', 'sex', 'deptId', 'realName', 'birthDate', 'phone', 'email', 'idNumber', 'ethnicity', 'address']
      let obj = {}
      Object.keys(staff).forEach((key) => {
        if (key === 'sex' || key === 'status') {
          staff[key] = staff[key].toString()
        }
        if (key === 'birthDate' && staff[key] !== null) {
          staff[key] = moment(staff[key], 'YYYY-MM-DD')
        }
        if (key === 'images') {
          this.fileList = []
          this.imagesInit(staff['images'])
        }
        // 添加身份证正反面照片处理
        if (key === 'idCardFrontImages') {
          this.idCardFrontFileList = []
          this.idCardFrontImagesInit(staff['idCardFrontImages'])
        }
        if (key === 'idCardReverseImages') {
          this.idCardReverseFileList = []
          this.idCardReverseImagesInit(staff['idCardReverseImages'])
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = staff[key]
        }
      })
      this.form.setFieldsValue(obj)
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
      // 获取图片List
      let images = []
      this.fileList.forEach(image => {
        if (image.response !== undefined) {
          images.push(image.response)
        } else {
          images.push(image.name)
        }
      })
      // 获取身份证正面图片
      let idCardFrontImages = []
      this.idCardFrontFileList.forEach(image => {
        if (image.response !== undefined) {
          idCardFrontImages.push(image.response)
        } else {
          idCardFrontImages.push(image.name)
        }
      })
      // 获取身份证反面图片
      let idCardReverseImages = []
      this.idCardReverseFileList.forEach(image => {
        if (image.response !== undefined) {
          idCardReverseImages.push(image.response)
        } else {
          idCardReverseImages.push(image.name)
        }
      })
      this.form.validateFields((err, values) => {
        values.id = this.rowId
        values.images = images.length > 0 ? images.join(',') : null
        values.idCardFrontImages = idCardFrontImages.length > 0 ? idCardFrontImages.join(',') : null
        values.idCardReverseImages = idCardReverseImages.length > 0 ? idCardReverseImages.join(',') : null
        if (values.birthDate) {
          values.birthDate = moment(values.birthDate).format('YYYY-MM-DD')
        }
        if (!err) {
          this.loading = true
          this.$put('/business/staff-info', {
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
