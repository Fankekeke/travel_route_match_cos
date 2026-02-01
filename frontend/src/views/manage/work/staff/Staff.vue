
<template>
  <div class="staff-work-container">
    <div class="route-info-section">
      <div class="section-header">
        <h3 class="section-title">路线信息</h3>
      </div>
      <div v-if="routeInfo && routeInfo.routeStaffInfo" class="info-grid">
        <div class="route-item">
          <span class="label">起点：</span>
          <span class="value">{{ routeInfo.routeStaffInfo.startAddress }}</span>
        </div>
        <div class="route-item">
          <span class="label">终点：</span>
          <span class="value">{{ routeInfo.routeStaffInfo.endAddress }}</span>
        </div>
        <div class="route-item">
          <span class="label">出发时间：</span>
          <span class="value">{{ routeInfo.routeStaffInfo.earliestTime }}</span>
        </div>
        <div class="route-item">
          <span class="label">最晚到达：</span>
          <span class="value">{{ routeInfo.routeStaffInfo.latestTime }}</span>
        </div>
        <div class="route-item">
          <span class="label">预估距离：</span>
          <span class="value">{{ routeInfo.routeStaffInfo.distance }} km</span>
        </div>
        <div class="route-item">
          <span class="label">计划单价：</span>
          <span class="value">¥{{ routeInfo.routeStaffInfo.planPriceUnit }}</span>
        </div>
        <div class="route-item">
          <span class="label">可乘人数：</span>
          <span class="value">{{ routeInfo.routeStaffInfo.rideNum }}</span>
        </div>
        <div class="route-item">
          <span class="label">状态：</span>
          <span class="value">
        <a-tag v-if="routeInfo.routeStaffInfo.status === '0'" color="orange">候补中</a-tag>
        <a-tag v-if="routeInfo.routeStaffInfo.status === '1'" color="green">已完成</a-tag>
        <a-tag v-if="routeInfo.routeStaffInfo.status === '2'" color="gray">暂停</a-tag>
      </span>
        </div>
      </div>
      <div v-else class="no-route">
        <div class="empty-state">
          <a-empty
            description="您当前没有正在进行的路线"
          />
          <div class="action-buttons">
            <a-button
              type="primary"
              @click="goToCreateRoute"
            >
              创建新路线
            </a-button>
          </div>
        </div>
      </div>
    </div>


    <div class="vehicle-info-section">
      <div class="section-header">
        <h3 class="section-title">车辆信息</h3>
      </div>
      <div v-if="routeInfo && routeInfo.vehicleInfo" class="info-grid">
        <div class="vehicle-item">
          <span class="label">车牌号：</span>
          <span class="value">{{ routeInfo.vehicleInfo.vehicleNo }}</span>
        </div>
        <div class="vehicle-item">
          <span class="label">车辆名称：</span>
          <span class="value">{{ routeInfo.vehicleInfo.name }}</span>
        </div>
        <div class="vehicle-item">
          <span class="label">车辆品牌：</span>
          <span class="value">{{ routeInfo.vehicleInfo.brand }}</span>
        </div>
        <div class="vehicle-item">
          <span class="label">座位数：</span>
          <span class="value">{{ routeInfo.vehicleInfo.seatNum }}</span>
        </div>
        <div class="vehicle-item">
          <span class="label">燃料类型：</span>
          <span class="value fuel-type">
        <span v-if="routeInfo.vehicleInfo.fuelType === '1'" class="fuel-label fuel-gasoline">燃油</span>
        <span v-if="routeInfo.vehicleInfo.fuelType === '2'" class="fuel-label fuel-diesel">柴油</span>
        <span v-if="routeInfo.vehicleInfo.fuelType === '3'" class="fuel-label fuel-hybrid">油电混动</span>
        <span v-if="routeInfo.vehicleInfo.fuelType === '4'" class="fuel-label fuel-electric">电能</span>
      </span>
        </div>
      </div>
      <div v-else class="no-vehicle">
        <div class="empty-state">
          <a-empty
            description="当前路线未关联车辆信息"
          />
        </div>
      </div>
    </div>

    <div class="staff-info-section">
      <div class="section-header">
        <h3 class="section-title">车主信息</h3>
      </div>
      <div v-if="routeInfo && routeInfo.staffInfo" class="info-grid">
        <div class="staff-item">
          <span class="label">车主姓名：</span>
          <span class="value">{{ routeInfo.staffInfo.name }}</span>
        </div>
        <div class="staff-item">
          <span class="label">联系电话：</span>
          <span class="value phone">{{ routeInfo.staffInfo.phone }}</span>
        </div>
        <div class="staff-item">
          <span class="label">邮箱：</span>
          <span class="value">{{ routeInfo.staffInfo.email }}</span>
        </div>
        <div class="staff-item">
          <span class="label">地址：</span>
          <span class="value">{{ routeInfo.staffInfo.address }}</span>
        </div>
      </div>
      <div v-else class="no-staff">
        <div class="empty-state">
          <a-empty
            description="当前路线未关联车主信息"
          />
        </div>
      </div>
    </div>

    <div>
      <a-row>
        <a-col :span="12">
          <div class="order-list-section">
            <div class="section-header">
              <h3 class="section-title">车找人</h3>
            </div>
            <div class="route-user-list-container" v-if="routeUserList && routeUserList.length > 0">
              <div>
                <a-list item-layout="vertical" size="large" :data-source="routeUserList">
                  <a-list-item slot="renderItem" slot-scope="item">
                    <a-card :bordered="false" class="route-user-card">
                      <div class="route-user-header">
                        <div class="route-user-info">
                          <div class="user-name">{{ item.userInfo.name }}</div>
                          <div class="user-phone" v-if="item.userInfo.phone">{{ item.userInfo.phone }}</div>
                        </div>
                        <div class="match-rate">
                          <a-tag color="blue">匹配度: {{ item.matchRate }}%</a-tag>
                        </div>
                      </div>

                      <div class="route-user-details">
                        <div class="detail-row">
                          <div class="detail-item">
                            <div class="info-label">起点</div>
                            <div class="info-value">{{ item.startAddress }}</div>
                          </div>
                          <div class="detail-item">
                            <div class="info-label">终点</div>
                            <div class="info-value">{{ item.endAddress }}</div>
                          </div>
                        </div>

                        <div class="detail-row">
                          <div class="detail-item">
                            <div class="info-label">出发时间</div>
                            <div class="info-value">{{ item.earliestTime }}</div>
                          </div>
                          <div class="detail-item">
                            <div class="info-label">最晚到达</div>
                            <div class="info-value">{{ item.latestTime }}</div>
                          </div>
                        </div>

                        <div class="detail-row">
                          <div class="detail-item">
                            <div class="info-label">距离</div>
                            <div class="info-value">{{ item.distance }} km</div>
                          </div>
                          <div class="detail-item">
                            <div class="info-label">可乘人数</div>
                            <div class="info-value">{{ item.rideNum }}</div>
                          </div>
                          <div class="detail-item">
                            <div class="info-label">服务类型</div>
                            <div class="info-value">
                              <a-tag v-if="item.type === '0'" color="blue">拼座</a-tag>
                              <a-tag v-if="item.type === '1'" color="green">独享</a-tag>
                              <span v-if="!item.type">-</span>
                            </div>
                          </div>
                          <div class="detail-item">
                            <div class="info-label">高速费</div>
                            <div class="info-value">
                              <a-tag v-if="item.highwayTolls === '0'" color="orange">部分协商</a-tag>
                              <a-tag v-if="item.highwayTolls === '1'" color="blue">全部承担</a-tag>
                              <a-tag v-if="item.highwayTolls === '2'" color="red">不承担</a-tag>
                              <span v-if="!item.highwayTolls">-</span>
                            </div>
                          </div>
                          <div class="detail-item">
                            <div class="info-label">起点距离</div>
                            <div class="info-value">{{ item.startDistance || 0 }} km</div>
                          </div>
                          <div class="detail-item">
                            <div class="info-label">终点距离</div>
                            <div class="info-value">{{ item.endDistance || 0 }} km</div>
                          </div>
                        </div>

                        <div class="detail-row" v-if="item.remark">
                          <div class="detail-item full-width">
                            <div class="info-label">备注</div>
                            <div class="info-value">{{ item.remark }}</div>
                          </div>
                        </div>
                      </div>

                      <div class="route-user-actions">
                        <a-button size="small" type="primary" @click="viewRoute(item)">查看行程</a-button>
                        <a-button size="small" type="primary" @click="contactUser(item)">联系乘客</a-button>
                      </div>
                    </a-card>
                  </a-list-item>
                </a-list>
              </div>
            </div>
            <div v-else class="no-orders">
              当前没有匹配的乘客订单
            </div>
          </div>
        </a-col>
        <a-col :span="12">
          <div class="order-list-section">
            <div class="section-header">
              <h3 class="section-title">当前订单列表</h3>
            </div>
            <div class="route-user-list-container" v-if="routeInfo.orderInfoList && routeInfo.orderInfoList.length > 0">
              <div>
                <a-tabs default-active-key="all" tab-position="top">
                  <a-tab-pane key="all" tab="全部订单">
                    <div v-for="order in routeInfo.orderInfoList" :key="order.id" class="order-item">
                      <a-card class="order-inner-card">
                        <div class="order-header">
                          <div class="order-basic-info">
                            <h3>{{ order.orderName }}</h3>
                            <div class="order-id">订单号：{{ order.code }}</div>
                          </div>
                          <div class="order-status">
                            <a-tag v-if="order.status === '0'" color="blue">待接单</a-tag>
                            <a-tag v-if="order.status === '1'" color="orange">已接单</a-tag>
                            <a-tag v-if="order.status === '2'" color="gold">行程中</a-tag>
                            <a-tag v-if="order.status === '3'" color="green">已完成</a-tag>
                            <a-tag v-if="order.status === '4'" color="red">已取消</a-tag>
                          </div>
                        </div>

                        <!-- 用户信息 -->
                        <div class="info-row">
                          <div class="info-item user-info">
                            <div class="info-label">乘客信息</div>
                            <div class="info-value" style="margin-left: 15px">
                              <div class="user-name">{{ order.userInfo.name }}</div>
                              <div class="user-phone">{{ order.userInfo.phone }}</div>
                            </div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">下单时间</div>
                            <div class="info-value">{{ order.createDate }}</div>
                          </div>
                        </div>

                        <!-- 订单金额 -->
                        <div class="info-row">
                          <div class="info-item">
                            <div class="info-label">订单金额</div>
                            <div class="info-value amount">¥{{ order.orderPrice }}</div>
                          </div>
                          <div class="info-item" v-if="order.afterOrderPrice && order.afterOrderPrice !== order.orderPrice">
                            <div class="info-label">优惠后金额</div>
                            <div class="info-value amount discount">¥{{ order.afterOrderPrice }}</div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">里程</div>
                            <div class="info-value">{{ order.kilometre }} km</div>
                          </div>
                        </div>

                        <!-- 路线信息 -->
                        <div class="route-info-wrapper">
                          <div class="info-label">路线详情</div>
                          <div class="route-details">
                            <div class="route-item">
                              <i class="icon start-icon">起点</i>
                              <span>{{ order.routeInfo.startAddress }}</span>
                            </div>
                            <div class="route-item">
                              <i class="icon end-icon">终点</i>
                              <span>{{ order.routeInfo.endAddress }}</span>
                            </div>
                            <div class="route-distance">距离：{{ order.routeInfo.distance }} km</div>
                          </div>
                        </div>

                        <!-- 评价信息 -->
                        <div class="evaluation-wrapper" v-if="order.evaluateInfo">
                          <div class="info-label">评价信息</div>
                          <div class="evaluation-details">
                            <a-rate :value="order.evaluateInfo.score" disabled/>
                            <div class="evaluation-content">{{ order.evaluateInfo.content }}</div>
                          </div>
                        </div>
                      </a-card>
                    </div>
                  </a-tab-pane>

                  <a-tab-pane key="pending" tab="待接单">
                    <div v-for="order in getOrdersByStatus('0')" :key="order.id" class="order-item">
                      <a-card class="order-inner-card">
                        <div class="order-header">
                          <div class="order-basic-info">
                            <h3>{{ order.orderName }}</h3>
                            <div class="order-id">订单号：{{ order.code }}</div>
                          </div>
                          <div class="order-status">
                            <a-tag color="blue">待接单</a-tag>
                          </div>
                        </div>

                        <!-- 用户信息 -->
                        <div class="info-row">
                          <div class="info-item user-info">
                            <div class="info-label">乘客信息</div>
                            <div class="info-value" style="margin-left: 15px">
                              <div class="user-name">{{ order.userInfo.name }}</div>
                              <div class="user-phone">{{ order.userInfo.phone }}</div>
                            </div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">下单时间</div>
                            <div class="info-value">{{ order.createDate }}</div>
                          </div>
                        </div>

                        <!-- 订单金额 -->
                        <div class="info-row">
                          <div class="info-item">
                            <div class="info-label">订单金额</div>
                            <div class="info-value amount">¥{{ order.orderPrice }}</div>
                          </div>
                          <div class="info-item" v-if="order.afterOrderPrice && order.afterOrderPrice !== order.orderPrice">
                            <div class="info-label">优惠后金额</div>
                            <div class="info-value amount discount">¥{{ order.afterOrderPrice }}</div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">里程</div>
                            <div class="info-value">{{ order.kilometre }} km</div>
                          </div>
                        </div>

                        <!-- 路线信息 -->
                        <div class="route-info-wrapper">
                          <div class="info-label">路线详情</div>
                          <div class="route-details">
                            <div class="route-item">
                              <i class="icon start-icon">起点</i>
                              <span>{{ order.routeInfo.startAddress }}</span>
                            </div>
                            <div class="route-item">
                              <i class="icon end-icon">终点</i>
                              <span>{{ order.routeInfo.endAddress }}</span>
                            </div>
                            <div class="route-distance">距离：{{ order.routeInfo.distance }} km</div>
                          </div>
                        </div>

                        <!-- 评价信息 -->
                        <div class="evaluation-wrapper" v-if="order.evaluateInfo">
                          <div class="info-label">评价信息</div>
                          <div class="evaluation-details">
                            <a-rate :value="order.evaluateInfo.score" disabled/>
                            <div class="evaluation-content">{{ order.evaluateInfo.content }}</div>
                          </div>
                        </div>
                      </a-card>
                    </div>
                  </a-tab-pane>

                  <a-tab-pane key="accepted" tab="已接单">
                    <div v-for="order in getOrdersByStatus('1')" :key="order.id" class="order-item">
                      <a-card class="order-inner-card">
                        <div class="order-header">
                          <div class="order-basic-info">
                            <h3>{{ order.orderName }}</h3>
                            <div class="order-id">订单号：{{ order.code }}</div>
                          </div>
                          <div class="order-status">
                            <a-tag color="orange">已接单</a-tag>
                          </div>
                        </div>

                        <!-- 用户信息 -->
                        <div class="info-row">
                          <div class="info-item user-info">
                            <div class="info-label">乘客信息</div>
                            <div class="info-value" style="margin-left: 15px">
                              <div class="user-name">{{ order.userInfo.name }}</div>
                              <div class="user-phone">{{ order.userInfo.phone }}</div>
                            </div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">下单时间</div>
                            <div class="info-value">{{ order.createDate }}</div>
                          </div>
                        </div>

                        <!-- 订单金额 -->
                        <div class="info-row">
                          <div class="info-item">
                            <div class="info-label">订单金额</div>
                            <div class="info-value amount">¥{{ order.orderPrice }}</div>
                          </div>
                          <div class="info-item" v-if="order.afterOrderPrice && order.afterOrderPrice !== order.orderPrice">
                            <div class="info-label">优惠后金额</div>
                            <div class="info-value amount discount">¥{{ order.afterOrderPrice }}</div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">里程</div>
                            <div class="info-value">{{ order.kilometre }} km</div>
                          </div>
                        </div>

                        <!-- 路线信息 -->
                        <div class="route-info-wrapper">
                          <div class="info-label">路线详情</div>
                          <div class="route-details">
                            <div class="route-item">
                              <i class="icon start-icon">起点</i>
                              <span>{{ order.routeInfo.startAddress }}</span>
                            </div>
                            <div class="route-item">
                              <i class="icon end-icon">终点</i>
                              <span>{{ order.routeInfo.endAddress }}</span>
                            </div>
                            <div class="route-distance">距离：{{ order.routeInfo.distance }} km</div>
                          </div>
                        </div>

                        <!-- 评价信息 -->
                        <div class="evaluation-wrapper" v-if="order.evaluateInfo">
                          <div class="info-label">评价信息</div>
                          <div class="evaluation-details">
                            <a-rate :value="order.evaluateInfo.score" disabled/>
                            <div class="evaluation-content">{{ order.evaluateInfo.content }}</div>
                          </div>
                        </div>
                      </a-card>
                    </div>
                  </a-tab-pane>

                  <a-tab-pane key="inprogress" tab="行程中">
                    <div v-for="order in getOrdersByStatus('2')" :key="order.id" class="order-item">
                      <a-card class="order-inner-card">
                        <div class="order-header">
                          <div class="order-basic-info">
                            <h3>{{ order.orderName }}</h3>
                            <div class="order-id">订单号：{{ order.code }}</div>
                          </div>
                          <div class="order-status">
                            <a-tag color="gold">行程中</a-tag>
                          </div>
                        </div>

                        <!-- 用户信息 -->
                        <div class="info-row">
                          <div class="info-item user-info">
                            <div class="info-label">乘客信息</div>
                            <div class="info-value" style="margin-left: 15px">
                              <div class="user-name">{{ order.userInfo.name }}</div>
                              <div class="user-phone">{{ order.userInfo.phone }}</div>
                            </div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">下单时间</div>
                            <div class="info-value">{{ order.createDate }}</div>
                          </div>
                        </div>

                        <!-- 订单金额 -->
                        <div class="info-row">
                          <div class="info-item">
                            <div class="info-label">订单金额</div>
                            <div class="info-value amount">¥{{ order.orderPrice }}</div>
                          </div>
                          <div class="info-item" v-if="order.afterOrderPrice && order.afterOrderPrice !== order.orderPrice">
                            <div class="info-label">优惠后金额</div>
                            <div class="info-value amount discount">¥{{ order.afterOrderPrice }}</div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">里程</div>
                            <div class="info-value">{{ order.kilometre }} km</div>
                          </div>
                        </div>

                        <!-- 路线信息 -->
                        <div class="route-info-wrapper">
                          <div class="info-label">路线详情</div>
                          <div class="route-details">
                            <div class="route-item">
                              <i class="icon start-icon">起点</i>
                              <span>{{ order.routeInfo.startAddress }}</span>
                            </div>
                            <div class="route-item">
                              <i class="icon end-icon">终点</i>
                              <span>{{ order.routeInfo.endAddress }}</span>
                            </div>
                            <div class="route-distance">距离：{{ order.routeInfo.distance }} km</div>
                          </div>
                        </div>

                        <!-- 评价信息 -->
                        <div class="evaluation-wrapper" v-if="order.evaluateInfo">
                          <div class="info-label">评价信息</div>
                          <div class="evaluation-details">
                            <a-rate :value="order.evaluateInfo.score" disabled/>
                            <div class="evaluation-content">{{ order.evaluateInfo.content }}</div>
                          </div>
                        </div>
                      </a-card>
                    </div>
                  </a-tab-pane>

                  <a-tab-pane key="completed" tab="已完成">
                    <div v-for="order in getOrdersByStatus('3')" :key="order.id" class="order-item">
                      <a-card class="order-inner-card">
                        <div class="order-header">
                          <div class="order-basic-info">
                            <h3>{{ order.orderName }}</h3>
                            <div class="order-id">订单号：{{ order.code }}</div>
                          </div>
                          <div class="order-status">
                            <a-tag color="green">已完成</a-tag>
                          </div>
                        </div>

                        <!-- 用户信息 -->
                        <div class="info-row">
                          <div class="info-item user-info">
                            <div class="info-label">乘客信息</div>
                            <div class="info-value" style="margin-left: 15px">
                              <div class="user-name">{{ order.userInfo.name }}</div>
                              <div class="user-phone">{{ order.userInfo.phone }}</div>
                            </div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">下单时间</div>
                            <div class="info-value">{{ order.createDate }}</div>
                          </div>
                        </div>

                        <!-- 订单金额 -->
                        <div class="info-row">
                          <div class="info-item">
                            <div class="info-label">订单金额</div>
                            <div class="info-value amount">¥{{ order.orderPrice }}</div>
                          </div>
                          <div class="info-item" v-if="order.afterOrderPrice && order.afterOrderPrice !== order.orderPrice">
                            <div class="info-label">优惠后金额</div>
                            <div class="info-value amount discount">¥{{ order.afterOrderPrice }}</div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">里程</div>
                            <div class="info-value">{{ order.kilometre }} km</div>
                          </div>
                        </div>

                        <!-- 路线信息 -->
                        <div class="route-info-wrapper">
                          <div class="info-label">路线详情</div>
                          <div class="route-details">
                            <div class="route-item">
                              <i class="icon start-icon">起点</i>
                              <span>{{ order.routeInfo.startAddress }}</span>
                            </div>
                            <div class="route-item">
                              <i class="icon end-icon">终点</i>
                              <span>{{ order.routeInfo.endAddress }}</span>
                            </div>
                            <div class="route-distance">距离：{{ order.routeInfo.distance }} km</div>
                          </div>
                        </div>

                        <!-- 评价信息 -->
                        <div class="evaluation-wrapper" v-if="order.evaluateInfo">
                          <div class="info-label">评价信息</div>
                          <div class="evaluation-details">
                            <a-rate :value="order.evaluateInfo.score" disabled/>
                            <div class="evaluation-content">{{ order.evaluateInfo.content }}</div>
                          </div>
                        </div>
                      </a-card>
                    </div>
                  </a-tab-pane>

                  <a-tab-pane key="cancelled" tab="已取消">
                    <div v-for="order in getOrdersByStatus('4')" :key="order.id" class="order-item">
                      <a-card class="order-inner-card">
                        <div class="order-header">
                          <div class="order-basic-info">
                            <h3>{{ order.orderName }}</h3>
                            <div class="order-id">订单号：{{ order.code }}</div>
                          </div>
                          <div class="order-status">
                            <a-tag color="red">已取消</a-tag>
                          </div>
                        </div>

                        <!-- 用户信息 -->
                        <div class="info-row">
                          <div class="info-item user-info">
                            <div class="info-label">乘客信息</div>
                            <div class="info-value" style="margin-left: 15px">
                              <div class="user-name">{{ order.userInfo.name }}</div>
                              <div class="user-phone">{{ order.userInfo.phone }}</div>
                            </div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">下单时间</div>
                            <div class="info-value">{{ order.createDate }}</div>
                          </div>
                        </div>

                        <!-- 订单金额 -->
                        <div class="info-row">
                          <div class="info-item">
                            <div class="info-label">订单金额</div>
                            <div class="info-value amount">¥{{ order.orderPrice }}</div>
                          </div>
                          <div class="info-item" v-if="order.afterOrderPrice && order.afterOrderPrice !== order.orderPrice">
                            <div class="info-label">优惠后金额</div>
                            <div class="info-value amount discount">¥{{ order.afterOrderPrice }}</div>
                          </div>
                          <div class="info-item">
                            <div class="info-label">里程</div>
                            <div class="info-value">{{ order.kilometre }} km</div>
                          </div>
                        </div>

                        <!-- 路线信息 -->
                        <div class="route-info-wrapper">
                          <div class="info-label">路线详情</div>
                          <div class="route-details">
                            <div class="route-item">
                              <i class="icon start-icon">起点</i>
                              <span>{{ order.routeInfo.startAddress }}</span>
                            </div>
                            <div class="route-item">
                              <i class="icon end-icon">终点</i>
                              <span>{{ order.routeInfo.endAddress }}</span>
                            </div>
                            <div class="route-distance">距离：{{ order.routeInfo.distance }} km</div>
                          </div>
                        </div>

                        <!-- 评价信息 -->
                        <div class="evaluation-wrapper" v-if="order.evaluateInfo">
                          <div class="info-label">评价信息</div>
                          <div class="evaluation-details">
                            <a-rate :value="order.evaluateInfo.score" disabled/>
                            <div class="evaluation-content">{{ order.evaluateInfo.content }}</div>
                          </div>
                        </div>
                      </a-card>
                    </div>
                  </a-tab-pane>
                </a-tabs>
              </div>
            </div>
            <div v-else class="no-orders">
              当前没有订单信息
            </div>
          </div>
        </a-col>
      </a-row>
    </div>
    <!-- 行程详情模态框 -->
    <a-modal
      v-model="routeDetailModalVisible"
      title="行程详情"
      :width="600"
      @cancel="closeRouteDetailModal"
      :footer="null"
    >
      <div v-if="selectedRouteDetail" class="route-detail-content">
        <div class="detail-row">
          <div class="detail-item">
            <div class="info-label">乘客姓名</div>
            <div class="info-value">{{ selectedRouteDetail.userInfo.name }}</div>
          </div>
          <div class="detail-item">
            <div class="info-label">联系电话</div>
            <div class="info-value">{{ selectedRouteDetail.userInfo.phone || '未提供' }}</div>
          </div>
        </div>

        <!-- 添加新字段到模态框 -->
        <div class="detail-row">
          <div class="detail-item">
            <div class="info-label">服务类型</div>
            <div class="info-value">
              <a-tag v-if="selectedRouteDetail.type === '0'" color="blue">拼座</a-tag>
              <a-tag v-if="selectedRouteDetail.type === '1'" color="green">独享</a-tag>
              <span v-if="!selectedRouteDetail.type">-</span>
            </div>
          </div>
          <div class="detail-item">
            <div class="info-label">高速费</div>
            <div class="info-value">
              <a-tag v-if="selectedRouteDetail.highwayTolls === '0'" color="orange">部分协商</a-tag>
              <a-tag v-if="selectedRouteDetail.highwayTolls === '1'" color="blue">全部承担</a-tag>
              <a-tag v-if="selectedRouteDetail.highwayTolls === '2'" color="red">不承担</a-tag>
              <span v-if="!selectedRouteDetail.highwayTolls">-</span>
            </div>
          </div>
        </div>

        <div class="detail-row">
          <div class="detail-item">
            <div class="info-label">起点距离</div>
            <div class="info-value">{{ selectedRouteDetail.startDistance || 0 }} km</div>
          </div>
          <div class="detail-item">
            <div class="info-label">终点距离</div>
            <div class="info-value">{{ selectedRouteDetail.endDistance || 0 }} km</div>
          </div>
        </div>

        <div class="detail-row">
          <div class="detail-item">
            <div class="info-label">起点</div>
            <div class="info-value">{{ selectedRouteDetail.startAddress }}</div>
          </div>
          <div class="detail-item">
            <div class="info-label">终点</div>
            <div class="info-value">{{ selectedRouteDetail.endAddress }}</div>
          </div>
        </div>

        <div class="detail-row">
          <div class="detail-item">
            <div class="info-label">出发时间</div>
            <div class="info-value">{{ selectedRouteDetail.earliestTime }}</div>
          </div>
          <div class="detail-item">
            <div class="info-label">最晚到达</div>
            <div class="info-value">{{ selectedRouteDetail.latestTime }}</div>
          </div>
        </div>

        <div class="detail-row">
          <div class="detail-item">
            <div class="info-label">距离</div>
            <div class="info-value">{{ selectedRouteDetail.distance }} km</div>
          </div>
          <div class="detail-item">
            <div class="info-label">可乘人数</div>
            <div class="info-value">{{ selectedRouteDetail.rideNum }}</div>
          </div>
        </div>

        <div class="detail-row">
          <div class="detail-item">
            <div class="info-label">匹配度</div>
            <div class="info-value match-rate-value">{{ selectedRouteDetail.matchRate }}%</div>
          </div>
          <div class="detail-item">
            <div class="info-label">创建时间</div>
            <div class="info-value">{{ selectedRouteDetail.createDate }}</div>
          </div>
        </div>

        <div class="detail-row" v-if="selectedRouteDetail.remark">
          <div class="detail-item full-width">
            <div class="info-label">备注</div>
            <div class="info-value remark-content">{{ selectedRouteDetail.remark }}</div>
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
  </div>
