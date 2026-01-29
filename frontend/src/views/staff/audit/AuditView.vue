<template>
  <a-modal v-model="show" title="审核详情" @cancel="onClose" :width="1000">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        关闭
      </a-button>
      <a-button key="back1" @click="onAudit(2)" type="danger" v-if="moduleData.status == 0">
        驳回
      </a-button>
      <a-button key="back2" @click="onAudit(1)" type="primary" v-if="moduleData.status == 0">
        通过
      </a-button>
    </template>
    <div style="font-size: 13px;font-family: SimHei" v-if="moduleData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px">
          <span class="view-title">审核信息</span>
        </a-col>
        <a-col :span="8"><b>审核状态：</b>
          <span v-if="moduleData.auditStatus == 2" style="color: red">驳回</span>
          <span v-if="moduleData.auditStatus == 1" style="color: green">通过</span>
          <span v-if="moduleData.auditStatus == 0" style="color: orange">未审核</span>
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>创建时间：</b>{{ moduleData.createDate }}</a-col>
        <a-col :span="8"><b>状态更新时间：</b>{{ moduleData.statusDate }}</a-col>
        <a-col :span="8"><b>编码：</b>{{ moduleData.code }}</a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span class="view-title">用户信息</span></a-col>
        <a-col :span="8"><b>用户名：</b>{{ moduleData.name }}</a-col>
        <a-col :span="8"><b>真实姓名：</b>{{ moduleData.realName }}</a-col>
        <a-col :span="8"><b>性别：</b>{{ moduleData.sex === 1 ? '男' : '女' }}</a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>手机号：</b>{{ moduleData.phone }}</a-col>
        <a-col :span="8"><b>地址：</b>{{ moduleData.address }}</a-col>
        <a-col :span="8"><b>民族：</b>{{ moduleData.ethnicity }}</a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>身份证号：</b>{{ moduleData.idNumber }}</a-col>
        <a-col :span="8"><b>出生日期：</b>{{ moduleData.birthDate }}</a-col>
        <a-col :span="8"><b>服务评分：</b>{{ moduleData.serviceScore }}</a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span class="view-title">其他信息</span></a-col>
        <a-col :span="24"><b>申请内容：</b>{{ moduleData.introduction || '- -' }}</a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>标签：</b>{{ moduleData.tag || '- -' }}</a-col>
        <a-col :span="8"><b>申请图片：</b>
          <a-popover v-if="moduleData.images" placement="top">
            <template slot="content">
              <img :src="`http://127.0.0.1:9527/imagesWeb/${moduleData.images}`"
                   style="max-width: 300px; max-height: 300px;" />
            </template>
            <a-avatar :src="`http://127.0.0.1:9527/imagesWeb/${moduleData.images}`" />
          </a-popover>
          <span v-else>- -</span>
        </a-col>
        <a-col :span="8"><b>用户图像：</b>
          <a-popover v-if="moduleData.userImages" placement="top">
            <template slot="content">
              <img :src="`http://127.0.0.1:9527/imagesWeb/${moduleData.userImages}`"
                   style="max-width: 300px; max-height: 300px;" />
            </template>
            <a-avatar :src="`http://127.0.0.1:9527/imagesWeb/${moduleData.userImages}`" />
          </a-popover>
          <span v-else>- -</span>
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>身份证正面：</b>
          <a-popover v-if="moduleData.idCardFrontImages" placement="top">
            <template slot="content">
              <img :src="`http://127.0.0.1:9527/imagesWeb/${moduleData.idCardFrontImages}`"
                   style="max-width: 300px; max-height: 300px;" />
            </template>
            <a-image :src="`http://127.0.0.1:9527/imagesWeb/${moduleData.idCardFrontImages}`"
                     width="50" height="50" />
          </a-popover>
          <span v-else>- -</span>
        </a-col>
        <a-col :span="8"><b>身份证反面：</b>
          <a-popover v-if="moduleData.idCardReverseImages" placement="top">
            <template slot="content">
              <img :src="`http://127.0.0.1:9527/imagesWeb/${moduleData.idCardReverseImages}`"
                   style="max-width: 300px; max-height: 300px;" />
            </template>
            <a-image :src="`http://127.0.0.1:9527/imagesWeb/${moduleData.idCardReverseImages}`"
                     width="50" height="50" />
          </a-popover>
          <span v-else>- -</span>
        </a-col>
      </a-row>
    </div>
    <br/>
  </a-modal>
</template>

<script>
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

export default {
  name: 'moduleView',
  props: {
    moduleShow: {
      type: Boolean,
      default: false
    },
    moduleData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.moduleShow
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      content: '',
      supplierInfo: null
    }
  },
  watch: {
    moduleShow: function (value) {
      if (value) {
      }
    }
  },
  methods: {
    querySupplierDetail (supplierId) {
      this.$get(`/business/supplier-info/${supplierId}`).then((r) => {
        this.supplierInfo = r.data.data
        if (this.supplierInfo.images !== null && this.supplierInfo.images !== '') {
          this.imagesInit(this.supplierInfo.images)
        }
      })
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
    picHandleChange ({fileList}) {
      this.fileList = fileList
    },
    onClose () {
      this.content = ''
      this.$emit('close')
    },
    onAudit (status) {
      let param = {
        id: this.moduleData.id,
        status: status,
        auditContent: this.content,
        supplierId: this.moduleData.supplierId
      }
      this.$put('/business/audit-info/supplierAudit', {
        ...param
      }).then((r) => {
        this.$emit('success')
      })
    }
  }
}
</script>

<style scoped>

</style>
