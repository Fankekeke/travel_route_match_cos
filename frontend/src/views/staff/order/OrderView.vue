
<template>
  <a-modal v-model="show" title="订单详情" @cancel="onClose" :width="600"
           :body-style="{ padding: '0' }">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="default">
        关闭
      </a-button>
    </template>

    <div class="order-detail-container" v-if="orderData !== null">
      <!-- 订单基本信息 -->
      <div class="info-section order-info">
        <div class="section-header">
          <h3 class="section-title">订单信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item full-width">
            <span class="label">订单编号：</span>
            <span class="value">{{ orderData.code }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">订单名称：</span>
            <span class="value">{{ orderData.orderName }}</span>
          </div>

          <div class="info-item">
            <span class="label">订单金额：</span>
            <span class="value">¥{{ orderData.orderPrice || '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">优惠后金额：</span>
            <span class="value">¥{{ orderData.afterOrderPrice || '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">预估里程：</span>
            <span class="value">{{ orderData.kilometre ? orderData.kilometre + ' km' : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">乘坐人数：</span>
            <span class="value">{{ orderData.rideNum ? orderData.rideNum + ' 人' : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">订单状态：</span>
            <span class="value">
              <a-tag v-if="orderData.status === '-1'" color="orange">确认中</a-tag>
              <a-tag v-if="orderData.status === '0'" color="blue">已确认</a-tag>
              <a-tag v-if="orderData.status === '1'" color="green">已接客</a-tag>
              <a-tag v-if="orderData.status === '2'" color="cyan">已送达</a-tag>
              <a-tag v-if="orderData.status === '3'" color="purple">已支付</a-tag>
              <a-tag v-if="orderData.status === '4'" color="red">已拒绝</a-tag>
              <a-tag v-if="orderData.status === '5'">已取消</a-tag>
              <span v-if="!orderData.status">- -</span>
            </span>
          </div>

          <div class="info-item">
            <span class="label">获得积分：</span>
            <span class="value">{{ orderData.integral || '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">预估时长：</span>
            <span class="value">{{ orderData.planMinute ? orderData.planMinute + ' 分钟' : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">实际时长：</span>
            <span class="value">{{ orderData.actualMinute ? orderData.actualMinute + ' 分钟' : '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">创建时间：</span>
            <span class="value date">{{ orderData.createDate }}</span>
          </div>

          <div class="info-item">
            <span class="label">支付时间：</span>
            <span class="value date">{{ orderData.payDate || '- -' }}</span>
          </div>
        </div>
      </div>

      <!-- 用户信息 -->
      <div class="info-section user-info">
        <div class="section-header">
          <h3 class="section-title">乘客信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item full-width">
            <span class="label">乘客姓名：</span>
            <span class="value">{{ orderData.userName }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">乘客电话：</span>
            <span class="value phone">{{ orderData.userPhone }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">乘客头像：</span>
            <div class="value">
              <img :size="50" :src="getImageUrl(orderData.userImages)"
                   alt="乘客头像"
                   class="avatar-image"/>
            </div>
          </div>

          <div class="info-item full-width" v-if="orderData.remark">
            <span class="label">乘客备注：</span>
            <span class="value content">{{ orderData.remark }}</span>
          </div>
        </div>
      </div>

      <!-- 车主信息 -->
      <div class="info-section staff-info">
        <div class="section-header">
          <h3 class="section-title">车主信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item full-width">
            <span class="label">车主姓名：</span>
            <span class="value">{{ orderData.staffName }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">车主电话：</span>
            <span class="value phone">{{ orderData.staffPhone || '- -' }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">车主头像：</span>
            <div class="value">
              <img :size="50" :src="getImageUrl(orderData.staffImages)"
                   alt="车主头像"
                   class="avatar-image"/>
            </div>
          </div>

          <div class="info-item full-width" v-if="orderData.staffRemark">
            <span class="label">车主备注：</span>
            <span class="value content">{{ orderData.staffRemark }}</span>
          </div>
        </div>
      </div>

      <!-- 车辆信息 -->
      <div class="info-section vehicle-info">
        <div class="section-header">
          <h3 class="section-title">车辆信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="label">车牌号码：</span>
            <span class="value">{{ orderData.vehicleNo }}</span>
          </div>

          <div class="info-item">
            <span class="label">车辆品牌：</span>
            <span class="value">{{ orderData.brand }}</span>
          </div>

          <div class="info-item">
            <span class="label">车辆类型：</span>
            <span class="value">
              <a-tag v-if="orderData.useType === '1'">轿车</a-tag>
              <a-tag v-if="orderData.useType === '2'" color="green">商务车</a-tag>
              <a-tag v-if="orderData.useType === '3'" color="blue">大巴</a-tag>
              <span v-if="!orderData.useType">- -</span>
            </span>
          </div>
        </div>
      </div>

      <!-- 出发到达信息 -->
      <div class="info-section route-info">
        <div class="section-header">
          <h3 class="section-title">行程信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item full-width">
            <span class="label">出发时间：</span>
            <span class="value">{{ orderData.staffEarliestTime }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">最晚到达：</span>
            <span class="value">{{ orderData.staffLatestTime }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">起点地址：</span>
            <span class="value">{{ orderData.staffStartAddress }}</span>
          </div>

          <div class="info-item full-width">
            <span class="label">终点地址：</span>
            <span class="value">{{ orderData.staffEndAddress }}</span>
          </div>
        </div>
      </div>

      <!-- AI推荐备注 -->
      <div class="info-section ai-remark" v-if="orderData.aiRemark">
        <div class="section-header">
          <h3 class="section-title">AI推荐备注</h3>
        </div>

        <div class="info-grid">
          <div class="info-item full-width">
            <span class="label">AI分析：</span>
            <span class="value content">{{ orderData.aiRemark }}</span>
          </div>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'OrderDetailView',
  props: {
    orderShow: {
      type: Boolean,
      default: false
    },
    orderData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.orderShow
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      loading: false
    }
  },
  methods: {
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
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>.order-detail-container {
  padding: 0;
}

.info-section {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  background-color: #fafafa;
}

.user-info {
  background-color: #f9f9f9;
}

.staff-info {
  background-color: #f8f9fa;
}

.vehicle-info {
  background-color: #f5f9ff;
}

.route-info {
  background-color: #f9f5ff;
}

.ai-remark {
  background-color: #fff9f5;
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
