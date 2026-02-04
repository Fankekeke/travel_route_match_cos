<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="车牌号码"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.vehicleNo"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="所属车主"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.staffName"/>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
<!--        <a-button type="primary" ghost @click="add">新增</a-button>-->
        <a-button @click="batchDelete">删除</a-button>
      </div>
      <!-- 表格区域 -->
      <a-table bordered  ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="titleShow" slot-scope="text, record">
          <template>
            <a-tooltip>
              <template slot="title">
                {{ record.title }}
              </template>
              {{ record.title.slice(0, 8) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon
            v-if="record.status === '-1'"
            type="stop"
            @click="cancelOrder(record)"
            title="取 消 订 单"
            style="margin-left: 15px; color: #ff4d4f;"
          ></a-icon>
          <a-icon v-if="record.status == 2" type="alipay" @click="handlevehiclePayOpen(record)" title="支 付" style="margin-left: 15px"></a-icon>
          <a-icon type="file-search" @click="handlevehicleViewOpen(record)" title="详 情" style="margin-left: 15px"></a-icon>
          <a-icon
            v-if="record.status === '3' && !record.evaluateId"
            type="star"
            @click="handleEvaluateOpen(record)"
            title="评 价"  style="margin-left: 15px; color: #1890ff;"
          ></a-icon>
          <a-icon
            v-if="record.status === '3' && !record.complaintId"
            type="exclamation-circle"
            @click="handleComplaintOpen(record)"
            title="投 诉"  style="margin-left: 15px; color: #ff4d4f;"
          ></a-icon>
        </template>
      </a-table>
    </div>
    <vehicle-add
      v-if="vehicleAdd.visiable"
      @close="handlevehicleAddClose"
      @success="handlevehicleAddSuccess"
      :vehicleAddVisiable="vehicleAdd.visiable">
    </vehicle-add>
    <vehicle-edit
      ref="vehicleEdit"
      @close="handlevehicleEditClose"
      @success="handlevehicleEditSuccess"
      :vehicleEditVisiable="vehicleEdit.visiable">
    </vehicle-edit>
    <vehicle-view
      @close="handlevehicleViewClose"
      :orderShow="vehicleView.visiable"
      :orderData="vehicleView.data">
    </vehicle-view>
    <order-pay
      @close="handlevehiclePayClose"
      :orderShow="vehiclePay.visiable"
      :orderData="vehiclePay.data">
    </order-pay>
    <order-evaluate
      :evaluate-visible.sync="evaluateModal.visible"
      :order-data="evaluateModal.data"
      @success="handleEvaluateSuccess"
    />
    <order-complaint
      :complaint-visible.sync="complaintModal.visible"
      :order-data="complaintModal.data"
      @success="handleComplaintSuccess"
    />
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import vehicleAdd from './OrderAdd.vue'
import vehicleEdit from './OrderEdit.vue'
import vehicleView from './OrderView.vue'
import OrderPay from './OrderPay.vue'
import {mapState} from 'vuex'
import OrderEvaluate from './OrderEvaluate.vue'
import OrderComplaint from './OrderComplaint.vue'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'vehicle',
  components: {vehicleAdd, vehicleEdit, vehicleView, OrderPay, OrderEvaluate, OrderComplaint, RangeDate},
  data () {
    return {
      advanced: false,
      evaluateModal: {
        visible: false,
        data: null
      },
      complaintModal: {
        visible: false,
        data: null
      },
      vehicleAdd: {
        visiable: false
      },
      vehicleEdit: {
        visiable: false
      },
      vehicleView: {
        visiable: false,
        data: null
      },
      vehiclePay: {
        visiable: false,
        data: null
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      brandList: []
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '订单名称',
        dataIndex: 'code',
        customRender: (text, record, index) => {
          if (!text) return '- -'
          return (
            <div style="display: flex; align-items: center;">
              <div>
                <a-tooltip title={text}>
                  <div style="max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                    {text}
                  </div>
                </a-tooltip>
                <a-tooltip title={record.orderName}>
                  <div style="color: #999; font-size: 12px; max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                    {record.orderName}
                  </div>
                </a-tooltip>
              </div>
            </div>
          )
        },
        width: 200
      }, {
        title: '乘客姓名',
        dataIndex: 'userName',
        customRender: (text, record, index) => {
          if (!text) return '- -'
          return (
            <div style="display: flex; align-items: center;">
              <a-avatar
                size="72"
                src={ record.userImages ? 'http://127.0.0.1:9527/imagesWeb/' + record.userImages : null }
                icon={ record.userImages ? null : 'user' }
                style="margin-right: 15px;"
              />
              <div>
                <div>{text}</div>
                <div style="color: #999; font-size: 12px;">{record.userPhone}</div>
              </div>
            </div>
          )
        },
        width: 250
      }, {
        title: '车主姓名',
        dataIndex: 'staffName',
        customRender: (text, record, index) => {
          if (!text) return '- -'
          return (
            <div style="display: flex; align-items: center;">
              <a-avatar
                size="72"
                src={ record.staffImages ? 'http://127.0.0.1:9527/imagesWeb/' + record.staffImages : null }
                icon={ record.staffImages ? null : 'user' }
                style="margin-right: 15px;"
              />
              <div>
                <div>{text}</div>
                <div style="color: #999; font-size: 12px;">{record.staffPhone}</div>
              </div>
            </div>
          )
        },
        width: 250,
        ellipsis: true
      }, {
        title: '订单状态',
        dataIndex: 'status',
        width: 150,
        customRender: (text, row, index) => {
          switch (text) {
            case '-1':
              return <a-tag color="orange">待接单</a-tag>
            case '0':
              return <a-tag color="blue">已确认</a-tag>
            case '1':
              return <a-tag color="green">已接客</a-tag>
            case '2':
              return <a-tag color="cyan">已送达</a-tag>
            case '3':
              return <a-tag color="purple">已支付</a-tag>
            case '4':
              return <a-tag color="red">已拒绝</a-tag>
            case '5':
              return <a-tag>已取消</a-tag>
            default:
              return <a-tag>未知状态</a-tag>
          }
        }
      }, {
        title: '车牌号码',
        dataIndex: 'vehicleNo',
        width: 150,
        ellipsis: true
      }, {
        title: '车辆品牌',
        dataIndex: 'brand',
        width: 150,
        ellipsis: true
      }, {
        title: '车辆类型',
        dataIndex: 'useType',
        width: 150,
        customRender: (text, row, index) => {
          switch (text) {
            case '1':
              return <a-tag>轿车</a-tag>
            case '2':
              return <a-tag>商务车</a-tag>
            case '3':
              return <a-tag>大巴</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '订单金额',
        dataIndex: 'orderPrice',
        width: 150,
        customRender: (text, row, index) => {
          if (text !== null) {
            return `¥${text}`
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '里程(km)',
        dataIndex: 'kilometre',
        width: 150,
        customRender: (text, row, index) => {
          if (text !== null) {
            return `${text} km`
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '乘客人数',
        dataIndex: 'rideNum',
        width: 150,
        customRender: (text, row, index) => {
          if (text !== null) {
            return `${text} 人`
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '创建时间',
        dataIndex: 'createDate',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        width: 150,
        ellipsis: true
      }, {
        title: '操作',
        dataIndex: 'operation',
        width: 100,
        fixed: 'right',
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    handleComplaintOpen(record) {
      this.complaintModal.data = record
      this.complaintModal.visible = true
    },
    handleComplaintClose() {
      this.complaintModal.visible = false
    },
    handleComplaintSuccess() {
      this.complaintModal.visible = false
      this.$message.success('投诉成功')
      this.fetch() // 刷新数据
    },
    handleEvaluateOpen(record) {
      this.evaluateModal.data = record
      this.evaluateModal.visible = true
    },
    handleEvaluateClose() {
      this.evaluateModal.visible = false
    },
    handleEvaluateSuccess() {
      this.evaluateModal.visible = false
      this.$message.success('评价成功')
      this.fetch() // 刷新数据
    },
    cancelOrder (row) {
      this.$confirm({
        title: '确认取消订单',
        content: '您确定要取消此订单吗？此操作不可撤销。',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          this.$get('/business/order-info/cancelOrder', { orderId: row.id }).then((r) => {
            this.$message.success('取消成功')
            this.fetch()
          }).catch(error => {
            this.$message.error('取消失败，请稍后再试')
          })
        },
        onCancel: () => {
          // 用户取消操作，不做任何事情
        }
      })
    },
    handlevehicleViewClose () {
      this.vehicleView.visiable = false
    },
    handlevehicleViewOpen (row) {
      this.vehicleView.data = row
      this.vehicleView.visiable = true
    },
    handlevehiclePayClose () {
      this.vehiclePay.visiable = false
    },
    handlevehiclePayOpen (row) {
      this.vehiclePay.data = row
      this.vehiclePay.visiable = true
    },
    selectShopList () {
      this.$get(`/business/brand-info/list`).then((r) => {
        this.brandList = r.data.data
      })
    },
    editStatus (row, status) {
      this.$post('/business/order-info/account/status', { vehicleId: row.id, status }).then((r) => {
        this.$message.success('修改成功')
        this.fetch()
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.vehicleAdd.visiable = true
    },
    handlevehicleAddClose () {
      this.vehicleAdd.visiable = false
    },
    handlevehicleAddSuccess () {
      this.vehicleAdd.visiable = false
      this.$message.success('新增车辆成功')
      this.search()
    },
    edit (record) {
      this.$refs.vehicleEdit.setFormValues(record)
      this.vehicleEdit.visiable = true
    },
    handlevehicleEditClose () {
      this.vehicleEdit.visiable = false
    },
    handlevehicleEditSuccess () {
      this.vehicleEdit.visiable = false
      this.$message.success('修改车辆成功')
      this.search()
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/business/order-info/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      if (params.brand === undefined) {
        delete params.brand
      }
      params.userId = this.currentUser.userId
      this.$get('/business/order-info/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
