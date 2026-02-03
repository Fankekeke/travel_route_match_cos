
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
      realtimeOrderOptions: {},
      realtimeOrderSeries: [],
      todayPassengerOrders: 0,
      todayStaffOrders: 0,
      supplyDemandRatio: 0,
      needsSubsidy: false,
      subsidyRecommendation: ''
    }
  },
  mounted () {
    this.queryCurrentRouteByStaff()
  },
  methods: {
    queryCurrentRouteByStaff () {
      this.$get('/business/order-info/queryHomeData').then((r) => {
        const data = r.data
        this.updateCharts(data)
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
