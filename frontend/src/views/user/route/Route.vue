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
        <a-button type="primary" ghost @click="add">新增</a-button>
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
<!--          <a-icon type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修 改" style="margin-left: 15px"></a-icon>-->
          <a-icon type="file-search" @click="handlevehicleViewOpen(record)" title="详 情" style="margin-left: 15px"></a-icon>
        </template>
      </a-table>
    </div>
    <vehicle-add
      v-if="vehicleAdd.visiable"
      @close="handlevehicleAddClose"
      @success="handlevehicleAddSuccess"
      :routeAddVisiable="vehicleAdd.visiable">
    </vehicle-add>
    <vehicle-edit
      ref="vehicleEdit"
      @close="handlevehicleEditClose"
      @success="handlevehicleEditSuccess"
      :vehicleEditVisiable="vehicleEdit.visiable">
    </vehicle-edit>
    <vehicle-view
      @close="handlevehicleViewClose"
      :routeShow="vehicleView.visiable"
      :routeData="vehicleView.data">
    </vehicle-view>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import vehicleAdd from './RouteAdd.vue'
import vehicleEdit from './RouteEdit.vue'
import vehicleView from './RouteView.vue'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'vehicle',
  components: {vehicleAdd, vehicleEdit, vehicleView, RangeDate},
  data () {
    return {
      advanced: false,
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
      return [
        {
          title: '起始位置',
          dataIndex: 'startAddress',
          ellipsis: true,
          width: 200
        },
        {
          title: '终点位置',
          dataIndex: 'endAddress',
          ellipsis: true,
          width: 200
        },
        {
          title: '乘客信息',
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
          width: 200
        },
        {
          title: '出发时间范围',
          dataIndex: 'earliestTime',
          customRender: (text, record, index) => {
            if (!text) return '- -'
            return (
              <div>
                <div>{text}</div>
                <div style="color: #999; font-size: 12px;">至 {record.latestTime}</div>
              </div>
            )
          },
          width: 180
        },
        {
          title: '行程距离(km)',
          dataIndex: 'distance',
          customRender: (text, record, index) => {
            if (text !== null && text !== undefined) {
              return `${text.toFixed(2)} km`
            } else {
              return '- -'
            }
          },
          width: 120
        },
        {
          title: '乘车人数',
          dataIndex: 'rideNum',
          customRender: (text, record, index) => {
            if (text !== null) {
              return `${text} 人`
            } else {
              return '- -'
            }
          },
          width: 100
        },
        {
          title: '类型',
          dataIndex: 'type',
          customRender: (text, record, index) => {
            switch (text) {
              case '0':
                return <a-tag color="blue">拼坐</a-tag>
              case '1':
                return <a-tag color="green">独享</a-tag>
              default:
                return <a-tag>未知</a-tag>
            }
          },
          width: 120
        },
        {
          title: '订单状态',
          dataIndex: 'status',
          customRender: (text, record, index) => {
            switch (text) {
              case '-1':
                return <a-tag>待接单</a-tag>
              case '0':
                return <a-tag color="orange">待上车</a-tag>
              case '1':
                return <a-tag color="green">已上车</a-tag>
              case '2':
                return <a-tag color="blue">已送达</a-tag>
              case '3':
                return <a-tag color="purple">已支付</a-tag>
              default:
                return <a-tag>未知</a-tag>
            }
          },
          width: 100
        },
        {
          title: '备注',
          dataIndex: 'remark',
          ellipsis: true,
          width: 150
        },
        {
          title: '创建时间',
          dataIndex: 'createDate',
          ellipsis: true,
          width: 160
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 100,
          fixed: 'right',
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    handlevehicleViewClose () {
      this.vehicleView.visiable = false
    },
    handlevehicleViewOpen (row) {
      this.vehicleView.data = row
      this.vehicleView.visiable = true
    },
    selectShopList () {
      this.$get(`/business/brand-info/list`).then((r) => {
        this.brandList = r.data.data
      })
    },
    editStatus (row, status) {
      this.$post('/business/route-info/account/status', { vehicleId: row.id, status }).then((r) => {
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
          that.$delete('/business/route-info/' + ids).then(() => {
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
      this.$get('/business/route-info/page', {
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
