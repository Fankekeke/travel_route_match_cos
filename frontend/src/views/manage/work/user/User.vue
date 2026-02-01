
<template>
  <div class="user-detail-container">
    <a-row :gutter="12">
      <a-col :span="12">
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
                <!-- 匹配阶段 -->
                <a-tag v-if="routeInfo.status === '-1'" color="orange">待匹配</a-tag>
                            <!-- 匹配成功后进入订单流程 -->
                <a-tag v-if="routeInfo.status === '0'" color="blue">待上车</a-tag>
                <a-tag v-if="routeInfo.status === '1'" color="green">已上车</a-tag>
                <a-tag v-if="routeInfo.status === '2'" color="gold">已送达</a-tag>
                <a-tag v-if="routeInfo.status === '3'" color="purple">已支付</a-tag>
                <span v-if="!routeInfo.status">- -</span>
              </span>
            </div>

            <div class="info-item full-width" v-if="routeInfo.remark">
              <span class="label">备注：</span>
              <span class="value content">{{ routeInfo.remark }}</span>
            </div>
          </div>
        </div>
        <div class="info-section no-route" v-else>
          <div class="empty-state">
            <a-empty
              description="您当前没有正在进行的行程"
            />
            <div class="action-buttons">
              <a-button
                type="primary"
                @click="goToCreateRoute"
              >
                创建新行程
              </a-button>
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
      </a-col>
      <a-col :span="12">
        <div class="info-section staff-list" v-if="staffList && staffList.length > 0">
          <div class="section-header">
            <h3 class="section-title">匹配司机信息</h3>
          </div>

          <div class="staff-cards">
            <div
              class="staff-card"
              v-for="staff in staffList"
              :key="staff.id"
            >
              <div class="staff-header">
                <div class="staff-avatar">
                  <img
                    :src="getImageUrl(staff.staffInfo.images)"
                    alt="头像"
                  />
                </div>
                <div class="staff-basic-info">
                  <h4 class="staff-name">{{ staff.staffInfo.realName || staff.staffInfo.name }}</h4>
                  <p class="staff-phone">{{ staff.staffInfo.phone }}</p>
                  <div class="staff-rating">
                <span class="rating-score" v-if="staff.staffInfo.serviceScore">
                  服务评分: {{ staff.staffInfo.serviceScore }}
                </span>
                    <span class="rating-score" v-else>暂无评分</span>
                  </div>
                </div>
              </div>

              <div class="staff-details">
                <div class="detail-item">
                  <span class="label">预估价格:</span>
                  <span class="value">¥{{ staff.planPriceUnit }}</span>
                </div>

                <div class="detail-item">
                  <span class="label">匹配度:</span>
                  <span class="value match-rate">{{ staff.matchRate }}%</span>
                </div>

                <div class="detail-item">
                  <span class="label">距离起点:</span>
                  <span class="value">{{ staff.startDistance.toFixed(2) }} km</span>
                </div>

                <div class="detail-item">
                  <span class="label">距离终点:</span>
                  <span class="value">{{ staff.endDistance.toFixed(2) }} km</span>
                </div>

                <!-- 新增字段 -->
                <div class="detail-item full-width">
                  <span class="label">司机起点:</span>
                  <span class="value">{{ staff.startAddress }}</span>
                </div>

                <div class="detail-item full-width">
                  <span class="label">司机终点:</span>
                  <span class="value">{{ staff.endAddress }}</span>
                </div>

                <div class="detail-item">
                  <span class="label">出发时间:</span>
                  <span class="value date">{{ staff.earliestTime }}</span>
                </div>

                <div class="detail-item">
                  <span class="label">最晚到达:</span>
                  <span class="value date">{{ staff.latestTime }}</span>
                </div>

                <div class="detail-item">
                  <span class="label">载客数:</span>
                  <span class="value">{{ staff.rideNum }}</span>
                </div>
              </div>

              <div class="staff-actions">
                <a-button
                  v-if="routeInfo != null && routeInfo.orderId == null"
                  type="primary"
                  size="small"
                  @click="addOrder(staff)"
                >
                  邀请接单
                </a-button>
                <a-button
                  type="primary"
                  size="small"
                  @click="viewDriverRoute(staff)"
                >
                  查看司机行程
                </a-button>
                <a-button
                  type="default"
                  size="small"
                  @click="contactDriver(staff)"
                >
                  司机沟通
                </a-button>
              </div>
            </div>
          </div>
        </div>

        <div class="info-section no-staff" v-else>
          <div class="empty-state">
            <a-empty
              description="暂无匹配司机信息"
              v-if="!loadingStaffList"
            />
            <a-spin v-else tip="加载中..." />
          </div>
        </div>
      </a-col>
      <a-modal
        v-model="driverDetailVisible"
        title="司机行程详情"
        :footer="null"
        width="600px"
        @cancel="closeDriverDetailModal"
      >
        <div v-if="selectedDriver" class="driver-detail-content">
          <!-- 基本信息 -->
          <div class="detail-row">
            <div class="detail-item">
              <div class="info-label">司机姓名</div>
              <div class="info-value">{{ selectedDriver.staffInfo.realName || selectedDriver.staffInfo.name }}</div>
            </div>
            <div class="detail-item">
              <div class="info-label">联系电话</div>
              <div class="info-value phone">{{ selectedDriver.staffInfo.phone }}</div>
            </div>
          </div>

          <div class="detail-row">
            <div class="detail-item">
              <div class="info-label">邮箱</div>
              <div class="info-value">{{ selectedDriver.staffInfo.email }}</div>
            </div>
            <div class="detail-item">
              <div class="info-label">服务评分</div>
              <div class="info-value score">{{ selectedDriver.staffInfo.serviceScore || '暂无评分' }}</div>
            </div>
          </div>

          <!-- 路线信息 -->
          <div class="detail-row">
            <div class="detail-item">
              <div class="info-label">起点</div>
              <div class="info-value">{{ selectedDriver.startAddress }}</div>
            </div>
            <div class="detail-item">
              <div class="info-label">终点</div>
              <div class="info-value">{{ selectedDriver.endAddress }}</div>
            </div>
          </div>

          <div class="detail-row">
            <div class="detail-item">
              <div class="info-label">出发时间</div>
              <div class="info-value date">{{ selectedDriver.earliestTime }}</div>
            </div>
            <div class="detail-item">
              <div class="info-label">最晚到达</div>
              <div class="info-value date">{{ selectedDriver.latestTime }}</div>
            </div>
          </div>

          <!-- 价格和距离信息 -->
          <div class="detail-row">
            <div class="detail-item">
              <div class="info-label">预估距离</div>
              <div class="info-value">{{ selectedDriver.distance }} km</div>
            </div>
            <div class="detail-item">
              <div class="info-label">预估价格</div>
              <div class="info-value amount">¥{{ (selectedDriver.planPriceUnit).toFixed(2) }}</div>
            </div>
          </div>

          <div class="detail-row">
            <div class="detail-item">
              <div class="info-label">匹配度</div>
              <div class="info-value match-rate-value">{{ selectedDriver.matchRate }}%</div>
            </div>
            <div class="detail-item">
              <div class="info-label">载客数</div>
              <div class="info-value">{{ selectedDriver.rideNum }}</div>
            </div>
          </div>

          <!-- 距离信息 -->
          <div class="detail-row">
            <div class="detail-item">
              <div class="info-label">距离起点</div>
              <div class="info-value">{{ selectedDriver.startDistance.toFixed(2) }} km</div>
            </div>
            <div class="detail-item">
              <div class="info-label">距离终点</div>
              <div class="info-value">{{ selectedDriver.endDistance.toFixed(2) }} km</div>
            </div>
          </div>

          <!-- 车辆信息 -->
          <div class="detail-row">
            <div class="detail-item">
              <div class="info-label">计划单价</div>
              <div class="info-value">¥{{ selectedDriver.planPriceUnit }}</div>
            </div>
          </div>
          <div class="map-section">
            <div class="section-header">
              <h3 class="section-title">路线地图</h3>
            </div>
            <div id="route-map" style="height: 400px; width: 100%;"></div>
          </div>
        </div>
      </a-modal>
    </a-row>
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
      userInfo: null,
      staffList: [], // 新增司机列表数据
      loadingStaffList: false, // 加载状态
      driverDetailVisible: false, // 控制 Modal 显示隐藏
      selectedDriver: null // 存储选中的司机信息
    }
  },
  mounted () {
    this.queryCurrentRouteByUser()
  },
  methods: {
    // 邀请司机接单
    addOrder (staff) {
      this.$confirm({
        title: '邀请接单',
        content: `确定邀请司机 ${staff.staffInfo.realName || staff.staffInfo.name} 接单吗？`,
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          // 调用后端API邀请司机接单
          this.$post('/business/order-info', {
            userId: this.currentUser.userId, // 当前用户ID
            staffId: staff.staffId, // 司机ID
            userRouteId: this.routeInfo.id, // 用户行程ID
            staffRouteId: staff.id // 司机路线ID
          }).then((r) => {
            this.$message.success('邀请成功，等待司机确认')
            // 刷新页面数据
            this.queryCurrentRouteByUser()
          }).catch(error => {
            console.error('邀请司机接单失败:', error)
            this.$message.error('邀请失败，请稍后重试')
          })
        }
      })
    },
    // 获取头像URL
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
    queryRouteStaffList (userRouteId) {
      this.loadingStaffList = true
      this.$get('/business/route-staff-info/queryRouteStaffList', {
        userRouteId: userRouteId
      }).then((r) => {
        if (r && r.data.data) {
          this.staffList = Array.isArray(r.data.data) ? r.data.data : []
          this.staffList.sort((a, b) => b.matchRate - a.matchRate)
        } else {
          this.staffList = []
        }
        this.loadingStaffList = false
      }).catch(error => {
        console.error('获取司机信息失败:', error)
        this.$message.error('获取司机信息失败')
        this.loadingStaffList = false
      })
    },

    goToCreateRoute () {
      // 根据实际路由配置调整路径
      this.$router.push('/user/route')
    },

    queryCurrentRouteByUser () {
      this.$get('/business/order-info/queryCurrentRouteByUser', {
        userId: this.currentUser.userId
      }).then((r) => {
        // 检查是否有有效的行程信息
        if (r.data && r.data.routeInfo) {
          this.routeInfo = r.data.routeInfo
          this.userInfo = r.data.userInfo
          if (this.routeInfo && this.routeInfo.id) {
            this.queryRouteStaffList(this.routeInfo.id)
          }
        } else {
          // 当没有行程时，仍显示用户信息
          this.routeInfo = null
          this.userInfo = r.data.userInfo || this.currentUser
        }
      }).catch(error => {
        console.error('获取当前行程信息失败:', error)
        this.$message.error('获取当前行程信息失败')
        // 即使失败也尝试显示用户基本信息
        this.routeInfo = null
        this.userInfo = this.currentUser
      })
    },

    // 显示司机详情模态框
    showDriverDetailModal (staff) {
      this.selectedDriver = staff
      this.driverDetailVisible = true
      setTimeout(() => {
        this.initRouteMap()
      }, 200)
    },

    // 关闭司机详情模态框
    closeDriverDetailModal () {
      this.driverDetailVisible = false
      this.selectedDriver = null
    },

    // 与司机沟通功能
    contactDriver (staff) {
      this.$message.success(`正在联系司机 ${staff.staffInfo.realName || staff.staffInfo.name}`)

      // 示例：打开聊天窗口或拨打电话
      // 可以集成即时通讯功能
      this.openChatWithDriver(staff)
    },

    // 打开与司机的聊天界面
    openChatWithDriver (staff) {
      // 实现与司机的沟通逻辑
      // 可以是弹窗聊天、跳转聊天页面或调用外部通讯工具
      this.$confirm({
        title: '联系司机',
        content: `是否在线沟通司机 ${staff.staffInfo.realName || staff.staffInfo.name} ？`,
        okText: '沟通',
        cancelText: '取消',
        onOk: () => {
          this.$post('/business/chat-record/defaultChat', {
            staffId: staff.staffId,
            userId: this.currentUser.userId,
            senderType: 0,
            content: '你好'
          }).then((r) => {
            // 跳转到聊天页面，并传递默认消息
            this.$router.push({
              path: '/user/message'
            })
          })
        }
      })
    },

    // 查看司机行程功能
    viewDriverRoute (staff) {
      this.showDriverDetailModal(staff)
    },
    initRouteMap () {
      this.map = new BMapGL.Map('route-map')
      this.map.centerAndZoom(new BMapGL.Point(116.404, 39.915), 12)
      this.map.enableScrollWheelZoom(true)

      // 解析路径数据
      let pathCoordinates = []
      if (this.selectedDriver.path) {
        try {
          pathCoordinates = JSON.parse(this.selectedDriver.path)
        } catch (e) {
          console.error('解析路径数据失败:', e)
          // 如果解析失败，回退到使用起终点坐标
          pathCoordinates = [
            { latitude: this.selectedDriver.startLatitude, longitude: this.selectedDriver.startLongitude },
            { latitude: this.selectedDriver.endLatitude, longitude: this.selectedDriver.endLongitude }
          ]
        }
      } else {
        // 如果没有路径数据，使用起终点坐标
        pathCoordinates = [
          { latitude: this.selectedDriver.startLatitude, longitude: this.selectedDriver.startLongitude },
          { latitude: this.selectedDriver.endLatitude, longitude: this.selectedDriver.endLongitude }
        ]
      }

      // 添加起点标记
      if (pathCoordinates.length > 0) {
        const startPoint = new BMapGL.Point(pathCoordinates[0].longitude, pathCoordinates[0].latitude)
        // 设置标记图标
        const startIcon = new BMapGL.Icon('static/img/start.png', new BMapGL.Size(32, 32), {
          offset: new BMapGL.Size(0, 0),
          imageOffset: new BMapGL.Size(0, 0)
        })
        const startMarker = new BMapGL.Marker(startPoint)
        startMarker.setIcon(startIcon)
        this.map.addOverlay(startMarker)
      }

      // 添加终点标记
      if (pathCoordinates.length > 0) {
        const endPoint = new BMapGL.Point(
          pathCoordinates[pathCoordinates.length - 1].longitude,
          pathCoordinates[pathCoordinates.length - 1].latitude
        )
        // 设置标记图标
        const endIcon = new BMapGL.Icon('static/img/end.png', new BMapGL.Size(32, 32), {
          offset: new BMapGL.Size(0, 0),
          imageOffset: new BMapGL.Size(0, 0)
        })
        const endMarker = new BMapGL.Marker(endPoint)
        endMarker.setIcon(endIcon)
        this.map.addOverlay(endMarker)
      }

      // 将路径坐标转换为百度地图点数组
      const pathPoints = pathCoordinates.map(coord => new BMapGL.Point(coord.longitude, coord.latitude))

      // 绘制详细路线
      if (pathPoints.length > 1) {
        const polyline = new BMapGL.Polyline(pathPoints, {
          strokeColor: '#1890ff',
          strokeWeight: 6,
          strokeOpacity: 0.8
        })
        this.map.addOverlay(polyline)
      }

      // 自动调整视野以显示整个路线
      if (pathPoints.length > 0) {
        this.map.setViewport(pathPoints)
      }
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

.staff-list {
  background-color: #f8f9fa;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.staff-cards {
  max-height: calc(100vh - 250px);
  overflow-y: auto;
  overflow-x: hidden;
}

.staff-card {
  background: white;
  margin-bottom: 16px;
  padding: 16px;
  transition: box-shadow 0.3s ease;
}

.staff-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.staff-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.staff-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 16px;
  border: 2px solid #e8e8e8;
}

