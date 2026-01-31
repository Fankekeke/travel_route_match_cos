
<template>
  <a-modal v-model="show" title="路线详情" @cancel="onClose" :width="1000"
           :body-style="{ padding: '0' }">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="default">
        关闭
      </a-button>
    </template>

    <div class="vehicle-detail-container" v-if="vehicleData !== null">
      <!-- 路线基本信息 -->
      <div class="info-section route-info">
        <div class="section-header">
          <h3 class="section-title">路线信息</h3>
        </div>

        <div class="info-grid">
<!--          <div class="info-item">-->
<!--            <span class="label">路线编号：</span>-->
<!--            <span class="value">{{ vehicleData.staffCode }}</span>-->
<!--          </div>-->

          <div class="info-item full-width">
            <span class="label">起点：</span>
            <span class="value">{{ vehicleData.startAddress }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">终点：</span>
            <span class="value">{{ vehicleData.endAddress }}</span>
          </div>

          <div class="info-item">
            <span class="label">出发时间：</span>
            <span class="value">{{ vehicleData.earliestTime }}</span>
          </div>

          <div class="info-item">
            <span class="label">最晚到达：</span>
            <span class="value">{{ vehicleData.latestTime }}</span>
          </div>

          <div class="info-item">
            <span class="label">预估距离：</span>
            <span class="value">{{ vehicleData.distance ? vehicleData.distance.toFixed(2) + ' km' : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">计划单价：</span>
            <span class="value">{{ vehicleData.planPriceUnit ? '¥' + vehicleData.planPriceUnit.toFixed(2) : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">可乘人数：</span>
            <span class="value">{{ vehicleData.rideNum ? vehicleData.rideNum : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">状态：</span>
            <span class="value">
        <a-tag v-if="vehicleData.status === '0'" color="orange">候补中</a-tag>
        <a-tag v-if="vehicleData.status === '1'" color="green">已完成</a-tag>
        <a-tag v-if="vehicleData.status === '2'" color="gray">暂停</a-tag>
        <span v-if="!vehicleData.status">- -</span>
      </span>
          </div>

          <div class="info-item">
            <span class="label">自动接单：</span>
            <span class="value">
        <a-tag v-if="vehicleData.autoOrder === '0'" color="blue">开启</a-tag>
        <a-tag v-if="vehicleData.autoOrder === '1'" color="default">关闭</a-tag>
        <span v-if="!vehicleData.autoOrder">- -</span>
      </span>
          </div>

          <div class="info-item">
            <span class="label">创建时间：</span>
            <span class="value date">{{ vehicleData.createDate }}</span>
          </div>
        </div>
      </div>

      <!-- 车主信息 -->
      <div class="info-section driver-info">
        <div class="section-header">
          <h3 class="section-title">车主信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item full-width">
            <span class="label">车主姓名：</span>
            <span class="value">{{ vehicleData.staffName }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">联系电话：</span>
            <span class="value phone">{{ vehicleData.staffPhone }}</span>
          </div>

          <div class="info-item">
            <span class="label">车牌号：</span>
            <span class="value">{{ vehicleData.vehicleNo }}</span>
          </div>

          <div class="info-item">
            <span class="label">车辆品牌：</span>
            <span class="value">{{ vehicleData.brand }}</span>
          </div>

          <div class="info-item">
            <span class="label">车辆类型：</span>
            <span class="value">
              <span v-if="vehicleData.useType === '1'" class="use-type operational">轿车</span>
              <span v-if="vehicleData.useType === '2'" class="use-type operational">商务车</span>
              <span v-if="vehicleData.useType === '3'" class="use-type operational">大巴</span>
              <span v-if="!vehicleData.useType">- -</span>
            </span>
          </div>

          <div class="info-item full-width">
            <span class="label">备注：</span>
            <span class="value content">{{ vehicleData.remark || '- -' }}</span>
          </div>
        </div>
        <div class="map-section">
          <div class="section-header">
            <h3 class="section-title">路线地图</h3>
          </div>
          <div id="route-map" style="height: 400px; width: 100%;"></div>
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
      typeInfo: null,
      map: null
    }
  },
  watch: {
    vehicleShow: function (value) {
      if (value) {
        this.$nextTick(() => {
          this.initRouteMap()
        })
      }
    }
  },
  methods: {
    initRouteMap () {
      this.map = new BMapGL.Map('route-map')
      this.map.centerAndZoom(new BMapGL.Point(116.404, 39.915), 12)
      this.map.enableScrollWheelZoom(true)

      // 解析路径数据
      let pathCoordinates = []
      if (this.vehicleData.path) {
        try {
          pathCoordinates = JSON.parse(this.vehicleData.path)
        } catch (e) {
          console.error('解析路径数据失败:', e)
          // 如果解析失败，回退到使用起终点坐标
          pathCoordinates = [
            { latitude: this.vehicleData.startLatitude, longitude: this.vehicleData.startLongitude },
            { latitude: this.vehicleData.endLatitude, longitude: this.vehicleData.endLongitude }
          ]
        }
      } else {
        // 如果没有路径数据，使用起终点坐标
        pathCoordinates = [
          { latitude: this.vehicleData.startLatitude, longitude: this.vehicleData.startLongitude },
          { latitude: this.vehicleData.endLatitude, longitude: this.vehicleData.endLongitude }
        ]
      }

      // 添加起点标记
      if (pathCoordinates.length > 0) {
        const startPoint = new BMapGL.Point(pathCoordinates[0].longitude, pathCoordinates[0].latitude)
        // 设置标记图标
        const startIcon = new BMapGL.Icon('static/img/start.png', new BMapGL.Size(32, 32), {
          offset: new BMapGL.Size(0, 0),
          imageOffset: new BMapGL.Size(0, 0)
        })
        const startMarker = new BMapGL.Marker(startPoint)
        startMarker.setIcon(startIcon)
        this.map.addOverlay(startMarker)
      }

      // 添加终点标记
      if (pathCoordinates.length > 0) {
        const endPoint = new BMapGL.Point(
          pathCoordinates[pathCoordinates.length - 1].longitude,
          pathCoordinates[pathCoordinates.length - 1].latitude
        )
        // 设置标记图标
        const endIcon = new BMapGL.Icon('static/img/end.png', new BMapGL.Size(32, 32), {
          offset: new BMapGL.Size(0, 0),
          imageOffset: new BMapGL.Size(0, 0)
        })
        const endMarker = new BMapGL.Marker(endPoint)
        endMarker.setIcon(endIcon)
        this.map.addOverlay(endMarker)
      }

      // 将路径坐标转换为百度地图点数组
      const pathPoints = pathCoordinates.map(coord => new BMapGL.Point(coord.longitude, coord.latitude))

      // 绘制详细路线
      if (pathPoints.length > 1) {
        const polyline = new BMapGL.Polyline(pathPoints, {
          strokeColor: '#1890ff',
          strokeWeight: 6,
          strokeOpacity: 0.8
        })
        this.map.addOverlay(polyline)
      }

      // 自动调整视野以显示整个路线
      if (pathPoints.length > 0) {
        this.map.setViewport(pathPoints)
      }
    },
    local (vehicleData) {
      baiduMap.clearOverlays()
      baiduMap.rMap().enableScrollWheelZoom(true)
      // eslint-disable-next-line no-undef
      let point = new BMapGL.Point(vehicleData.longitude, vehicleData.latitude)
      baiduMap.pointAdd(point)
      baiduMap.findPoint(point, 16)
      // let driving = new BMapGL.DrivingRoute(baiduMap.rMap(), {renderOptions:{map: baiduMap.rMap(), autoViewport: true}});
      // driving.search(new BMapGL.Point(this.nowPoint.lng,this.nowPoint.lat), new BMapGL.Point(scenic.point.split(",")[0],scenic.point.split(",")[1]));
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
