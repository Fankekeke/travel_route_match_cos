<template>
  <a-row :gutter="8" style="width: 100%">
    <a-col :span="8">
      <div style="background:#ECECEC; padding:30px;margin-top: 30px">
        <a-card :bordered="false">
          <b style="font-size: 15px">我的信息</b>
        </a-card>
        <a-card :bordered="false">
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
        </a-card>
      </div>
    </a-col>
    <a-col :span="16">
      <div style="background:#ECECEC; padding:30px;margin-top: 30px">
        <a-card :bordered="false">
          <a-spin :spinning="dataLoading">
            <div v-if="staffInfo && staffInfo.status !== undefined">
              <div v-if="staffInfo.status == -1" style="text-align: center; padding: 20px;">
                <a-alert
                  message="人脸认证提醒"
                  description="您尚未完成人脸认证，请及时进行人脸认证以激活账户功能"
                  type="warning"
                  show-icon
                />
                <a-button
                  type="primary"      style="margin-top: 15px;"
                  @click="faceView.visiable = true"
                >
                  进行人脸认证
                </a-button>
                <div style="margin-top: 15px;" v-if="faceVerification">
                  <a-button
                    type="primary"
                    @click="updateStatusToWorking"
                  >
                    更新为接单中
                  </a-button>
                </div>
              </div>
              <div v-else style="text-align: center; padding: 20px;">
                <a-alert
                  message="人脸认证状态"
                  description="您已完成人脸认证，账户功能已激活"
                  type="success"
                  show-icon
                />
              </div>
            </div>
            <div v-else style="text-align: center; padding: 20px;">
              <a-spin tip="加载中..." />
            </div>
          </a-spin>
        </a-card>
      </div>
      <a-modal v-model="faceView.visiable" title="上传人脸照片">
        <template slot="footer">
          <a-button key="back" @click="faceView.visiable = false">
            取消
          </a-button>
        </template>
        <div style="height: 350px">
          <div class="camera_outer">
            <video id="videoCamera" :width="videoWidth" :height="videoHeight" autoplay></video>
            <canvas style="display:none;" id="canvasCamera" :width="videoWidth" :height="videoHeight" ></canvas>
            <div v-if="imgSrc" class="img_bg_camera">
              <img :src="imgSrc" alt="" class="tx_img">
            </div>
            <div style="margin-top: 10px">
              <a-button
                size="small"
                type="primary"
                @click.stop.prevent="getCompetence">打开摄像头
              </a-button>
              <a-button
                size="small"
                type="primary"
                @click.stop.prevent="stopNavigator">关闭摄像头
              </a-button>
              <a-button
                size="small"
                type="primary"
                @click.stop.prevent="setImage">识别
              </a-button>
            </div>
          </div>
        </div>
      </a-modal>
    </a-col>
  </a-row>
</template>

<script>
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
export default {
  name: 'User',
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
      fileList: [], // 原有照片
      previewVisible: false,
      previewImage: '',
      idCardFrontFileList: [], // 身份证正面照片
      idCardFrontPreviewVisible: false,
      idCardFrontPreviewImage: '',
      idCardReverseFileList: [], // 身份证反面照片
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
      // 旧版本浏览器可能根本不支持mediaDevices，我们首先设置一个空对象
      if (navigator.mediaDevices === undefined) {
        navigator.mediaDevices = {}
      }
      // 一些浏览器实现了部分mediaDevices，我们不能只分配一个对象
      // 使用getUserMedia，因为它会覆盖现有的属性。
      // 这里，如果缺少getUserMedia属性，就添加它。
      if (navigator.mediaDevices.getUserMedia === undefined) {
        navigator.mediaDevices.getUserMedia = function (constraints) {
          // 首先获取现存的getUserMedia(如果存在)
          var getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.getUserMedia
          // 有些浏览器不支持，会返回错误信息
          // 保持接口一致
          if (!getUserMedia) {
            return Promise.reject(new Error('getUserMedia is not implemented in this browser'))
          }
          // 否则，使用Promise将调用包装到旧的navigator.getUserMedia
          return new Promise(function (resolve, reject) {
            getUserMedia.call(navigator, constraints, resolve, reject)
          })
        }
      }
      var constraints = { audio: false, video: { width: this.videoWidth, height: this.videoHeight, transform: 'scaleX(-1)' } }
      navigator.mediaDevices.getUserMedia(constraints).then(function (stream) {
        // 旧的浏览器可能没有srcObject
        if ('srcObject' in _this.thisVideo) {
          _this.thisVideo.srcObject = stream
        } else {
          // 避免在新的浏览器中使用它，因为它正在被弃用。
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
      // 点击，canvas画图
      _this.thisContext.drawImage(_this.thisVideo, 0, 0, _this.videoWidth, _this.videoHeight)
      // 获取图片base64链接
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
      // _this.imgSrc = image
      // this.$emit('refreshDataList', this.imgSrc)
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
        // 添加身份证正反面照片处理
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
    }
  }
}
</script>

<style scoped>
</style>
