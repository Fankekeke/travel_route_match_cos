
<template>
  <div>
    <a-row :gutter="16">
      <!-- 实时在线车主数 -->
      <a-col :span="8">
        <apexchart type="radialBar" :options="onlineStaffOptions" :series="onlineStaffSeries"></apexchart>
      </a-col>

      <!-- 今日统计 -->
      <a-col :span="8">
        <div class="today-stats">
          <h3>今日统计</h3>
          <p>发单量：<strong>{{ todayPassengerOrders }}</strong></p>
          <p>接单量：<strong>{{ todayStaffOrders }}</strong></p>
          <p>供需比：<strong>{{ supplyDemandRatio.toFixed(2) }}</strong></p>
          <p v-if="needsSubsidy" class="subsidy-warning">
            ⚠️ {{ subsidyRecommendation }}
          </p>
        </div>
      </a-col>

      <!-- 供需比仪表盘 -->
      <a-col :span="8">
        <apexchart type="radialBar" :options="supplyDemandOptions" :series="supplyDemandSeries"></apexchart>
      </a-col>
    </a-row>

    <!-- 当月交易分析 -->
    <a-row :gutter="16" style="margin-top: 20px;">
      <a-col :span="6">
        <a-card title="当月交易分析">
          <!-- 总览数据 -->
          <a-row :gutter="16">
            <a-col :span="12">
              <div class="summary-item">
                <p>年份：<strong>{{ monthlyTransactionData.year }}</strong></p>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="summary-item">
                <p>月份：<strong>{{ monthlyTransactionData.month }}</strong></p>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="summary-item">
                <p>总交易数：<strong>{{ monthlyTransactionData.totalTransactions }}</strong></p>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="summary-item">
                <p>交易总额：<strong>¥{{ monthlyTransactionData.totalAmount }}</strong></p>
              </div>
            </a-col>
          </a-row>

          <!-- 区间数据表格 -->
          <a-table
            :columns="transactionColumns"
            :data-source="transactionDataSource"
            :pagination="false"            style="margin-top: 20px;"
          />
        </a-card>
      </a-col>
      <a-col :span="18">
        <a-row :gutter="16" style="margin-top: 20px;">
          <a-col :span="24" style="height: 350px;">
            <a-card title="实时发单量和接单量统计" style="height: 100%;">
              <!-- 今日统计 -->
              <a-row :gutter="16">
                <a-col :span="8">
                  <div class="summary-item">
                    <p>今日乘客发单量：<strong>{{ realtimeOrderData.todayPassengerOrders }}</strong></p>
                  </div>
                </a-col>
                <a-col :span="8">
                  <div class="summary-item">
                    <p>今日车主接单量：<strong>{{ realtimeOrderData.todayStaffOrders }}</strong></p>
                  </div>
                </a-col>
                <a-col :span="8">
                  <div class="summary-item">
                    <p>供需比：<strong>{{ realtimeOrderData.supplyDemandRatio.toFixed(2) }}</strong></p>
                  </div>
                </a-col>
              </a-row>

              <!-- 每小时趋势图 -->
              <apexchart
                type="line"
                height=300
                :options="realtimeOrderOptions"
                :series="realtimeOrderSeries"            style="margin-top: 20px;"
              ></apexchart>
            </a-card>
          </a-col>
        </a-row>
      </a-col>
    </a-row>
  </div>
</template>

<script>import VueApexCharts from 'vue-apexcharts'

