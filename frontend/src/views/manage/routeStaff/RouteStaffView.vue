
<template>
  <a-modal v-model="show" title="车辆详情" @cancel="onClose" :width="1000"
           :body-style="{ padding: '0' }">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="default">
        关闭
      </a-button>
    </template>

    <div class="vehicle-detail-container" v-if="vehicleData !== null">
      <!-- 车辆基本信息 -->
      <div class="info-section vehicle-info">
        <div class="section-header">
          <h3 class="section-title">车辆信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="label">车辆编号：</span>
            <span class="value">{{ vehicleData.vehicleNo }}</span>
          </div>

          <div class="info-item">
            <span class="label">车牌号：</span>
            <span class="value">{{ vehicleData.vehicleNumber ? vehicleData.vehicleNumber : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">车辆颜色：</span>
            <span class="value">{{ vehicleData.vehicleColor ? vehicleData.vehicleColor : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">车辆名称：</span>
            <span class="value">{{ vehicleData.name }}</span>
          </div>

          <div class="info-item">
            <span class="label">排放标准：</span>
            <span class="value">{{ vehicleData.emissionStandard }}</span>
          </div>

          <div class="info-item">
            <span class="label">发动机号码：</span>
            <span class="value">{{ vehicleData.engineNo }}</span>
          </div>

          <div class="info-item">
            <span class="label">车辆品牌：</span>
            <span class="value">{{ vehicleData.brand }}</span>
          </div>

          <div class="info-item">
            <span class="label">燃料类型：</span>
            <span class="value fuel-type">
              <span v-if="vehicleData.fuelType == 1" class="fuel-label fuel-gasoline">燃油</span>
              <span v-if="vehicleData.fuelType == 2" class="fuel-label fuel-diesel">柴油</span>
              <span v-if="vehicleData.fuelType == 3" class="fuel-label fuel-hybrid">油电混动</span>
              <span v-if="vehicleData.fuelType == 4" class="fuel-label fuel-electric">电能</span>
            </span>
          </div>

          <div class="info-item">
            <span class="label">使用性质：</span>
            <span class="value">
              <span v-if="vehicleData.useType == 1" class="use-type operational">营运</span>
              <span v-if="vehicleData.useType == 2" class="use-type non-operational">非营运</span>
            </span>
          </div>

          <div class="info-item">
            <span class="label">座位数：</span>
            <span class="value">{{ vehicleData.seatNum ? vehicleData.seatNum : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">出厂日期：</span>
            <span class="value date">{{ vehicleData.factoryDate }}</span>
          </div>

          <div class="info-item">
            <span class="label">创建时间：</span>
            <span class="value date">{{ vehicleData.createDate }}</span>
          </div>
        </div>
      </div>

      <!-- 司机信息 -->
      <div class="info-section driver-info">
        <div class="section-header">
          <h3 class="section-title">司机信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="label">司机姓名：</span>
            <span class="value">{{ vehicleData.staffName }}</span>
          </div>

          <div class="info-item">
            <span class="label">联系电话：</span>
            <span class="value phone">{{ vehicleData.phone }}</span>
          </div>

          <div class="info-item">
            <span class="label">员工编号：</span>
            <span class="value">{{ vehicleData.code }}</span>
          </div>

          <div class="info-item">
            <span class="label">性别：</span>
            <span class="value">{{ vehicleData.sex === 1 ? '男' : '女' }}</span>
          </div>

          <div class="info-item">
            <span class="label">身份证号：</span>
            <span class="value id-number">{{ vehicleData.idNumber }}</span>
          </div>

          <div class="info-item">
            <span class="label">出生日期：</span>
            <span class="value date">{{ vehicleData.birthDate }}</span>
          </div>

          <div class="info-item">
            <span class="label">民族：</span>
            <span class="value">{{ vehicleData.ethnicity }}</span>
          </div>

          <div class="info-item">
            <span class="label">服务评分：</span>
            <span class="value score">{{ vehicleData.serviceScore }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">地址：</span>
            <span class="value address">{{ vehicleData.address }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">备注：</span>
            <span class="value content">{{ vehicleData.content }}</span>
          </div>
        </div>
      </div>

      <!-- 图册部分 -->
      <div class="gallery-section">
        <div class="section-header">
          <h3 class="section-title">图册</h3>
        </div>

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
  name: 'vehicleView',
  props: {
    vehicleShow: {
      type: Boolean,
      default: false
    },
    vehicleData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.vehicleShow
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
      vehicleInfo: null,
      shopInfo: null,
      brandInfo: null,
      typeInfo: null
    }
  },
  watch: {
    vehicleShow: function (value) {
      if (value) {
        this.imagesInit(this.vehicleData.images)
      }
    }
  },
  methods: {
    local (vehicleData) {
      baiduMap.clearOverlays()
      baiduMap.rMap().enableScrollWheelZoom(true)
      // eslint-disable-next-line no-undef
      let point = new BMap.Point(vehicleData.longitude, vehicleData.latitude)
      baiduMap.pointAdd(point)
      baiduMap.findPoint(point, 16)
      // let driving = new BMap.DrivingRoute(baiduMap.rMap(), {renderOptions:{map: baiduMap.rMap(), autoViewport: true}});
      // driving.search(new BMap.Point(this.nowPoint.lng,this.nowPoint.lat), new BMap.Point(scenic.point.split(",")[0],scenic.point.split(",")[1]));
    },
    dataInit (vehicleNo) {
      this.$get(`/business/vehicle-info/detail/${vehicleNo}`).then((r) => {
        this.vehicleInfo = r.data.vehicle
        this.shopInfo = r.data.shop
        this.brandInfo = r.data.brand
        this.typeInfo = r.data.type
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
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>.vehicle-detail-container {
  padding: 0;
}

.info-section {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.vehicle-info {
  background-color: #fafafa;
}

.driver-info {
  background-color: #f9f9f9;
}

.gallery-section {
  padding: 24px;
  background-color: #ffffff;
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

.info-item .value.score {
  color: #52c41a;
  font-weight: 600;
  font-size: 15px;
}

.info-item .value.phone {
  color: #1890ff;
  font-weight: 500;
}

.info-item .value.id-number {
  font-family: monospace;
  letter-spacing: 1px;
}

.fuel-label {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  text-align: center;
}

.fuel-gasoline {
  background-color: #fffbe6;
  color: #faad14;
  border: 1px solid #ffe58f;
}

.fuel-diesel {
  background-color: #fff2e8;
  color: #ff7a45;
  border: 1px solid #ffd8bf;
}

.fuel-hybrid {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.fuel-electric {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.use-type {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.operational {
  background-color: #fff1f0;
  color: #ff4d4f;
  border: 1px solid #ffa39e;
}

.non-operational {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.gallery-content {
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

  .info-item.full-width {
    grid-column: span 1;
  }

  .info-section,
  .gallery-section {
    padding: 16px;
  }

  .section-title {
    font-size: 15px;
  }
}
</style>
