
<template>
  <a-modal v-model="show" title="提现记录详情" @cancel="onClose" :width="800"
           :body-style="{ padding: '0' }">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="default">
        关闭
      </a-button>
    </template>

    <div class="withdraw-detail-container" v-if="withdrawData !== null">
      <!-- 基础信息区域 -->
      <div class="info-section">
        <div class="section-header">
          <h3 class="section-title">基础信息</h3>
        </div>

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
            <span class="value amount">¥{{ withdrawData.withdrawPrice }}</span>
          </div>

          <div class="info-item">
            <span class="label">提现后余额：</span>
            <span class="value balance">¥{{ withdrawData.accountPrice }}</span>
          </div>

          <div class="info-item">
            <span class="label">申请时间：</span>
            <span class="value date">{{ withdrawData.createDate }}</span>
          </div>
        </div>
      </div>

      <!-- 附件凭证区域 -->
      <div class="attachment-section">
        <div class="section-header">
          <h3 class="section-title">附件凭证</h3>
        </div>

        <div class="attachment-content">
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

<script>import baiduMap from '@/utils/map/baiduMap'
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
      userInfo: null
    }
  },
  watch: {
    withdrawShow: function (value) {
      if (value) {
        this.imagesInit(this.withdrawData.images)
      }
    }
  },
  methods: {
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

.info-section,
.attachment-section {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.info-section:last-child,
.attachment-section:last-child {
  border-bottom: none;
}

.section-header {
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1d;
  margin: 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #1890ff;
  display: inline-block;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
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
  line-height: 1.5;
}

.info-item .amount {
  color: #f5222d;
  font-weight: 600;
  font-size: 16px;
}

.info-item .balance {
  color: #52c41a;
  font-weight: 500;
}

.info-item .date {
  color: #8c8c8c;
  font-size: 13px;
}

.attachment-content {
  background-color: #fafafa;
  padding: 16px;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-section,
  .attachment-section {
    padding: 16px;
  }

  .section-title {
    font-size: 15px;
  }
}
</style>