.staff-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.staff-basic-info {
  flex: 1;
}

.staff-name {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1d;
}

.staff-phone {
  margin: 0 0 4px 0;
  color: #1890ff;
  font-size: 14px;
}

.staff-rating {
  font-size: 12px;
  color: #52c41a;
}

.rating-score {
  background-color: #f6ffed;
  color: #52c41a;
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #b7eb8f;
}

.staff-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 10px;
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-item .label {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 2px;
}

.detail-item .value {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.detail-item .value.match-rate {
  color: #52c41a;
  font-weight: 600;
}

.staff-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.no-staff {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
  background-color: #fafafa;
}

.empty-state {
  text-align: center;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .staff-header {
    flex-direction: column;
    text-align: center;
  }

  .staff-avatar {
    margin-right: 0;
    margin-bottom: 12px;
  }

  .staff-actions {
    flex-direction: column;
  }

  .staff-actions .ant-btn {
    width: 100%;
    margin-bottom: 8px;
  }
}

/* 在样式部分添加以下内容 */
.driver-detail-content {
  overflow-y: auto;
  padding-right: 10px;
}

.detail-row {
  display: flex;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #f0f0f0;
}

.detail-label {
  font-weight: 500;
  color: #595959;
  width: 100px;
  flex-shrink: 0;
}

.detail-value {
  flex: 1;
  color: #262626;
  word-break: break-all;
}

.detail-item.full-width {
  grid-column: span 2;
}

/* 美化后的司机详情模态框样式 */
.driver-detail-content {
  padding: 10px 0;
}

.detail-row {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 16px;
}

.detail-item {
  flex: 1;
  min-width: 200px;
}

.detail-item.full-width {
  flex: 0 0 100%;
}

.info-label {
  font-weight: 500;
  color: #595959;
  font-size: 13px;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  color: #262626;
  word-break: break-all;
  line-height: 1.5;
}

.info-value.date {
  color: #8c8c8c;
  font-size: 13px;
}

.info-value.phone {
  color: #1890ff;
  font-weight: 500;
}

.info-value.score {
  color: #52c41a;
  font-weight: 600;
  font-size: 15px;
}

.info-value.amount {
  font-weight: 600;
  color: #1890ff;
  font-size: 16px;
}

.match-rate-value {
  color: #1890ff;
  font-weight: 600;
}

.remark-content {
  background-color: #f9f9f9;
  padding: 8px;
  border-radius: 4px;
  border-left: 3px solid #1890ff;
}

.no-route,
.no-user-info {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
  background-color: #fafafa;
  text-align: center;
}

.action-buttons {
  margin-top: 20px;
}
</style>
