
<template>
  <a-modal
    v-model="visible"
    title="订单评价"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-form :form="form">
      <a-form-item label="评价内容">
        <a-textarea
          v-decorator="['content', { rules: [{ required: true, message: '请输入评价内容' }] }]"
          placeholder="请输入您的评价内容"
        />
      </a-form-item>
      <a-form-item label="评分">
        <a-rate
          v-decorator="['score', { initialValue: 5, rules: [{ required: true, message: '请选择评分' }] }]"
        />
      </a-form-item>
      <a-form-item label="上传图片">
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
        <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel1">
          <img alt="example" style="width: 100%" :src="previewImage" />
        </a-modal>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>export default {
  name: 'OrderEvaluate',
  props: {
    evaluateVisible: {
      type: Boolean,
      default: false
    },
    orderData: {
      type: Object,
      default: () => ({})
    }
  },
  data () {
    return {
      form: this.$form.createForm(this),
      fileList: [],
      previewVisible: false,
      previewImage: ''
    }
  },
  computed: {
    visible: {
      get () {
        return this.evaluateVisible
      },
      set (value) {
        this.$emit('update:evaluateVisible', value)
      }
    }
  },
  methods: {
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          let images = []
          this.fileList.forEach(image => {
            images.push(image.response)
          })
          const data = {
            orderId: this.orderData.id,
            userId: this.orderData.userId,
            staffId: this.orderData.staffId,
            content: values.content,
            score: values.score,
            images: images.length > 0 ? images.join(',') : null
          };
          this.$post('/business/evaluate-info', data).then(() => {
            this.handleCancel()
            this.$emit('success')
          })
        }
      })
    },
    handleCancel () {
      this.visible = false
      this.form.resetFields()
      this.fileList = []
    },
    handleCancel1 () {
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
    }
  }
}
</script>
