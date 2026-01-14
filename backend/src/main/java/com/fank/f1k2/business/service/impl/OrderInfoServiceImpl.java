package com.fank.f1k2.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.dao.EvaluateInfoMapper;
import com.fank.f1k2.business.dao.VehicleInfoMapper;
import com.fank.f1k2.business.entity.*;
import com.fank.f1k2.business.dao.OrderInfoMapper;
import com.fank.f1k2.business.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fank.f1k2.common.utils.DistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    private final IUserInfoService userInfoService;

    private final IStaffInfoService staffInfoService;

    private final INotifyInfoService notifyInfoService;

    private final IRouteStaffInfoService routeStaffInfoService;

    private final IRouteInfoService routeInfoService;

    private final EvaluateInfoMapper evaluateInfoMapper;

    private final VehicleInfoMapper vehicleInfoMapper;

    /**
     * 分页获取订单信息
     *
     * @param page      分页对象
     * @param queryFrom 订单信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<OrderInfo> page, OrderInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }

    /**
     * 用户添加订单信息
     *
     * @param orderInfo 订单信息
     * @return 添加结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean orderAdd(OrderInfo orderInfo) {
        RouteStaffInfo routeStaffInfo = routeStaffInfoService.getById(orderInfo.getStaffRouteId());
        RouteInfo routeInfo = routeInfoService.getById(orderInfo.getUserRouteId());

        StaffInfo staffInfo = staffInfoService.getById(routeStaffInfo.getStaffId());
        UserInfo userInfo = userInfoService.getById(routeInfo.getUserId());

        orderInfo.setUserId(routeInfo.getUserId());
        orderInfo.setStaffId(routeStaffInfo.getStaffId());
        orderInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        orderInfo.setCode("ORD-" + System.currentTimeMillis());
        orderInfo.setStatus("-1");
        orderInfo.setOrderName(DateUtil.formatChineseDate(new Date(), true, false) + " 用户【" + userInfo.getName() + "】 " + routeInfo.getStartAddress() + " 到 " + routeInfo.getEndAddress());

        // 车主发送消息
        NotifyInfo notifyInfo = new NotifyInfo();
        notifyInfo.setStaffId(staffInfo.getId());
        notifyInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        notifyInfo.setContent("用户【" + userInfo.getName() + "】 " + routeInfo.getStartAddress() + " 到 " + routeInfo.getEndAddress() + " 订单已下单，请尽快处理");
        notifyInfo.setDelFlag(0);
        notifyInfoService.save(notifyInfo);
        return this.save(orderInfo);
    }

    /**
     * 车主审核订单
     *
     * @param orderId 订单ID
     * @param status  订单状态
     * @return 订单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditOrderByStaff(Integer orderId, String status) {
        // 获取订单信息
        OrderInfo orderInfo = this.getById(orderId);
        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }
        // 获取关联的用户信息
        UserInfo userInfo = userInfoService.getById(orderInfo.getUserId());
        if (userInfo == null) {
            throw new RuntimeException("用户信息不存在");
        }
        if ("0".equals(status)) {
            // 接单操作
            NotifyInfo notifyInfo = new NotifyInfo();
            notifyInfo.setUserId(userInfo.getId());
            notifyInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
            notifyInfo.setContent("车主已接单，订单【" + orderInfo.getCode() + "】已确认，请耐心等待");
            notifyInfo.setDelFlag(0);
            this.setOrderDeliverIndexNo(orderInfo.getStaffRouteId());
            // 更新行程状态
            routeInfoService.update(Wrappers.<RouteInfo>lambdaUpdate().set(RouteInfo::getOrderId, orderId).eq(RouteInfo::getId, orderInfo.getUserRouteId()));
            notifyInfoService.save(notifyInfo);
        } else {
            // 拒绝操作
            orderInfo.setStatus("4");
            NotifyInfo notifyInfo = new NotifyInfo();
            notifyInfo.setUserId(userInfo.getId());
            notifyInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
            notifyInfo.setContent("很抱歉，车主拒绝了订单【" + orderInfo.getCode() + "】，您可以重新发布行程");
            notifyInfo.setDelFlag(0);
            notifyInfoService.save(notifyInfo);
        }
        // 更新订单状态
        return this.updateById(orderInfo);
    }

    /**
     * 用户取消订单
     *
     * @param orderId 订单ID
     * @return 订单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Integer orderId) {
        // 获取订单信息
        OrderInfo orderInfo = this.getById(orderId);
        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }
        // 获取关联的车主信息
        StaffInfo staffInfo = staffInfoService.getById(orderInfo.getStaffId());
        if (staffInfo == null) {
            throw new RuntimeException("车主信息不存在");
        }
        // 检查订单是否已经完成或已被取消，防止重复操作
        if ("3".equals(orderInfo.getStatus()) || "4".equals(orderInfo.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }
        this.setOrderDeliverIndexNo(orderInfo.getStaffRouteId());
        // 更新行程状态
        routeInfoService.update(Wrappers.<RouteInfo>lambdaUpdate().set(RouteInfo::getOrderId, null).eq(RouteInfo::getId, orderInfo.getUserRouteId()));
        // 更新订单状态为已取消（假定用状态码"5"表示已取消）
        orderInfo.setStatus("5");
        // 向车主发送取消通知
        NotifyInfo notifyInfo = new NotifyInfo();
        notifyInfo.setStaffId(staffInfo.getId());
        notifyInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        notifyInfo.setContent("用户已取消订单【" + orderInfo.getCode() + "】，订单已失效");
        notifyInfo.setDelFlag(0);
        notifyInfoService.save(notifyInfo);
        // 更新订单
        return this.updateById(orderInfo);
    }

    /**
     * 查询用户行程订单信息详情
     *
     * @param routeId 行程ID
     * @return 订单信息
     */
    @Override
    public LinkedHashMap<String, Object> queryRouteUserDetail(Integer routeId) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        // 查询用户路线信息
        RouteInfo routeInfo = routeInfoService.getById(routeId);
        if (routeInfo != null) {
            UserInfo userInfo = userInfoService.getById(routeInfo.getUserId());
            if (routeInfo.getOrderId() != null) {
                OrderInfo orderInfo = this.getById(routeInfo.getOrderId());
                StaffInfo staffInfo = staffInfoService.getById(orderInfo.getStaffId());
                RouteStaffInfo routeStaffInfo = routeStaffInfoService.getById(orderInfo.getStaffRouteId());

                // 计算开始距离公里数
                double startDistance = DistanceUtil.calculateDistanceInKilometer(routeStaffInfo.getStartLongitude().doubleValue(), routeStaffInfo.getStartLatitude().doubleValue(), routeInfo.getStartLongitude().doubleValue(), routeInfo.getStartLatitude().doubleValue());
                double endDistance = DistanceUtil.calculateDistanceInKilometer(routeStaffInfo.getEndLongitude().doubleValue(), routeStaffInfo.getEndLatitude().doubleValue(), routeInfo.getStartLongitude().doubleValue(), routeInfo.getEndLatitude().doubleValue());
                routeStaffInfo.setStartDistance(startDistance);
                routeStaffInfo.setEndDistance(endDistance);

                EvaluateInfo evaluateInfo = evaluateInfoMapper.selectOne(Wrappers.<EvaluateInfo>lambdaQuery().eq(EvaluateInfo::getOrderId, orderInfo.getId()));
                VehicleInfo vehicleInfo = vehicleInfoMapper.selectById(routeStaffInfo.getVehicleId());
                result.put("orderInfo", orderInfo);
                result.put("staffInfo", staffInfo);
                result.put("vehicleInfo", vehicleInfo);
                result.put("evaluateInfo", evaluateInfo);
                result.put("routeStaffInfo", routeStaffInfo);
            }
            // 组装返回数据
            result.put("userInfo", userInfo);
            result.put("routeInfo", routeInfo);
        }
        return result;
    }

    /**
     * 获取车主行程订单信息详情
     *
     * @param routeId 行程ID
     * @return 订单信息
     */
    @Override
    public LinkedHashMap<String, Object> queryRouteStaffDetail(Integer routeId) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        // 查询车主路线信息
        RouteStaffInfo routeStaffInfo = routeStaffInfoService.getById(routeId);
        if (routeStaffInfo != null) {
            StaffInfo staffInfo = staffInfoService.getById(routeStaffInfo.getStaffId());
            VehicleInfo vehicleInfo = vehicleInfoMapper.selectById(routeStaffInfo.getVehicleId());
            List<OrderInfo> orderInfoList = this.list(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getStaffRouteId, routeId));
            List<Integer> orderIdList = orderInfoList.stream().map(OrderInfo::getId).collect(Collectors.toList());
            List<EvaluateInfo> evaluateInfoList = evaluateInfoMapper.selectList(Wrappers.<EvaluateInfo>lambdaQuery().in(EvaluateInfo::getOrderId, orderIdList));
            if (CollectionUtil.isNotEmpty(evaluateInfoList)) {
                Map<Integer, EvaluateInfo> evaluateInfoMap = evaluateInfoList.stream().collect(Collectors.toMap(EvaluateInfo::getOrderId, evaluateInfo -> evaluateInfo));
                orderInfoList.forEach(orderInfo -> {
                    orderInfo.setEvaluateInfo(evaluateInfoMap.get(orderInfo.getId()));
                });
            }
            if (CollectionUtil.isNotEmpty(orderInfoList)) {
                List<Integer> userRouteIdList = orderInfoList.stream().map(OrderInfo::getUserRouteId).collect(Collectors.toList());
                List<Integer> userIdList = orderInfoList.stream().map(OrderInfo::getUserRouteId).collect(Collectors.toList());

                List<UserInfo> userInfoList = new ArrayList<>(userInfoService.listByIds(userIdList));
                List<RouteInfo> routeInfoList = new ArrayList<>(routeInfoService.listByIds(userRouteIdList));
                Map<Integer, RouteInfo> routeInfoMap = routeInfoList.stream().collect(Collectors.toMap(RouteInfo::getId, routeInfo -> routeInfo));
                Map<Integer, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getId, userInfo -> userInfo));
                orderInfoList.forEach(orderInfo -> {
                    orderInfo.setUserInfo(userInfoMap.get(orderInfo.getUserId()));
                    RouteInfo routeInfo = routeInfoMap.get(orderInfo.getUserRouteId());

                    // 计算开始距离公里数
                    double startDistance = DistanceUtil.calculateDistanceInKilometer(routeStaffInfo.getStartLongitude().doubleValue(), routeStaffInfo.getStartLatitude().doubleValue(), routeInfo.getStartLongitude().doubleValue(), routeInfo.getStartLatitude().doubleValue());
                    double endDistance = DistanceUtil.calculateDistanceInKilometer(routeStaffInfo.getEndLongitude().doubleValue(), routeStaffInfo.getEndLatitude().doubleValue(), routeInfo.getStartLongitude().doubleValue(), routeInfo.getEndLatitude().doubleValue());
                    routeStaffInfo.setStartDistance(startDistance);
                    routeStaffInfo.setEndDistance(endDistance);

                    orderInfo.setRouteInfo(routeInfo);
                });
                result.put("orderInfoList", orderInfoList);
            }
            result.put("routeStaffInfo", routeStaffInfo);
            result.put("staffInfo", staffInfo);
            result.put("vehicleInfo", vehicleInfo);
        }
        return result;
    }

    /**
     * 设置订单配送序号-基于贪心算法
     *
     * @param routeId 路线ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean setOrderDeliverIndexNo(Integer routeId) {
        // 查询车主路线信息
        RouteStaffInfo routeStaffInfo = routeStaffInfoService.getById(routeId);
        List<Integer> statusList = Arrays.asList(0, 1, 2, 3);
        List<OrderInfo> orderInfoList = this.list(Wrappers.<OrderInfo>lambdaQuery()
                .eq(OrderInfo::getStaffRouteId, routeId)
                .in(OrderInfo::getStatus, statusList));

        if (CollectionUtil.isEmpty(orderInfoList)) {
            return false;
        }
        // 获取所有相关路线信息
        List<Integer> userRouteIdList = orderInfoList.stream()
                .map(OrderInfo::getUserRouteId)
                .collect(Collectors.toList());
        List<RouteInfo> routeInfoList = new ArrayList<>(routeInfoService.listByIds(userRouteIdList));
        Map<Integer, RouteInfo> routeInfoMap = routeInfoList.stream()
                .collect(Collectors.toMap(RouteInfo::getId, routeInfo -> routeInfo));
        // 创建订单与路线的映射关系
        List<OrderWithLocation> ordersWithLocations = new ArrayList<>();
        for (OrderInfo orderInfo : orderInfoList) {
            RouteInfo routeInfo = routeInfoMap.get(orderInfo.getUserRouteId());
            if (routeInfo != null) {
                ordersWithLocations.add(new OrderWithLocation(orderInfo, routeInfo));
            }
        }
        // 使用贪心算法找到最顺路的配送顺序
        List<OrderWithLocation> optimizedOrderList = optimizeDeliverySequence(
                routeStaffInfo, ordersWithLocations);
        // 设置配送顺序编号
        for (int i = 0; i < optimizedOrderList.size(); i++) {
            OrderWithLocation orderWithLocation = optimizedOrderList.get(i);
            OrderInfo orderInfo = orderWithLocation.getOrderInfo();
            orderInfo.setDeliverIndexNo(i + 1);
            this.updateById(orderInfo);
        }

        return true;
    }

    /**
     * 使用贪心算法优化配送顺序
     */
    private List<OrderWithLocation> optimizeDeliverySequence(RouteStaffInfo routeStaffInfo, List<OrderWithLocation> ordersWithLocations) {
        if (ordersWithLocations.isEmpty()) {
            return ordersWithLocations;
        }
        List<OrderWithLocation> remainingOrders = new ArrayList<>(ordersWithLocations);
        List<OrderWithLocation> optimizedOrder = new ArrayList<>();
        // 当前位置初始化为车主起点
        double currentLng = routeStaffInfo.getStartLongitude().doubleValue();
        double currentLat = routeStaffInfo.getStartLatitude().doubleValue();

        while (!remainingOrders.isEmpty()) {
            // 找到最近的订单
            OrderWithLocation nearestOrder = null;
            double minDistance = Double.MAX_VALUE;
            for (OrderWithLocation order : remainingOrders) {
                double distance = DistanceUtil.calculateDistanceInKilometer(
                        currentLng, currentLat,
                        order.getRouteInfo().getStartLongitude().doubleValue(),
                        order.getRouteInfo().getStartLatitude().doubleValue());

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestOrder = order;
                }
            }
            // 将最近的订单加入优化序列
            optimizedOrder.add(nearestOrder);
            remainingOrders.remove(nearestOrder);
            // 更新当前位置为该订单的下车点
            currentLng = nearestOrder.getRouteInfo().getEndLongitude().doubleValue();
            currentLat = nearestOrder.getRouteInfo().getEndLatitude().doubleValue();
        }
        return optimizedOrder;
    }

    // 更新辅助类
    private static class OrderWithLocation {
        private final OrderInfo orderInfo;
        private final RouteInfo routeInfo;

        public OrderWithLocation(OrderInfo orderInfo, RouteInfo routeInfo) {
            this.orderInfo = orderInfo;
            this.routeInfo = routeInfo;
        }

        // Getters
        public OrderInfo getOrderInfo() {
            return orderInfo;
        }

        public RouteInfo getRouteInfo() {
            return routeInfo;
        }
    }
}
