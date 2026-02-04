<template>
  <a-modal v-model="show" title="订单支付" @cancel="onClose" :width="800"
           :body-style="{ padding: '0' }">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="default">
        关闭
      </a-button>
      <a-button key="pay" @click="handlePayment" type="primary" :loading="loading">
        支付
      </a-button>
    </template>

    <div class="order-detail-container" v-if="orderData !== null">
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

      <!-- 优惠券选择 -->
      <div class="info-section discount-info">
        <div class="section-header">
          <h3 class="section-title">优惠券</h3>
        </div>

        <div class="info-grid">
          <div class="info-item full-width">
            <span class="label">请选择优惠券：</span>
            <div class="coupon-list">
              <div
                v-for="discount in discountList"
                :key="discount.id"
                :class="['coupon-card', { disabled: !isCouponValid(discount) }]"
                @click="selectCoupon(discount)"
              >
                <div class="coupon-name">{{ discount.couponName }}</div>
                <div class="coupon-content">{{ discount.content }}</div>
                <div v-if="!isCouponValid(discount)" class="invalid-tip">未满足门槛金额</div>
              </div>
              <!-- 无优惠券时的提示 -->
              <div v-if="discountList.length === 0" class="no-coupons-tip">
                暂无可使用的优惠券
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 结算金额 -->
      <div class="info-section settlement-info">
        <div class="section-header">
          <h3 class="section-title">结算信息</h3>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="label">原价：</span>
            <span class="value">¥{{ orderData.orderPrice || '- -' }}</span>
          </div>

          <div class="info-item">
            <span class="label">优惠类型：</span>
            <span class="value">
        <span v-if="selectedDiscount && selectedDiscount.type === '1'">满减券</span>
        <span v-if="selectedDiscount && selectedDiscount.type === '2'">折扣券</span>
        <span v-if="!selectedDiscount">未选择优惠券</span>
      </span>
          </div>

          <div class="info-item">
            <span class="label">优惠金额：</span>
            <span class="value">- ¥{{ discountAmount }}</span>
          </div>

          <div class="info-item">
            <span class="label">应付金额：</span>
            <span class="value highlight">¥{{ finalAmount }}</span>
          </div>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>import moment from 'moment'
import {mapState} from 'vuex'

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
    selectedDiscount () {
      if (!this.selectedDiscountId) return null

      return this.discountList.find(
        discount => discount.id === this.selectedDiscountId
      )
    },
    // 计算优惠金额和最终金额
    discountAmount () {
      if (!this.selectedDiscountId) return 0
      const selectedDiscount = this.discountList.find(
        discount => discount.id === this.selectedDiscountId
      )

      if (!selectedDiscount) return 0

      if (selectedDiscount.type === '1') {
        // 满减券：满足门槛金额才可使用
        return selectedDiscount.threshold <= this.orderData.orderPrice
          ? selectedDiscount.discountPrice
          : 0
      } else if (selectedDiscount.type === '2') {
        // 折扣券：无门槛，直接按比例计算折扣金额
        const rebate = selectedDiscount.rebate || 0
        console.log(this.orderData.orderPrice * (rebate / 10))
        return this.orderData.orderPrice * ((10 - rebate) / 10) // rebate 是折扣比例（例如 8 表示 8 折）
      }
      return 0
    },
    finalAmount () {
      return (this.orderData.orderPrice - this.discountAmount).toFixed(2)
    },
    ...mapState({
      currentUser: state => state.account.user
    }),
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
      loading: false,
      discountList: [],
      selectedDiscountId: null // 选中的优惠券ID
    }
  },
  mounted () {
    this.queryDiscountByUser()
  },
  methods: {
    handlePayment  () {
      let data = { outTradeNo: this.orderData.code, subject: `${this.orderData.name}`, totalAmount: this.finalAmount, body: '', discountId: this.selectedDiscountId }
      this.$post('/business/pay/alipay', data).then((r) => {
        this.onClose()
        // console.log(r.data.msg)
        // 添加之前先删除一下，如果单页面，页面不刷新，添加进去的内容会一直保留在页面中，二次调用form表单会出错
        const divForm = document.getElementsByTagName('div')
        if (divForm.length) {
          document.body.removeChild(divForm[0])
        }
        const div = document.createElement('div')
        div.innerHTML = r.data.msg // data就是接口返回的form 表单字符串
        // console.log(div.innerHTML)
        document.body.appendChild(div)
        document.forms[0].setAttribute('target', '_self') // 新开窗口跳转
        document.forms[0].submit()
      })
    },
    isCouponValid (discount) {
      // 满减券需要校验门槛金额
      if (discount.type === '1') {
        return this.orderData.orderPrice >= discount.threshold
      }
      // 折扣券无门槛限制，默认可用
      return true
    },
    selectCoupon (discount) {
      // 如果优惠券不可用，直接返回
      if (!this.isCouponValid(discount)) {
        this.$message.warning('未满足门槛金额，无法选择此优惠券')
        return
      }
      // 设置选中的优惠券 ID
      this.selectedDiscountId = discount.id
    },
    queryDiscountByUser () {
      this.$get(`/business/discount-info/queryDiscountByUser`, {
        userId: this.currentUser.userId
      }).then((r) => {
        this.discountList = r.data.data
      })
    },
    getImageUrl (imagePath) {
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

.settlement-info {
  background-color: #fffbe6; /* 浅黄色背景 */
}

.highlight {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f; /* 红色强调金额 */
}

.coupon-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.coupon-card {
  width: 200px;
  padding: 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.coupon-card:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.coupon-card.disabled {
  background-color: #f5f5f5;
  color: #bfbfbf;
  cursor: not-allowed;
  border-color: #d9d9d9;
}

.coupon-card.disabled:hover {
  border-color: #d9d9d9;
  box-shadow: none;
}

.coupon-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.coupon-content {
  font-size: 12px;
  color: #595959;
}

.invalid-tip {
  font-size: 12px;
  color: #ff4d4f;
  margin-top: 4px;
}
.no-coupons-tip {
  font-size: 14px;
  color: #8c8c8c;
  text-align: center;
  padding: 20px 15px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  background-color: #fafafa;
}
</style>
