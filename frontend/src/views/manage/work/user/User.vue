
<template>
  <div class="user-detail-container">
    <div class="info-section route-info" v-if="routeInfo">
      <div class="section-header">
        <h3 class="section-title">当前行程信息</h3>
      </div>

      <div class="info-grid">
        <div class="info-item">
          <span class="label">起点：</span>
          <span class="value">{{ routeInfo.startAddress }}</span>
        </div>

        <div class="info-item">
          <span class="label">终点：</span>
          <span class="value">{{ routeInfo.endAddress }}</span>
        </div>

        <div class="info-item">
          <span class="label">出发时间：</span>
          <span class="value date">{{ routeInfo.earliestTime }}</span>
        </div>

        <div class="info-item">
          <span class="label">最晚到达：</span>
          <span class="value date">{{ routeInfo.latestTime }}</span>
        </div>

        <div class="info-item">
          <span class="label">预估距离：</span>
          <span class="value">{{ routeInfo.distance }} km</span>
        </div>

        <div class="info-item">
          <span class="label">乘坐人数：</span>
          <span class="value">{{ routeInfo.rideNum }}</span>
        </div>

        <div class="info-item">
          <span class="label">服务类型：</span>
          <span class="value">
            <span v-if="routeInfo.type === '0'" class="service-type service-sharing">拼座</span>
            <span v-if="routeInfo.type === '1'" class="service-type service-exclusive">独享</span>
          </span>
        </div>

        <div class="info-item">
          <span class="label">高速费：</span>
          <span class="value">
            <span v-if="routeInfo.highwayTolls === '0'" class="fee-type fee-negotiable">部分协商</span>
            <span v-if="routeInfo.highwayTolls === '1'" class="fee-type fee-full">全部承担</span>
            <span v-if="routeInfo.highwayTolls === '2'" class="fee-type fee-none">不承担</span>
          </span>
        </div>

        <div class="info-item">
          <span class="label">状态：</span>
          <span class="value">
            <span v-if="routeInfo.status === '-1'" class="status pending">待匹配</span>
            <span v-if="routeInfo.status === '0'" class="status matched">已匹配</span>
            <span v-if="routeInfo.status === '1'" class="status completed">已完成</span>
            <span v-if="routeInfo.status === '2'" class="status cancelled">已取消</span>
          </span>
        </div>

        <div class="info-item full-width" v-if="routeInfo.remark">
          <span class="label">备注：</span>
          <span class="value content">{{ routeInfo.remark }}</span>
        </div>
      </div>
    </div>

    <div class="info-section user-info" v-if="userInfo">
      <div class="section-header">
        <h3 class="section-title">用户信息</h3>
      </div>

      <div class="info-grid">
        <div class="info-item">
          <span class="label">用户姓名：</span>
          <span class="value">{{ userInfo.name }}</span>
        </div>

        <div class="info-item">
          <span class="label">联系电话：</span>
          <span class="value phone">{{ userInfo.phone }}</span>
        </div>

        <div class="info-item">
          <span class="label">邮箱：</span>
          <span class="value">{{ userInfo.mail }}</span>
        </div>

        <div class="info-item">
          <span class="label">用户编号：</span>
          <span class="value">{{ userInfo.code }}</span>
        </div>

        <div class="info-item">
          <span class="label">积分：</span>
          <span class="value">{{ userInfo.integral }}</span>
        </div>

        <div class="info-item">
          <span class="label">性别：</span>
          <span class="value">{{ userInfo.sex === '1' ? '男' : userInfo.sex === '2' ? '女' : '-' }}</span>
        </div>

        <div class="info-item">
          <span class="label">注册时间：</span>
          <span class="value date">{{ userInfo.createDate }}</span>
        </div>

        <div class="info-item full-width" v-if="userInfo.remark">
          <span class="label">备注：</span>
          <span class="value content">{{ userInfo.remark }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>import {mapState} from 'vuex'

export default {
  name: 'User',
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  data () {
    return {
      routeInfo: null,
      userInfo: null
    }
  },
  mounted () {
    this.queryCurrentRouteByUser()
  },
  methods: {
    queryCurrentRouteByUser () {
      this.$get('/business/order-info/queryCurrentRouteByUser', {
        userId: this.currentUser.userId
      }).then((r) => {
        this.routeInfo = r.data.routeInfo
        this.userInfo = r.data.userInfo
      }).catch(error => {
        console.error('获取当前行程信息失败:', error)
        this.$message.error('获取当前行程信息失败')
      })
    }
  }
}
</script>

<style scoped>
.user-detail-container {
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

.info-item .value.id-number {
  font-family: monospace;
  letter-spacing: 1px;
}

.service-type, .fee-type, .status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  text-align: center;
}

.service-sharing {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.service-exclusive {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.fee-negotiable {
  background-color: #fffbe6;
  color: #faad14;
  border: 1px solid #ffe58f;
}

.fee-full {
  background-color: #f0f9ff;
  color: #2d8cf0;
  border: 1px solid #a6daff;
}

.fee-none {
  background-color: #f9f0ff;
  color: #722ed1;
  border: 1px solid #d3adf7;
}

.status.pending {
  background-color: #fff2e8;
  color: #ff7a45;
  border: 1px solid #ffd8bf;
}

.status.matched {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.status.completed {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status.cancelled {
  background-color: #fff1f0;
  color: #ff4d4f;
  border: 1px solid #ffa39e;
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
