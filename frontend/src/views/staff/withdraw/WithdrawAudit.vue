<template>
  <a-modal v-model="show" title="提现记录详情" @cancel="onClose" :width="800"
           :body-style="{ padding: '0' }">
    <template slot="footer">
      <a-button key="back" @click="audit(2)" type="danger" :loading="loading">
        驳回
      </a-button>
      <a-button key="submit" @click="audit(1)" type="primary" :loading="loading">
        通过
      </a-button>
    </template>

    <div class="withdraw-detail-container" v-if="withdrawData !== null">
      <!-- 进度条区域 -->
      <div class="progress-section">
        <a-steps :current="current" size="small" class="custom-steps">
          <a-step title="已提交" />
          <a-step title="正在审核" />
          <a-step :title="currentText" />
        </a-steps>
      </div>

      <!-- 基础信息区域 -->
      <div class="info-section">
        <div class="section-title">基础信息</div>

        <div class="info-grid">
          <div class="info-item">
            <span class="label">车主编号：</span>
            <span class="value">{{ withdrawData.code }}</span>
          </div>

          <div class="info-item">
            <span class="label">车主姓名：</span>
            <span class="value">{{ withdrawData.name ? withdrawData.name : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">联系方式：</span>
            <span class="value">{{ withdrawData.phone ? withdrawData.phone : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">提现金额：</span>
            <span class="value amount">{{ withdrawData.withdrawPrice }} 元</span>
          </div>

          <div class="info-item">
            <span class="label">提现后余额：</span>
            <span class="value balance">{{ withdrawData.accountPrice }} 元</span>
          </div>

          <div class="info-item">
            <span class="label">申请时间：</span>
            <span class="value date">{{ withdrawData.createDate }}</span>
          </div>
        </div>
      </div>

      <!-- 图册区域 -->
      <div class="gallery-section">
        <div class="section-title">附件凭证</div>
        <div class="gallery-content">
          <a-upload
            name="avatar"
            action="http://127.0.0.1:9527/file/fileUpload/"
            list-type="picture-card"
            :file-list="fileList"
            @preview="handlePreview"
            @change="picHandleChange"
            :disabled="true"
          >
          </a-upload>
          <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
            <img alt="example" style="width: 100%" :src="previewImage" />
          </a-modal>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>
import baiduMap from '@/utils/map/baiduMap'
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
  name: 'withdrawView',
  props: {
    withdrawShow: {
      type: Boolean,
      default: false
    },
    withdrawData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.withdrawShow
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
      repairInfo: null,
      reserveInfo: null,
      durgList: [],
      logisticsList: [],
      userInfo: null,
      current: 0,
      currentText: '审核结果'
    }
  },
  watch: {
    withdrawShow: function (value) {
      if (value) {
        if (this.withdrawData.status == 0) {
          this.current = 1
        }
        if (this.withdrawData.status == 1) {
          this.current = 2
          this.currentText = '审核完成'
        }
        if (this.withdrawData.status == 2) {
          this.current = 2
          this.currentText = '审核驳回'
        }
        this.imagesInit(this.withdrawData.images)
      }
    }
  },
  methods: {
    audit (status) {
      let data = this.withdrawData
      data.status = status
      this.$post(`/business/order-info/auditWithdraw`, data).then((r) => {
        this.$emit('auditSuccess')
      })
    },
    local (withdrawData) {
      baiduMap.clearOverlays()
      baiduMap.rMap().enableScrollWheelZoom(true)
      // eslint-disable-next-line no-undef
      let point = new BMap.Point(withdrawData.longitude, withdrawData.latitude)
      baiduMap.pointAdd(point)
      baiduMap.findPoint(point, 16)
      // let driving = new BMap.DrivingRoute(baiduMap.rMap(), {renderOptions:{map: baiduMap.rMap(), autoViewport: true}});
      // driving.search(new BMap.Point(this.nowPoint.lng,this.nowPoint.lat), new BMap.Point(scenic.point.split(",")[0],scenic.point.split(",")[1]));
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
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>.withdraw-detail-container {
  padding: 0;
}

.progress-section {
  padding: 24px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #e8e8e8;
}

.custom-steps {
  max-width: 800px;
  margin: 0 auto;
}

.info-section {
  padding: 24px;
  background-color: #fff;
  border-bottom: 1px solid #e8e8e8;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1d;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #1890ff;
  display: inline-block;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}

.info-item .label {
  font-weight: 500;
  color: #595959;
  font-size: 13px;
  margin-bottom: 4px;
}

.info-item .value {
  font-size: 14px;
  color: #262626;
  word-break: break-all;
}

.info-item .amount {
  color: #f5222d;
  font-weight: 600;
}

.info-item .balance {
  color: #52c41a;
}

.info-item .date {
  color: #8c8c8c;
}

.gallery-section {
  padding: 24px;
  background-color: #fafafa;
}

.gallery-content {
  background-color: #fff;
  padding: 16px;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .progress-section,
  .info-section,
  .gallery-section {
    padding: 16px;
  }
}
</style>
