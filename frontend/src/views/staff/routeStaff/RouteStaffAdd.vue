<template>
  <a-modal
    v-model="show"
    @cancel="onClose"
    :width="1300"
    :bodyStyle="{ padding: 0 }"
    :footer="null"
    class="route-add-modal"
  >
    <template slot="footer">
      <a-button key="back" @click="onClose" class="cancel-btn">
        取消
      </a-button>
      <a-button
        key="submit"
        type="primary"
        :loading="loading"
        @click="handleSubmit"
        class="submit-btn"
      >
        提交
      </a-button>
    </template>

    <a-form :form="form" layout="vertical" class="main-form">
      <a-row class="main-content-row">
        <a-col :span="15" class="map-column">
          <div id="areas" class="map-container"></div>

          <div>
            <div v-if="showRoutePlans" class="route-plans-panel">
              <div
                v-for="(plan, index) in routePlans"
                :key="index"
                :class="['route-plan-item', { active: index === selectedRouteIndex }]"
                @click="selectRoute(index)"
              >
                <div class="route-title">
    <span class="route-status-indicator"
          :class="'route-status-' + plan.policyCode"></span>
                  路线 {{ index + 1 }} ({{ plan.policyName }})
                </div>
                <div class="route-info">
                  <span class="distance">📏 {{ plan.distance }}</span>
                  <span class="duration">⏱️ {{ plan.duration }}</span>
                </div>
              </div>
            </div>
          </div>
        </a-col>
        <a-col :span="9" class="form-column">
          <div class="form-container" style="padding: 60px 20px 20px 20px">
            <!-- 出发地和目的地 -->
            <a-row :gutter="16" class="start-end-container">
              <a-col :span="12">
                <a-form-item label="出发地" v-bind="formItemLayout">
                  <a-input-search
                    v-decorator="[
        'startAddress',
        { rules: [{ required: true, message: '请输入出发地' }] }
      ]"
                    placeholder="点击地图选择出发地"
                    :enterButton="false"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="目的地" v-bind="formItemLayout">
                  <a-input-search
                    v-decorator="[
        'endAddress',
        { rules: [{ required: true, message: '请输入目的地' }] }
      ]"
                    placeholder="点击地图选择目的地"
                    :enterButton="false"
                  />
                </a-form-item>
              </a-col>
              <!--              <a-col :span="24">-->
              <!--                <a-form-item>-->
              <!--                  <a-button-->
              <!--                    type="primary"-->
              <!--                    @click="handleCoordinateChange"-->
              <!--                    :disabled="!canCalculateRoute" style="margin-right: 10px;"-->
              <!--                  >-->
              <!--                    手动规划路线-->
              <!--                  </a-button>-->
              <!--                  <span v-if="!canCalculateRoute" style="color: #999; font-size: 12px;">-->
              <!--                    请先设置出发地和目的地-->
              <!--                  </span>-->
              <!--                </a-form-item>-->
              <!--              </a-col>-->
            </a-row>

            <!-- 时间选择 -->
            <a-row :gutter="16" class="time-container">
              <a-col :span="12">
                <a-form-item label="最早出发时间" v-bind="formItemLayout">
                  <a-date-picker
                    v-decorator="['earliestTime']"
                    placeholder="选择最早出发时间"
                    format="YYYY-MM-DD HH:mm"
                    show-time                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="最迟出发时间" v-bind="formItemLayout">
                  <a-date-picker
                    v-decorator="['latestTime']"
                    placeholder="选择最迟出发时间"
                    format="YYYY-MM-DD HH:mm"
                    show-time                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <!-- 距离和人数 -->
            <a-row :gutter="16" class="distance-passenger-container">
              <a-col :span="12">
                <a-form-item label="路线距离(km)" v-bind="formItemLayout">
                  <a-input-number
                    v-decorator="['distance', { rules: [{ pattern: /^\d+(\.\d{1,2})?$/, message: '请输入正确的距离值' }] }]"
                    placeholder="请输入路线距离"                    style="width: 100%"
                    :precision="2"
                    :step="0.1"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="乘坐人数" v-bind="formItemLayout">
                  <a-input-number
                    v-decorator="['rideNum', { rules: [{ required: true, message: '请输入乘坐人数' }] }]"
                    :min="1"
                    :max="50"
                    placeholder="请输入乘坐人数"                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <!-- 类型和费用 -->
            <a-row :gutter="16" class="type-tolls-container">
              <a-col :span="12">
                <a-form-item label="是否自动接单" v-bind="formItemLayout">
                  <a-select
                    v-decorator="[
                      'autoOrder',
                      { initialValue: '0', rules: [{ required: true, message: '请选择是否自动接单' }] }
                    ]"
                    placeholder="是否自动接单"
                  >
                    <a-select-option value="0">否</a-select-option>
                    <a-select-option value="1">是</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="驾驶车辆" v-bind="formItemLayout">
                  <a-select
                    v-decorator="[
                    'vehicleId',
                    { rules: [{ required: true, message: '请选择车辆' }] }
                  ]"
                    placeholder="选择车辆"
                  >
                    <a-select-option v-for="item in vehicleList" :key="item.id" :value="item.id">
                      {{ item.vehicleNo }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <!-- 备注信息 -->
            <a-form-item label="备注信息" v-bind="formItemLayout">
              <a-textarea
                v-decorator="['remark']"
                placeholder="请输入备注信息"
                :rows="4"
              />
            </a-form-item>
            <div>
              <div class="map-controls">
                <a-button @click="onClose" class="control-btn cancel-btn">取消</a-button>
                <a-button type="primary" @click="handleSubmit" :loading="loading" class="control-btn submit-btn">提交</a-button>
              </div>
            </div>

            <!-- 隐藏字段 -->
            <a-form-item class="hidden-field">
              <a-input
                v-decorator="['startLongitude']"
                @change="handleCoordinateChange"
              />
            </a-form-item>
            <a-form-item class="hidden-field">
              <a-input
                v-decorator="['startLatitude']"
                @change="handleCoordinateChange"
              />
            </a-form-item>
            <a-form-item class="hidden-field">
              <a-input
                v-decorator="['endLongitude']"
                @change="handleCoordinateChange"
              />
            </a-form-item>
            <a-form-item class="hidden-field">
              <a-input
                v-decorator="['endLatitude']"
                @change="handleCoordinateChange"
              />
            </a-form-item>
          </div>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import {mapState} from 'vuex'
import conversion from '@/utils/conversion'
import moment from 'moment'
moment.locale('zh-cn')

const BMAP_DRIVING_POLICY_LEAST_TIME = 0 // 最少时间
const BMAP_DRIVING_POLICY_LEAST_DISTANCE = 1 // 最短距离
const BMAP_DRIVING_POLICY_AVOID_HIGHWAYS = 2 // 避开高速

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
  name: 'routeAdd', // 修改组件名称
  props: {
    routeStaffAddVisiable: { // 修改属性名称
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.routeStaffAddVisiable // 修改属性名称
      },
      set: function () {
      }
    },
    canCalculateRoute () {
      const formValues = this.form.getFieldsValue()
      return formValues.startLongitude &&
        formValues.startLatitude &&
        formValues.endLongitude &&
        formValues.endLatitude
    }
  },
  data () {
    return {
      formItemLayout,
      map: null,
      mapId: 'areas',
      form: this.$form.createForm(this),
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      pharmacyList: [],
      shopList: [],
      vehicleTypeList: [],
      vehicleList: [],
      brandList: [],
      selectedPointType: 'start',
      routePlanService: null, // 路线规划服务实例
      routePlans: [], // 存储路线规划结果
      selectedRouteIndex: 0, // 选中的路线索引
      showRoutePlans: false, // 是否显示路线规划结果
      selectedStartPoint: null, // 选择的起点
      selectedEndPoint: null // 选择的终点
    }
  },
  mounted () {
    setTimeout(() => {
      this.initMap()
      this.queryVehicleByStaff()
    }, 200)
  },
  watch: {
    routeStaffAddVisiable: function (val) {
      console.log(val)
      if (val) {
        setTimeout(() => {
          this.initMap()
        }, 500)
      }
    }
  },
  methods: {
    queryVehicleByStaff () {
      this.$get('/business/vehicle-info/queryVehicleByStaff', {
        userId: this.currentUser.userId
      }).then((r) => {
        this.vehicleList = r.data.data || []
      })
    },
    /**
     * 处理坐标字段变化
     */
    handleCoordinateChange () {
      // 检查是否满足计算路线的条件
      if (this.canCalculateRoute) {
        this.checkForRoutePlanning()
      }
    },
    onSearch (value) {
      // 搜索地址并定位到地图上
      const local = new BMapGL.LocalSearch(this.map, {
        onSearchComplete: (results) => {
          if (results && results.getNumPois()) {
            const point = results.getPoi(0).point
            this.map.centerAndZoom(point, 15)

            // 可以考虑添加标记并让用户确认设置为出发地或目的地
            this.map.clearOverlays()
            const marker = new BMapGL.Marker(point)
            this.map.addOverlay(marker)

            // 弹出对话框让用户选择设置为出发地还是目的地
            this.showSetPointDialog(results.getPoi(0).title, point.lng, point.lat)
          }
        }
      })
      local.search(value)
    },
    /**
     * 初始化地图
     */
    initMap () {
      this.map = new BMapGL.Map(this.mapId)
      this.map.centerAndZoom(new BMapGL.Point(116.404, 39.915), 12)
      this.map.enableScrollWheelZoom(true)
      // this.map.setDisplayOptions({poiIcon: false})
      this.addMapEventListeners() // 添加地图事件监听
    },

    /**
     * 添加地图事件监听
     */
    addMapEventListeners () {
      // 地图点击事件，用于选择地点
      this.map.addEventListener('click', (e) => {
        this.handleMapClick(e)
      })
    },

    /**
     * 显示设置地点对话框
     */
    showSetPointDialog (address, longitude, latitude) {
      const h = this.$createElement

      // 创建一个临时的内部状态变量
      let tempSelectedType = 'start'

      this.$confirm({
        title: '设置地点',
        content: h('div', [
          h('p', '请选择要设置的地点类型：'),
          h('a-radio-group', {
            props: {
              defaultValue: 'start'
            },
            on: {
              change: (e) => {
                tempSelectedType = e.target.value
              }
            }
          }, [
            h('a-radio', { props: { value: 'start' } }, '出发地'),
            h('a-radio', { props: { value: 'end' } }, '目的地')
          ])
        ]),
        onOk: () => {
          // 使用临时变量的值
          this.setPoint(address, longitude, latitude, tempSelectedType)
        }
      })
    },

    /**
     * 处理地图点击事件
     */
    handleMapClick (e) {
      console.log(e)
      // this.$confirm({
      //   title: '选择地点',
      //   content: `您选择的地点是：测试地点1\n\n请选择地点：`,
      //   okText: '确定',
      //   cancelText: '取消',
      //   onOk: () => {
      //     // 弹出选择框，让用户选择是设置为起点还是终点
      //     this.showSetPointDialog('测试地点1', e.latlng.lng, e.latlng.lat)
      //   }
      // })
      // 使用百度地图逆地理编码获取地址
      const geocoder = new BMapGL.Geocoder()
      geocoder.getLocation(e.latlng, (rs) => {
        console.log(rs)
        const address = rs.address

        // 弹出对话框让用户选择设置为出发地还是目的地
        this.$confirm({
          title: '选择地点',
          content: `您选择的地点是：${address}\n\n请选择地点：`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            // 弹出选择框，让用户选择是设置为起点还是终点
            this.showSetPointDialog(address, e.latlng.lng, e.latlng.lat)
          }
        })
      })
    },

    /**
     * 设置地点信息
     */
    setPoint (address, longitude, latitude, pointType) {
      if (pointType === 'start') {
        this.form.setFieldsValue({
          startAddress: address,
          startLongitude: longitude,
          startLatitude: latitude
        })
      } else if (pointType === 'end') {
        this.form.setFieldsValue({
          endAddress: address,
          endLongitude: longitude,
          endLatitude: latitude
        })
      }

      // 使用 $nextTick 确保 DOM 更新后再检查路线规划
      this.$nextTick(() => {
        this.checkForRoutePlanning()
      })
    },

    checkForRoutePlanning () {
      // 获取表单中的经纬度值
      const formValues = this.form.getFieldsValue()
      const hasStart = formValues.startLongitude !== undefined &&
        formValues.startLatitude !== undefined &&
        formValues.startLongitude !== '' &&
        formValues.startLatitude !== ''
      const hasEnd = formValues.endLongitude !== undefined &&
        formValues.endLatitude !== undefined &&
        formValues.endLongitude !== '' &&
        formValues.endLatitude !== ''

      if (hasStart && hasEnd) {
        this.calculateRoute(
          parseFloat(formValues.startLongitude),
          parseFloat(formValues.startLatitude),
          parseFloat(formValues.endLongitude),
          parseFloat(formValues.endLatitude)
        )
      }
    },
    calculateRoute (startLng, startLat, endLng, endLat) {
      // 清空之前的路线规划结果
      this.routePlans = []
      this.showRoutePlans = false

      const converStart = conversion.baiduToChina(startLat, startLng)
      const converEnd = conversion.baiduToChina(endLat, endLng)
      // 发送请求到后端获取路线数据
      this.$get('/business/order-info/routeSet', {
        startLongitude: converStart.lng,
        startLatitude: converStart.lat,
        endLongitude: converEnd.lng,
        endLatitude: converEnd.lat
      }).then((r) => {
        if (r.data.data && Array.isArray(r.data.data)) {
          r.data.data.forEach((routeData, index) => {
            // 转换 geo_list 中的经纬度坐标系
            const convertedGeoList = routeData.geo_list.map(coord => {
              const convertedCoord = conversion.chinaToBaidu(coord.latitude, coord.longitude)
              return {
                latitude: convertedCoord.lat,
                longitude: convertedCoord.lng
              }
            })

            const routeInfo = {
              index: index,
              distance: (routeData.dist / 1000).toFixed(2) + '公里', // 将米转换为公里
              duration: this.formatDuration(routeData.duration), // 格式化持续时间
              policyName: routeData.tag || '平台推荐',
              policyCode: 'recommended',
              geoList: convertedGeoList, // 使用转换后的坐标列表
              routes: routeData
            }
            this.routePlans.push(routeInfo)
          })

          if (this.routePlans.length > 0) {
            this.showRoutePlans = true
            this.selectRoute(0) // 默认选择第一条路线
          }
        }
      }).catch((error) => {
        console.error('获取路线数据失败:', error)
        // 可以在这里添加错误提示给用户
        this.$message.error('获取路线数据失败，请重试')
      })
    },

    formatDuration (seconds) {
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)

      if (hours > 0) {
        return `${hours}小时${minutes}分钟`
      } else {
        return `${minutes}分钟`
      }
    },

    selectRoute (index) {
      if (this.routePlans[index]) {
        this.selectedRouteIndex = index
        const selectedPlan = this.routePlans[index]

        // 清除之前绘制的路线和标记
        this.map.clearOverlays()

        // 添加起点和终点标记
        const formValues = this.form.getFieldsValue()
        const startMarker = new BMapGL.Marker(new BMapGL.Point(
          parseFloat(formValues.startLongitude),
          parseFloat(formValues.startLatitude)
        ))
        const endMarker = new BMapGL.Marker(new BMapGL.Point(
          parseFloat(formValues.endLongitude),
          parseFloat(formValues.endLatitude)
        ))

        // 设置标记图标
        const startIcon = new BMapGL.Icon('static/img/start.png', new BMapGL.Size(32, 32), {
          offset: new BMapGL.Size(0, 0),
          imageOffset: new BMapGL.Size(0, 0)
        })
        const endIcon = new BMapGL.Icon('static/img/end.png', new BMapGL.Size(32, 32), {
          offset: new BMapGL.Size(0, 0),
          imageOffset: new BMapGL.Size(0, 0)
        })

        startMarker.setIcon(startIcon)
        endMarker.setIcon(endIcon)

        this.map.addOverlay(startMarker)
        this.map.addOverlay(endMarker)

        // 使用 geo_list 数据绘制路线
        if (selectedPlan.geoList && Array.isArray(selectedPlan.geoList) && selectedPlan.geoList.length > 0) {
          // 将 geo_list 转换为百度地图的 Point 数组
          const pathPoints = selectedPlan.geoList.map(coord =>
            new BMapGL.Point(coord.longitude, coord.latitude)
          )

          // 创建 Polyline 对象并设置样式
          const polyline = new BMapGL.Polyline(pathPoints, {
            strokeColor: '#cccaca', // 改为蓝色
            strokeWeight: 8, // 稍微加粗作为边框
            strokeOpacity: 0.6, // 边框透明度
            strokeStyle: 'solid' // 实线边框
          })

          // 创建内部细线
          const innerLine = new BMapGL.Polyline(pathPoints, {
            strokeColor: '#0b89d5', // 白色细线
            strokeWeight: 4, // 内部细线较细
            strokeOpacity: 1, // 不透明
            strokeStyle: 'dashed', // 虚线样式
            strokeDasharray: '30,3' // 虚线间隔模式：实线6px，空白4px
          })

          this.map.addOverlay(polyline) // 先添加边框
          this.map.addOverlay(innerLine) // 后添加内部线，使其显示在上面

          this.map.addOverlay(polyline)

          // 让地图适应路线视图
          this.map.setViewport(pathPoints)
        }

        // 更新距离字段
        this.form.setFieldsValue({
          distance: parseFloat(selectedPlan.distance.replace('公里', ''))
        })
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
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        // 获取图片List
        let images = []
        this.fileList.forEach(image => {
          images.push(image.response)
        })
        if (!err) {
          values.images = images.length > 0 ? images.join(',') : null

          // 添加路径信息
          if (this.routePlans.length > 0 && this.selectedRouteIndex >= 0) {
            const selectedRoute = this.routePlans[this.selectedRouteIndex]
            // 将选中路线的geoList转换为字符串格式存储
            values.path = JSON.stringify(selectedRoute.geoList || [])
          }

          // 处理时间字段
          if (values.earliestTime) {
            values.earliestTime = moment(values.earliestTime).format('YYYY-MM-DD HH:mm')
          }
          if (values.latestTime) {
            values.latestTime = moment(values.latestTime).format('YYYY-MM-DD HH:mm')
          }
          values.staffId = this.currentUser.userId
          this.loading = true
          this.$post('/business/route-staff-info', { // 修改API路径
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
<style scoped>/* 整体容器样式 */
.a-modal-wrap .a-modal {
  max-width: 100%;
  margin: 20px auto;
  border-radius: 8px;
}

/* 取消模态框内边框 */
.a-modal-content {
  border: none !important;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 模态框主体无边框 */
.a-modal-body {
  padding: 0px;
  border: none;
}

/* 标题栏样式优化 */
.a-modal-header {
  border: none;
  border-bottom: 1px solid #e8e8e8;
  padding: 16px 24px;
  border-radius: 8px 8px 0 0;
}

/* 底部按钮栏样式优化 */
.a-modal-footer {
  border: none;
  border-top: 1px solid #e8e8e8;
  padding: 16px 24px;
  background-color: #fafafa;
  border-radius: 0 0 8px 8px;
}

/* 地图容器样式 */
#areas {
  width: 100%;
  height: 523px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e8e8e8;
}

/* 表单区域样式 */
.form-container {
  padding: 16px;
  background-color: #fafafa;
  border-radius: 8px;
}

/* 表单项样式 */
.a-form-item {
  margin-bottom: 16px;
}

.a-form-item-label {
  font-weight: 500;
  color: #2c3e50;
}

/* 输入框样式 */
.a-input,
.a-input-number,
.a-select,
.a-textarea {
  border-radius: 6px;
  border: 1px solid #d9d9d9;
  transition: all 0.3s;
}

.a-input:hover,
.a-input-number:hover,
.a-select:hover,
.a-textarea:hover {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

/* 日期选择器样式 */
.a-date-picker {
  width: 100%;
}

/* 按钮样式 */
.a-btn-primary {
  background-color: #1890ff;
  border-color: #1890ff;
  border-radius: 6px;
  font-weight: 500;
}

.a-btn-primary:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

.a-btn {
  border-radius: 6px;
}

/* 隐藏表单项样式 */
.hidden-field {
  display: none;
}

/* 表单标题样式 */
.a-form-item-label {
  font-size: 14px;
  line-height: 1.5;
  padding-bottom: 6px;
}

/* 响应式优化 */
@media (max-width: 768px) {
  #areas {
    height: 300px;
  }

  .a-col {
    margin-bottom: 12px;
  }
}

/* 特定表单项样式 */
/*.start-end-container {*/
/*  display: flex;*/
/*  gap: 10px;*/
/*}*/

/*.time-container {*/
/*  display: flex;*/
/*  gap: 10px;*/
/*}*/

/*.distance-passenger-container {*/
/*  display: flex;*/
/*  gap: 10px;*/
/*}*/

/*.type-tolls-container {*/
/*  display: flex;*/
/*  gap: 10px;*/
/*}*/
/* 路线规划面板样式 */
.route-plans-panel {
  margin-top: 18px;
  bottom: 20px;
  left: 20px;
  right: 20px;
  border-radius: 3px;
  backdrop-filter: blur(10px);
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
}

.route-plans-panel::-webkit-scrollbar {
  width: 6px;
}

.route-plans-panel::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.route-plans-panel::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.route-plan-item {
  width: 95%;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 10px;
  border: 2px solid #e8e8e8;
  border-radius: 2px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.route-plan-item.active {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-color: #1890ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
}

.route-title {
  display: flex;
  align-items: center;
  font-weight: 600;
  color: #2c3e50;
  font-size: 15px;
}

.route-status-indicator {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 10px;
  border: 2px solid transparent;
}

.route-status-recommended {
  background-color: #52c41a;
}

.route-status-fastest {
  background-color: #1890ff;
}

.route-status-shortest {
  background-color: #faad14;
}

.route-status-avoid_highways {
  background-color: #722ed1;
}

.route-info {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

.route-info .distance,
.route-info .duration {
  display: flex;
  align-items: center;
  gap: 3px;
}
</style>
