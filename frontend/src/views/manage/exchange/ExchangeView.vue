
<template>
  <a-modal v-model="show" title="兑换详情" @cancel="onClose" :width="800"
           :body-style="{ padding: '0' }">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="default">
        关闭
      </a-button>
    </template>

    <div class="exchange-detail-container">
      <!-- 兑换信息区域 -->
      <div class="info-section exchange-info" v-if="exchangeData !== null">
        <div class="section-header">
          <h3 class="section-title">兑换信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="label">兑换时间：</span>
            <span class="value date">{{ exchangeData.createDate }}</span>
          </div>

          <div class="info-item">
            <span class="label">消耗积分：</span>
            <span class="value integral">{{ exchangeData.integral ? exchangeData.integral : '- -' }}</span>
          </div>
        </div>
      </div>

      <!-- 用户信息区域 -->
      <div class="info-section user-info" v-if="userInfo != null">
        <div class="section-header">
          <h3 class="section-title">用户信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="label">用户编号：</span>
            <span class="value">{{ userInfo.code }}</span>
          </div>

          <div class="info-item">
            <span class="label">用户名称：</span>
            <span class="value">{{ userInfo.name ? userInfo.name : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">联系方式：</span>
            <span class="value phone">{{ userInfo.phone ? userInfo.phone : '- -' }}</span>
          </div>
        </div>
      </div>

      <!-- 优惠券信息区域 -->
      <div class="info-section coupon-info" v-if="materialInfo != null">
        <div class="section-header">
          <h3 class="section-title">优惠券信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="label">优惠券名称：</span>
            <span class="value">{{ materialInfo.name }}</span>
          </div>

          <div class="info-item">
            <span class="label">优惠券编号：</span>
            <span class="value">{{ materialInfo.code ? materialInfo.code : '- -' }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">优惠券备注：</span>
            <span class="value content">{{ materialInfo.content ? materialInfo.content : '- -' }}</span>
          </div>
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
  name: 'exchangeView',
  props: {
    exchangeShow: {
      type: Boolean,
      default: false
    },
    exchangeData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.exchangeShow
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
      exchangeInfo: null,
      materialInfo: null
    }
  },
  watch: {
    exchangeShow: function (value) {
      if (value) {
        this.changeDetail(this.exchangeData.id)
      }
    }
  },
  methods: {
    changeDetail (id) {
      this.$get(`/business/exchange-info/${id}`).then((r) => {
        this.userInfo = r.data.user
        this.exchangeInfo = r.data.exchange
        this.materialInfo = r.data.material
        this.imagesInit(this.materialInfo.images)
      })
    },
    local (exchangeData) {
      baiduMap.clearOverlays()
      baiduMap.rMap().enableScrollWheelZoom(true)
      // eslint-disable-next-line no-undef
      let point = new BMap.Point(exchangeData.longitude, exchangeData.latitude)
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

<style scoped>.exchange-detail-container {
  padding: 0;
}

.info-section {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.exchange-info {
  background-color: #f9f9f9;
}

.user-info {
  background-color: #fafafa;
}

.coupon-info {
  background-color: #f8f9ff;
}

.info-section:last-child {
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
  gap: 16px 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}

.info-item.full-width {
  grid-column: span 2;
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

.info-item .value.date {
  color: #8c8c8c;
  font-size: 13px;
}

.info-item .value.integral {
  color: #fa8c16;
  font-weight: 600;
  font-size: 15px;
}

.info-item .value.phone {
  color: #1890ff;
  font-weight: 500;
}

.info-item .value.content {
  color: #595959;
  line-height: 1.6;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-item.full-width {
    grid-column: span 1;
  }

  .info-section {
    padding: 16px;
  }

  .section-title {
    font-size: 15px;
  }
}
</style>
