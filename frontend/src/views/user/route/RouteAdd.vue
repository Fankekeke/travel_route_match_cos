<template>
  <a-modal
    v-model="show"
    title="新增路线"
    @cancel="onClose"
    :width="1400"
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
      <a-row :gutter="24" class="main-content-row">
        <a-col :span="15" class="map-column">
          <div id="areas" class="map-container"></div>
          <div>
            <h4>推荐路线</h4>
            <div></div>
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
          <div class="form-container">
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
                    enter-button="选择"
                    @search="onSearch"
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
                    enter-button="选择"
                    @search="onSearch"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="24">
                <a-form-item>
                  <a-button
                    type="primary"
                    @click="handleCoordinateChange"
                    :disabled="!canCalculateRoute" style="margin-right: 10px;"
                  >
                    手动规划路线
                  </a-button>
                  <span v-if="!canCalculateRoute" style="color: #999; font-size: 12px;">
                    请先设置出发地和目的地
                  </span>
                </a-form-item>
              </a-col>
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
                <a-form-item label="类型" v-bind="formItemLayout">
                  <a-select
                    v-decorator="[
                      'type',
                      { initialValue: '0', rules: [{ required: true, message: '请选择类型' }] }
                    ]"
                    placeholder="选择路线类型"
                  >
                    <a-select-option value="0">拼座</a-select-option>
                    <a-select-option value="1">独享</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="高速费协商方式" v-bind="formItemLayout">
                  <a-select
                    v-decorator="[
                      'highwayTolls',
                      { initialValue: '0', rules: [{ required: true, message: '请选择高速费协商方式' }] }
                    ]"
                    placeholder="选择高速费协商方式"
                  >
                    <a-select-option value="0">部分协商</a-select-option>
                    <a-select-option value="1">全部承担</a-select-option>
                    <a-select-option value="2">不承担</a-select-option>
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
    routeAddVisiable: { // 修改属性名称
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.routeAddVisiable // 修改属性名称
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
    }, 200)
  },
  watch: {
    routeAddVisiable: function (val) {
      console.log(val)
      if (val) {
        setTimeout(() => {
          this.initMap()
        }, 500)
      }
    }
  },
  methods: {

    /**
     * 处理坐标字段变化
     */
    handleCoordinateChange () {
      // 检查是否满足计算路线的条件
      if (this.canCalculateRoute) {
        this.checkForRoutePlanning();
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
      this.$confirm({
        title: '选择地点',
        content: `您选择的地点是：测试地点1\n\n请选择地点：`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          // 弹出选择框，让用户选择是设置为起点还是终点
          this.showSetPointDialog('测试地点1', e.latlng.lng, e.latlng.lat)
        }
      })
      // 使用百度地图逆地理编码获取地址
      // const geocoder = new BMapGL.Geocoder()
      // geocoder.getLocation(e.latlng, (rs) => {
      //   console.log(rs)
      //   const address = rs.address
      //
      //   // 弹出对话框让用户选择设置为出发地还是目的地
      //   this.$confirm({
      //     title: '选择地点',
      //     content: `您选择的地点是：${address}\n\n请选择地点：`,
      //     okText: '确定',
      //     cancelText: '取消',
      //     onOk: () => {
      //       // 弹出选择框，让用户选择是设置为起点还是终点
      //       this.showSetPointDialog(address, e.latlng.lng, e.latlng.lat)
      //     }
      //   })
      // })
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

    queryRoutePlans (startLng, startLat, endLng, endLat) {
      this.$get(`/business/order-info/routeSet`, {
        startLongitude: startLng,
        startLatitude: startLat,
        endLongitude: endLng,
        endLatitude: endLat
      }).then((r) => {
        let routePlans = r.data
      })
    },
    calculateRoute (startLng, startLat, endLng, endLat) {
      // 清空之前的路线规划结果
      this.routePlans = []
      this.showRoutePlans = false

      // 发送请求到后端获取路线数据
      this.$get('/business/order-info/routeSet', {
        startLongitude: startLng,
        startLatitude: startLat,
        endLongitude: endLng,
        endLatitude: endLat
      }).then((r) => {
        if (r.data && Array.isArray(r.data)) {
          r.data.forEach((routeData, index) => {
            const routeInfo = {
              index: index,
              distance: (routeData.dist / 1000).toFixed(2) + '公里', // 将米转换为公里
              duration: this.formatDuration(routeData.duration), // 格式化持续时间
              policyName: routeData.tag || '平台推荐',
              policyCode: 'recommended',
              geoList: routeData.geo_list,
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
        const startIcon = new BMapGL.Icon('//api.map.baidu.com/img/markers_new.png', new BMapGL.Size(23, 25), {
          offset: new BMapGL.Size(10, 25),
          imageOffset: new BMapGL.Size(0, 0)
        })
        const endIcon = new BMapGL.Icon('//api.map.baidu.com/img/markers_new.png', new BMapGL.Size(23, 25), {
          offset: new BMapGL.Size(10, 25),
          imageOffset: new BMapGL.Size(-23, 0)
        })

        startMarker.setIcon(startIcon)
        endMarker.setIcon(endIcon)

        this.map.addOverlay(startMarker)
        this.map.addOverlay(endMarker)

        // 添加路线
        this.routePlans.forEach((plan, idx) => {
          const route = plan.routes.getRoute(0)

          // 为路线创建 Polyline 对象并设置样式
          const polyline = new BMapGL.Polyline(route.getPath(), {
            strokeColor: idx === index ? '#FF0000' : '#5555FF', // 选中的路线为红色，其他为蓝色
            strokeWeight: idx === index ? 6 : 4, // 选中的路线更粗
            strokeOpacity: idx === index ? 0.8 : 0.6 // 选中的路线更明显
          })

          this.map.addOverlay(polyline)
        })

        // 让地图适应路线视图
        this.map.setViewport(selectedPlan.routes.getRoute(0).getPath())

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

          // 处理时间字段
          if (values.earliestTime) {
            values.earliestTime = moment(values.earliestTime).format('YYYY-MM-DD HH:mm')
          }
          if (values.latestTime) {
            values.latestTime = moment(values.latestTime).format('YYYY-MM-DD HH:mm')
          }

          this.loading = true
          this.$post('/business/route-info', { // 修改API路径
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
  height: 577px;
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

/* 表单行间距 */
.a-row {
  margin-bottom: 16px;
}

.a-row:last-child {
  margin-bottom: 0;
}

.route-plans-panel {
  margin-top: 10px;
  padding: 10px;
  background: white;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
}

.route-plan-item {
  padding: 8px 12px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.route-plan-item:hover {
  border-color: #1890ff;
  background-color: #f0f8ff;
}

.route-plan-item.active {
  border-color: #1890ff;
  background-color: #e6f7ff;
}

.route-title {
  font-weight: bold;
  margin-bottom: 4px;
}

.route-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

.route-plans-panel {
  margin-top: 16px;
  padding: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 12px;
  border: 1px solid #e9ecef;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
  max-height: 450px;
  overflow-y: auto;
}

.route-plans-panel h4 {
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #1890ff;
  color: #1d3557;
  font-weight: 600;
  font-size: 16px;
}

.route-plan-item {
  padding: 12px 16px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #ffffff 0%, #fafafa 100%);
  position: relative;
  overflow: hidden;
}

.route-plan-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #1890ff;
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.route-plan-item:hover::before {
  transform: scaleY(1);
}

.route-plan-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  border-color: #1890ff;
  background: linear-gradient(135deg, #f0f8ff 0%, #e6f7ff 100%);
}

.route-plan-item.active {
  border-color: #1890ff;
  background: linear-gradient(135deg, #e6f7ff 0%, #dcf4ff 100%);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.route-plan-item.active::before {
  transform: scaleY(1);
}

.route-title {
  font-weight: 600;
  font-size: 14px;
  color: #1d3557;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.route-title::before {
  content: '📍';
  font-size: 16px;
}

.route-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #6c757d;
  padding-top: 6px;
  border-top: 1px dashed #e9ecef;
}

.distance {
  font-weight: 500;
  color: #2a9d8f;
}

.duration {
  font-weight: 500;
  color: #e76f51;
}

.route-status-indicator {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 6px;
}

.route-status-time {
  background-color: #4ecdc4;
}

.route-status-distance {
  background-color: #f4978e;
}

.route-status-avoid-highways {
  background-color: #ffd166;
}

.route-status-indicator {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 6px;
}

.route-status-time {
  background-color: #4ecdc4;
}

.route-status-distance {
  background-color: #f4978e;
}

.route-status-avoid-highways {
  background-color: #ffd166;
}

.route-plans-panel {
  margin-top: 16px;
  padding: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 12px;
  border: 1px solid #e9ecef;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
  max-height: 450px;
  overflow-y: auto;
}

.route-plans-panel::-webkit-scrollbar {
  width: 6px;
}

.route-plans-panel::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.route-plans-panel::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.route-plans-panel::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.route-title {
  font-weight: 600;
  font-size: 14px;
  color: #1d3557;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.route-title::before {
  content: '📍';
  font-size: 16px;
}

.route-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #6c757d;
  padding-top: 6px;
  border-top: 1px dashed #e9ecef;
}

.distance {
  font-weight: 500;
  color: #2a9d8f;
}

.duration {
  font-weight: 500;
  color: #e76f51;
}

.route-plan-item {
  padding: 12px 16px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #ffffff 0%, #fafafa 100%);
  position: relative;
  overflow: hidden;
}

.route-plan-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #1890ff;
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.route-plan-item:hover::before {
  transform: scaleY(1);
}

.route-plan-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  border-color: #1890ff;
  background: linear-gradient(135deg, #f0f8ff 0%, #e6f7ff 100%);
}

.route-plan-item.active {
  border-color: #1890ff;
  background: linear-gradient(135deg, #e6f7ff 0%, #dcf4ff 100%);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.route-plan-item.active::before {
  transform: scaleY(1);
}
</style>
