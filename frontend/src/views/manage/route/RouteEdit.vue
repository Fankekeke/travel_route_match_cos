
<template>
  <a-modal v-model="show" title="新增路线" @cancel="onClose" :width="1300">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        提交
      </a-button>
    </template>
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">
        <a-col :span="7">
          <div id="areas" style="width: 100%;height: 100vh;color:#fff"></div>
        </a-col>
        <a-col :span="17">
          <a-row :gutter="10">
            <a-col :span="12">
              <a-form-item label="出发地" v-bind="formItemLayout">
                <a-input
                  v-decorator="[
                    'startAddress',
                    { rules: [{ required: true, message: '请输入出发地' }] }
                  ]"
                  placeholder="点击地图选择出发地"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="目的地" v-bind="formItemLayout">
                <a-input
                  v-decorator="[
                    'endAddress',
                    { rules: [{ required: true, message: '请输入目的地' }] }
                  ]"
                  placeholder="点击地图选择目的地"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="10">
            <a-col :span="12">
              <a-form-item label="最早出发时间" v-bind="formItemLayout">
                <a-date-picker
                  v-decorator="['earliestTime']"
                  placeholder="选择最早出发时间"
                  format="YYYY-MM-DD HH:mm"
                  show-time        style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="最迟出发时间" v-bind="formItemLayout">
                <a-date-picker
                  v-decorator="['latestTime']"
                  placeholder="选择最迟出发时间"
                  format="YYYY-MM-DD HH:mm"
                  show-time        style="width: 100%"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="10">
            <a-col :span="12">
              <a-form-item label="路线距离(km)" v-bind="formItemLayout">
                <a-input-number
                  v-decorator="['distance', { rules: [{ pattern: /^\d+(\.\d{1,2})?$/, message: '请输入正确的距离值' }] }]"
                  placeholder="请输入路线距离"                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="乘坐人数" v-bind="formItemLayout">
                <a-input-number
                  v-decorator="['rideNum', { rules: [{ required: true, message: '请输入乘坐人数' }] }]"
                  :min="1"
                  :max="50"
                  placeholder="请输入乘坐人数"                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="10">
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

          <a-form-item label="备注信息" v-bind="formItemLayout">
            <a-textarea
              v-decorator="['remark']"
              placeholder="请输入备注信息"
              :rows="4"
            />
          </a-form-item>

          <!-- 隐藏字段，用于存储经纬度 -->
          <a-form-item style="display: none;">
            <a-input v-decorator="['startLongitude']"/>
          </a-form-item>
          <a-form-item style="display: none;">
            <a-input v-decorator="['startLatitude']"/>
          </a-form-item>
          <a-form-item style="display: none;">
            <a-input v-decorator="['endLongitude']"/>
          </a-form-item>
          <a-form-item style="display: none;">
            <a-input v-decorator="['endLatitude']"/>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import {mapState} from 'vuex'
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

const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}

export default {
  name: 'routeAdd',  // 修改组件名称
  props: {
    routeEditVisiable: {  // 修改属性名称
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.routeEditVisiable  // 修改属性名称
      },
      set: function () {
      }
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
      selectedStartPoint: null,  // 选择的起点
      selectedEndPoint: null      // 选择的终点
    }
  },
  mounted () {
  },
  methods: {
    /**
     * 初始化地图
     */
    initMap() {
      this.map = new BMapGL.Map(this.mapId);
      this.map.centerAndZoom(new BMapGL.Point(116.404, 39.915), 12);
      this.map.enableScrollWheelZoom(true);
      this.map.setDisplayOptions({poiIcon: false});
    },

    /**
     * 添加地图事件监听
     */
    addMapEventListeners() {
      // 地图点击事件，用于选择地点
      this.map.addEventListener('click', (e) => {
        this.handleMapClick(e);
      });
    },

    /**
     * 处理地图点击事件
     */
    handleMapClick(e) {
      // 使用百度地图逆地理编码获取地址
      const geocoder = new BMapGL.Geocoder();
      geocoder.getLocation(e.point, (rs) => {
        const address = rs.address;

        // 这里可以根据用户交互决定是设置起点还是终点
        // 比如可以弹出对话框询问用户
        this.$confirm({
          title: '选择地点',
          content: `您选择的地点是：${address}\n\n是否设置为起点或终点？`,
          onOk: () => {
            // 可以添加逻辑让用户选择设置为起点还是终点
            this.setPoint(address, e.point.lng, e.point.lat);
          }
        });
      });
    },

    /**
     * 设置地点信息
     */
    setPoint(address, longitude, latitude) {
      // 示例：设置起点信息
      this.form.setFieldsValue({
        startAddress: address,
        startLongitude: longitude,
        startLatitude: latitude
      });

      // 如果需要设置终点，可以类似操作
      // this.form.setFieldsValue({
      //   endAddress: address,
      //   endLongitude: longitude,
      //   endLatitude: latitude
      // });
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
    setFormValues ({...vehicle}) {
      this.rowId = vehicle.id
      this.initMap();
      this.addMapEventListeners();  // 添加地图事件监听
      let fields = [
        'path',
        'distance',
        'startLongitude',
        'startLatitude',
        'endLongitude',
        'endLatitude',
        'startAddress',
        'endAddress',
        'earliestTime',
        'latestTime',
        'type',
        'rideNum',
        'remark',
        'highwayTolls'
      ]
      let obj = {}
      Object.keys(route).forEach((key) => {
        if ((key === 'earliestTime' || key === 'latestTime') && route[key] != null) {
          route[key] = moment(route[key], 'YYYY-MM-DD HH:mm')
        }
        if (key === 'images') {
          this.fileList = []
          this.imagesInit(route['images'])
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = route[key]
        }
      })
      this.form.setFieldsValue(obj)
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
          this.$post('/business/route-info', {  // 修改API路径
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

<style scoped>

</style>
