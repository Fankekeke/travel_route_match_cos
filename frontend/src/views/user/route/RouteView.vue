
<template>
  <a-modal v-model="show" title="行程详情" @cancel="onClose" :width="600"
           :body-style="{ padding: '0' }">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="default">
        关闭
      </a-button>
    </template>

    <div class="route-detail-container" v-if="routeData !== null">
      <!-- 行程基本信息 -->
      <div class="info-section route-info">
        <div class="section-header">
          <h3 class="section-title">行程信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item full-width">
            <span class="label">起点：</span>
            <span class="value">{{ routeData.startAddress }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">终点：</span>
            <span class="value">{{ routeData.endAddress }}</span>
          </div>

          <div class="info-item">
            <span class="label">出发时间：</span>
            <span class="value">{{ routeData.earliestTime }}</span>
          </div>

          <div class="info-item">
            <span class="label">最晚到达：</span>
            <span class="value">{{ routeData.latestTime }}</span>
          </div>

          <div class="info-item">
            <span class="label">预估距离：</span>
            <span class="value">{{ routeData.distance ? routeData.distance.toFixed(2) + ' km' : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">服务类型：</span>
            <span class="value">
              <a-tag v-if="routeData.type === '0'" color="blue">拼座</a-tag>
              <a-tag v-if="routeData.type === '1'" color="green">独享</a-tag>
              <span v-if="!routeData.type">- -</span>
            </span>
          </div>

          <div class="info-item">
            <span class="label">乘坐人数：</span>
            <span class="value">{{ routeData.rideNum ? routeData.rideNum : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">高速费：</span>
            <span class="value">
              <a-tag v-if="routeData.highwayTolls === '0'" color="orange">部分协商</a-tag>
              <a-tag v-if="routeData.highwayTolls === '1'" color="blue">全部承担</a-tag>
              <a-tag v-if="routeData.highwayTolls === '2'" color="red">不承担</a-tag>
              <span v-if="!routeData.highwayTolls">- -</span>
            </span>
          </div>

          <div class="info-item">
            <span class="label">状态：</span>
            <span class="value">
              <a-tag v-if="routeData.status === '-1'">待接单</a-tag>
              <a-tag v-if="routeData.status === '0'" color="orange">待上车</a-tag>
              <a-tag v-if="routeData.status === '1'" color="green">已上车</a-tag>
              <a-tag v-if="routeData.status === '2'" color="blue">已送达</a-tag>
              <a-tag v-if="routeData.status === '3'" color="purple">已支付</a-tag>
              <span v-if="!routeData.status">- -</span>
            </span>
          </div>

          <div class="info-item">
            <span class="label">行程编号：</span>
            <span class="value">{{ routeData.userCode || '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">创建时间：</span>
            <span class="value date">{{ routeData.createDate }}</span>
          </div>
        </div>
      </div>

      <!-- 用户信息 -->
      <div class="info-section user-info">
        <div class="section-header">
          <h3 class="section-title">用户信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item full-width">
            <span class="label">用户姓名：</span>
            <span class="value">{{ routeData.userName }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">联系电话：</span>
            <span class="value phone">{{ routeData.userPhone }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">用户头像：</span>
            <div class="value">
              <img :size="50" :src="getImageUrl(routeData.userImages)"
                   alt="用户头像"
                   class="avatar-image"/>
            </div>
          </div>

          <div class="info-item full-width" v-if="routeData.remark">
            <span class="label">备注：</span>
            <span class="value content">{{ routeData.remark }}</span>
          </div>
        </div>
      </div>

      <!-- 路线地图 -->
      <div class="info-section map-section">
        <div class="section-header">
          <h3 class="section-title">路线地图</h3>
        </div>
        <div id="route-map" style="height: 400px; width: 100%;"></div>
      </div>
    </div>
  </a-modal>
</template>

<script>import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'RouteUserView',
  props: {
    routeShow: {
      type: Boolean,
      default: false
    },
    routeData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.routeShow
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      loading: false,
      map: null
    }
  },
  watch: {
    routeShow: function (value) {
      if (value) {
        setTimeout(() => {
          this.initRouteMap()
        }, 200)
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
      if (this.routeData.path) {
        try {
          pathCoordinates = JSON.parse(this.routeData.path)
        } catch (e) {
          console.error('解析路径数据失败:', e)
          // 如果解析失败，回退到使用起终点坐标
          pathCoordinates = [
            { latitude: this.routeData.startLatitude, longitude: this.routeData.startLongitude },
            { latitude: this.routeData.endLatitude, longitude: this.routeData.endLongitude }
          ]
        }
      } else {
        // 如果没有路径数据，使用起终点坐标
        pathCoordinates = [
          { latitude: this.routeData.startLatitude, longitude: this.routeData.startLongitude },
          { latitude: this.routeData.endLatitude, longitude: this.routeData.endLongitude }
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
    getImageUrl(imagePath) {
      if (!imagePath) {
        return '' // 替换为默认头像路径
      }

      // 如果已经是完整的URL，则直接返回
      if (imagePath.startsWith('http')) {
        return imagePath
      }

      // 按照 http://127.0.0.1:9527/imagesWeb/${text} 格式构建URL
      return `http://127.0.0.1:9527/imagesWeb/${imagePath}`
    },
    setDefaultAvatar(e) {
      e.target.src = ''
    },
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>.route-detail-container {
  padding: 0;
}

.info-section {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.route-info {
  background-color: #fafafa;
}

.user-info {
  background-color: #f9f9f9;
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

.info-item .value.phone {
  color: #1890ff;
  font-weight: 500;
}

.info-item .value.content {
  background-color: #f9f9f9;
  padding: 8px;
  border-left: 5px solid #1890ff;
  white-space: pre-wrap;
}

.avatar-image {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e8e8e8;
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