</template>
<script>import {mapState} from 'vuex'

export default {
  name: 'Staff',
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  data () {
    return {
      map: null,
      routeInfo: null,
      routeUserList: [], // 新增车找人订单列表
      routeDetailModalVisible: false, // 控制行程详情模态框
      selectedRouteDetail: null // 选中的行程详情
    }
  },
  mounted () {
    this.queryCurrentRouteByStaff()
  },
  methods: {
    initRouteMap () {
      this.map = new BMapGL.Map('route-map')
      this.map.centerAndZoom(new BMapGL.Point(116.404, 39.915), 12)
      this.map.enableScrollWheelZoom(true)

      // 解析路径数据
      let pathCoordinates = []
      if (this.selectedRouteDetail.path) {
        try {
          pathCoordinates = JSON.parse(this.selectedRouteDetail.path)
        } catch (e) {
          console.error('解析路径数据失败:', e)
          // 如果解析失败，回退到使用起终点坐标
          pathCoordinates = [
            { latitude: this.selectedRouteDetail.startLatitude, longitude: this.selectedRouteDetail.startLongitude },
            { latitude: this.selectedRouteDetail.endLatitude, longitude: this.selectedRouteDetail.endLongitude }
          ]
        }
      } else {
        // 如果没有路径数据，使用起终点坐标
        pathCoordinates = [
          { latitude: this.selectedRouteDetail.startLatitude, longitude: this.selectedRouteDetail.startLongitude },
          { latitude: this.selectedRouteDetail.endLatitude, longitude: this.selectedRouteDetail.endLongitude }
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
    },
    viewRoute (item) {
      this.selectedRouteDetail = item
      this.routeDetailModalVisible = true
      setTimeout(() => {
        this.initRouteMap()
      }, 200)
    },

    closeRouteDetailModal () {
      this.routeDetailModalVisible = false
      this.selectedRouteDetail = null
    },
    queryRouteUserList (staffRouteId) {
      this.$get('/business/route-staff-info/queryRouteUserList', {
        staffRouteId: staffRouteId
      }).then((r) => {
        // 按匹配度从大到小排序
        this.routeUserList = r.data.data.sort((a, b) => b.matchRate - a.matchRate)
      }).catch(error => {
        console.error('获取车找人订单失败:', error)
        this.$message.error('获取车找人订单失败')
      })
    },
    goToCreateRoute() {
      // 根据实际路由配置调整路径
      this.$router.push('/staff/routeStaff')
    },
    queryCurrentRouteByStaff () {
      this.$get('/business/order-info/queryCurrentRouteByStaff', {
        staffId: this.currentUser.userId
      }).then((r) => {
        this.routeInfo = r.data
        // 添加条件检查，确保对象存在且有id属性时才调用
        if (this.routeInfo && this.routeInfo.routeStaffInfo && this.routeInfo.routeStaffInfo.id) {
          this.queryRouteUserList(this.routeInfo.routeStaffInfo.id)
        }
      }).catch(error => {
        console.error('获取当前路线信息失败:', error)
        this.$message.error('获取当前路线信息失败')
        // 即使失败也设置routeInfo为默认值，以便页面正确渲染
        this.routeInfo = null
      })
    },

    getOrdersByStatus (status) {
      if (!this.routeInfo || !this.routeInfo.orderInfoList) {
        return []
      }
      return this.routeInfo.orderInfoList.filter(order => order.status === status)
    },

    contactUser (item) {
      // 联系乘客的逻辑
      this.$confirm({
        title: '联系乘客',
        content: `您即将联系乘客 ${item.userInfo.name}`,
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$post('/business/chat-record/defaultStaffChat', {
            staffId: this.currentUser.userId,
            userId: item.userInfo.id,
            senderType: 1,
            content: '你好'
          }).then((r) => {
            this.$message.success(`已发起联系请求给 ${item.userInfo.name}`)
            // 跳转到聊天页面，并传递默认消息
            this.$router.push({
              path: '/staff/message'
            })
          })
        }
      })
    }
  }
}
</script>

<style scoped>.staff-work-container {
  padding: 0;
  background-color: #ffffff;
}

.route-info-section,
.vehicle-info-section,
.staff-info-section,
.order-list-section {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.route-info-section {
  background-color: #fafafa;
}

.vehicle-info-section {
  background-color: #f9f9f9;
}

.staff-info-section {
  background-color: #f8f8f8;
}

.order-list-section {
  background-color: #ffffff;
  padding: 24px;
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

.route-item,
.vehicle-item,
.staff-item,
.detail-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}

.route-item.full-width,
.vehicle-item.full-width,
.staff-item.full-width,
.detail-item.full-width {
  grid-column: span 2;
}

.label {
  font-weight: 500;
  color: #595959;
  font-size: 13px;
  margin-bottom: 4px;
}

.value {
  font-size: 14px;
  color: #262626;
  word-break: break-all;
  line-height: 1.5;
}

.value.date {
  color: #8c8c8c;
  font-size: 13px;
}

.value.score {
  color: #52c41a;
  font-weight: 600;
  font-size: 15px;
}

.value.phone {
  color: #1890ff;
  font-weight: 500;
}

.value.id-number {
  font-family: monospace;
  letter-spacing: 1px;
}

.fuel-label {
  display: inline-block;
  padding: 2px 8px;
  /*border-radius: 12px;*/
  font-size: 12px;
  font-weight: 500;
  text-align: center;
}

.fuel-gasoline {
  background-color: #fffbe6;
  color: #faad14;
  border: 1px solid #ffe58f;
}

.fuel-diesel {
  background-color: #fff2e8;
  color: #ff7a45;
  border: 1px solid #ffd8bf;
}

.fuel-hybrid {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.fuel-electric {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.use-type {
  display: inline-block;
  padding: 2px 8px;
  /*border-radius: 4px;*/
  font-size: 12px;
  font-weight: 500;
}

.operational {
  background-color: #fff1f0;
  color: #ff4d4f;
  border: 1px solid #ffa39e;
}

.non-operational {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.order-inner-card {
  /*border-radius: 6px;*/
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  border: 1px solid #e8e8e8;
  margin-bottom: 15px;
  width: 100%;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-header h3 {
  margin: 0;
  color: #333;
}

.route-sub-info {
  background-color: #f9f9f9;
  padding: 8px;
  border-radius: 1px;
  border-left: 5px solid #1890ff;
}

.route-sub-info div {
  margin-bottom: 4px;
  font-size: 13px;
  color: #555;
}

.route-sub-info div:last-child {
  margin-bottom: 0;
}

.evaluation {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  background-color: #f6ffed;
  padding: 8px;
  border-radius: 1px;
  border-left: 5px solid #52c41a;
}

.evaluation-content {
  margin-left: 10px;
  color: #5a6b5a;
  font-size: 13px;
}

.no-orders {
  text-align: center;
  color: #8c8c8c;
  font-style: italic;
  padding: 20px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .route-item.full-width,
  .vehicle-item.full-width,
  .staff-item.full-width,
  .detail-item.full-width {
    grid-column: span 1;
  }

  .route-info-section,
  .vehicle-info-section,
  .staff-info-section,
  .order-list-section {
    padding: 16px;
  }

  .section-title {
    font-size: 15px;
  }
}

.order-list-section {
  background-color: #ffffff;
  padding: 24px;
}

.order-inner-card {
  /*border-radius: 8px;*/
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e8e8e8;
  margin-bottom: 16px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-basic-info {
  flex: 1;
}

.order-basic-info h3 {
  margin: 0 0 4px 0;
  color: #1d3557;
  font-size: 16px;
  font-weight: 600;
}

.order-id {
  font-size: 12px;
  color: #8c8c8c;
}

.order-status {
  flex-shrink: 0;
  margin-left: 16px;
}

.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 16px;
}

.info-item {
  flex: 1;
  min-width: 150px;
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

.amount {
  font-weight: 600;
  color: #1890ff;
  font-size: 16px;
}

.discount {
  color: #f5222d;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  font-weight: 600;
  color: #262626;
}

.user-phone {
  color: #595959;
  font-size: 13px;
}

.route-info-wrapper {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f9f9f9;
  /*border-radius: 6px;*/
}

.route-details {
  margin-top: 8px;
}

.route-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
}

.route-item:last-child {
  margin-bottom: 0;
}

.icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 24px;
  background-color: #e6f7ff;
  color: #1890ff;
  /*border-radius: 12px;*/
  font-size: 12px;
  margin-right: 12px;
  flex-shrink: 0;
}

.start-icon::before {
  content: "起点";
}

.end-icon::before {
  content: "终点";
}

.route-distance {
  margin-top: 8px;
  /*padding-left: 62px;*/
  color: #8c8c8c;
  font-size: 13px;
}

.evaluation-wrapper {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.evaluation-details {
  margin-top: 8px;
  background-color: #f6ffed;
  padding: 12px;
  /*border-radius: 6px;*/
  border-left: 3px solid #52c41a;
}

.evaluation-content {
  margin-top: 8px;
  color: #5a6b5a;
  font-size: 14px;
  line-height: 1.5;
}

.no-orders {
  text-align: center;
  color: #8c8c8c;
  font-style: italic;
  padding: 40px 20px;
  background-color: #fafafa;
  /*border-radius: 8px;*/
  border: 1px dashed #d9d9d9;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .info-row {
    flex-direction: column;
    gap: 12px;
  }

  .order-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .order-status {
    margin-left: 0;
    margin-top: 8px;
  }

  .route-distance {
    padding-left: 0;
  }
}

.route-user-card {
  /*border-radius: 8px;*/
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e8e8e8;
  margin-bottom: 16px;
  overflow: hidden;
}

.route-user-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.route-user-info {
  flex: 1;
}

.user-name {
  font-weight: 600;
  color: #1d3557;
  font-size: 16px;
  margin-bottom: 4px;
}

.user-phone {
  color: #595959;
  font-size: 14px;
}

.match-rate {
  flex-shrink: 0;
}

.route-user-details {
  margin-bottom: 16px;
}

.detail-row {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 12px;
}

.detail-item {
  flex: 1;
  min-width: 150px;
}

.detail-item.full-width {
  flex: 0 0 100%;
}

.route-user-actions {
  text-align: right;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.route-user-list-container {
  height: 600px; /* 设置固定高度 */
  overflow-y: auto; /* 超出时显示垂直滚动条 */
  overflow-x: hidden; /* 隐藏水平滚动条 */
  padding-right: 8px; /* 为滚动条留出空间 */
}

.route-user-list-container::-webkit-scrollbar {
  width: 6px;
}

.route-user-list-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.route-user-list-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.route-user-list-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.route-detail-content {
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
.no-vehicle,
.no-staff {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  background-color: #fafafa;
  text-align: center;
}

.action-buttons {
  margin-top: 20px;
}
</style>