export default {
  name: 'Admin',
  components: {
    apexchart: VueApexCharts
  },
  data () {
    return {
      onlineStaffOptions: {},
      onlineStaffSeries: [],
      supplyDemandOptions: {},
      supplyDemandSeries: [],
      todayPassengerOrders: 0,
      todayStaffOrders: 0,
      supplyDemandRatio: 0,
      needsSubsidy: false,
      subsidyRecommendation: '',
      monthlyTransactionData: {
        year: 2026,
        month: 2,
        totalTransactions: 2,
        totalAmount: 5.2,
        distanceRangeCount: {
          '0-100': 2,
          '100-300': 0,
          '300-500': 0,
          '500-800': 0,
          '800-1500': 0,
          '1500+': 0
        },
        distanceRangeAmount: {
          '0-100': 5.2,
          '100-300': 0,
          '300-500': 0,
          '500-800': 0,
          '800-1500': 0,
          '1500+': 0
        }
      },
      transactionColumns: [
        { title: '距离区间', dataIndex: 'range', key: 'range' },
        { title: '交易次数', dataIndex: 'count', key: 'count' },
        { title: '交易金额 (元)', dataIndex: 'amount', key: 'amount' }
      ],
      transactionDataSource: [],
      realtimeOrderData: {
        todayPassengerOrders: 0,
        todayStaffOrders: 0,
        supplyDemandRatio: 0.0,
        hourlyPassengerOrders: Array(24).fill(0),
        hourlyStaffOrders: Array(24).fill(0)
      },
      realtimeOrderOptions: {
        chart: {
          type: 'line',
          height: '100%'
        },
        xaxis: {
          categories: Array.from({ length: 24 }, (_, i) => `${i}点`)
        },
        tooltip: {
          shared: true,
          intersect: false
        }
      },
      realtimeOrderSeries: [
        {
          name: '乘客发单量',
          data: []
        },
        {
          name: '车主接单量',
          data: []
        }
      ]
    }
  },
  mounted () {
    this.queryCurrentRouteByStaff()
    this.initTransactionTable()
  },
  methods: {
    updateRealtimeOrderChart(data) {
      this.realtimeOrderData = data.realtimeOrderData;
      this.realtimeOrderSeries = [
        {
          name: '乘客发单量',
          data: this.realtimeOrderData.hourlyPassengerOrders
        },
        {
          name: '车主接单量',
          data: this.realtimeOrderData.hourlyStaffOrders
        }
      ];
    },
    initTransactionTable () {
      const ranges = Object.keys(this.monthlyTransactionData.distanceRangeCount)
      this.transactionDataSource = ranges.map(range => ({
        key: range,
        range,
        count: this.monthlyTransactionData.distanceRangeCount[range],
        amount: this.monthlyTransactionData.distanceRangeAmount[range]
      }))
    },
    queryCurrentRouteByStaff () {
      this.$get('/business/order-info/queryHomeData').then((r) => {
        const data = r.data
        this.updateCharts(data)
        this.updateRealtimeOrderChart(data);
      })
    },
    updateCharts (data) {
      this.todayPassengerOrders = data.realtimeOrderData.todayPassengerOrders
      this.todayStaffOrders = data.realtimeOrderData.todayStaffOrders
      this.supplyDemandRatio = data.realtimeOrderData.supplyDemandRatio
      this.needsSubsidy = data.realtimeOrderData.needsSubsidy
      this.subsidyRecommendation = data.realtimeOrderData.subsidyRecommendation
      // 实时在线车主数图表配置
      this.onlineStaffOptions = {
        chart: {
          type: 'radialBar',
          width: '100%',
          height: 300,
          toolbar: {
            show: false // 隐藏工具栏
          }
        },
        plotOptions: {
          radialBar: {
            startAngle: -135,
            endAngle: 135,
            dataLabels: {
              name: {
                fontSize: '16px',
                color: '#333'
              },
              value: {
                fontSize: '24px',
                color: '#ff4d4f'
              }
            }
          }
        },
        labels: ['在线车主数'],
        colors: ['#1890ff'], // 主题色
        fill: {
          type: 'gradient',
          gradient: {
            shade: 'dark',
            type: 'horizontal',
            gradientToColors: ['#5bc0de'],
            stops: [0, 100]
          }
        }
      }
      this.onlineStaffSeries = [data.onlineStaffData.onlineStaffCount]

      this.supplyDemandOptions = {
        chart: {
          type: 'radialBar',
          width: '100%',
          height: 300
        },
        plotOptions: {
          radialBar: {
            startAngle: -135,
            endAngle: 135,
            dataLabels: {
              name: {
                fontSize: '16px',
                color: '#333'
              },
              value: {
                fontSize: '24px',
                color: '#ff4d4f'
              }
            }
          }
        },
        labels: ['供需比'],
        colors: ['#ff4d4f'],
        fill: {
          type: 'gradient',
          gradient: {
            shade: 'dark',
            type: 'horizontal',
            gradientToColors: ['#52c41a'],
            stops: [0, 100]
          }
        }
      }

      // 动态计算供需比百分比（假设最大值为 3）
      const maxRatio = 3
      this.supplyDemandSeries = [(this.supplyDemandRatio / maxRatio) * 100]

      // 实时发单量和接单量图表配置
      this.realtimeOrderOptions = {
        chart: {
          type: 'line'
        },
        xaxis: {
          categories: Array.from({ length: 24 }, (_, i) => `${i}点`)
        }
      }
      this.realtimeOrderSeries = [
        {
          name: '乘客发单量',
          data: data.realtimeOrderData.hourlyPassengerOrders
        },
        {
          name: '车主接单量',
          data: data.realtimeOrderData.hourlyStaffOrders
        }
      ]
    }
  }
}
</script>

<style scoped>/* 你可以在这里添加一些自定义样式 */

.today-stats {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  text-align: center;
}

.today-stats h3 {
  margin-bottom: 16px;
  font-size: 18px;
  color: #333;
}

.today-stats p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}

.subsidy-warning {
  color: #ff4d4f;
  font-weight: bold;
}
</style>

<style scoped>/* 卡片样式优化 */
.ant-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.ant-card-head-title {
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
}

/* 内容区域间距优化 */
.summary-item {
  padding: 12px;
  background-color: #f5f5f5;
  border-radius: 6px;
  margin-bottom: 10px;
}

.summary-item p {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.summary-item strong {
  color: #1890ff;
}
</style>

<style scoped>/* 表格样式优化 */
.ant-table-thead > tr > th {
  background-color: #fafafa;
  font-weight: bold;
  text-align: center;
}

.ant-table-tbody > tr > td {
  text-align: center;
  font-size: 14px;
}

.ant-table-tbody > tr:hover > td {
  background-color: #e6f7ff;
}
</style>
