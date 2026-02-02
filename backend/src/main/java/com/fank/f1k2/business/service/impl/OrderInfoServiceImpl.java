package com.fank.f1k2.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
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
        orderInfo.setRemark(routeInfo.getRemark());
        orderInfo.setKilometre(routeInfo.getDistance());
        orderInfo.setOrderPrice(routeStaffInfo.getPlanPriceUnit());
        orderInfo.setDeliverIndexNo(0);
        orderInfo.setIntegral(routeStaffInfo.getPlanPriceUnit());

        if ("0".equals(routeStaffInfo.getAutoOrder())) {
            orderInfo.setStatus("-1");
        } else {
            orderInfo.setStatus("0");
        }
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
            // 取消用户此行程其他订单
            this.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getStatus, "5").eq(OrderInfo::getUserRouteId, orderInfo.getUserRouteId()).ne(OrderInfo::getId, orderId));
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
     * 检查订单状态
     *
     * @param orderId 订单ID
     */
    @Override
    public void checkOrderStatus(Integer orderId) {
        OrderInfo orderInfo = this.getById(orderId);
        // 获取车主路线
        RouteStaffInfo routeStaffInfo = routeStaffInfoService.getById(orderInfo.getStaffRouteId());
        // 获取用户路线
        RouteInfo routeInfo = routeInfoService.getById(orderInfo.getUserRouteId());
        if ("4".equals(orderInfo.getStatus()) || "5".equals(orderInfo.getStatus())) {
            routeInfo.setStatus("-1");
        } else {
            routeInfo.setStatus(orderInfo.getStatus());
        }
        routeInfoService.updateById(routeInfo);

        List<OrderInfo> orderInfoList = this.list(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getStaffRouteId, routeStaffInfo.getId()));
        // 校验状态是否已经全部支付
        if (orderInfoList.size() == orderInfoList.stream().filter(order -> "3".equals(order.getStatus())).count()) {
            routeStaffInfo.setStatus("1");
        }
        routeStaffInfoService.updateById(routeStaffInfo);
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

    /**
     * 获取主页统计数据
     *
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> homeData(Integer userId) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        // 实时在线车主数
        result.put("onlineStaffData", getOnlineStaffCount());

        // 实时发单量和接单量
        result.put("realtimeOrderData", getRealtimeOrderStats());

        // 当前月度交易分析
        String currentMonth = DateUtil.format(new Date(), "yyyy-MM");
        result.put("monthlyTransactionData", monthlyTransactionAnalysis(currentMonth));
        return result;
    }

    /**
     * 获取实时在线车主数
     *
     * @return 实时在线车主数
     */
    public LinkedHashMap<String, Object> getOnlineStaffCount() {
        List<StaffInfo> onlineStaffs = staffInfoService.list(
                new LambdaQueryWrapper<StaffInfo>()
                        .eq(StaffInfo::getStatus, "1")
        );

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("onlineStaffCount", onlineStaffs.size()); // 实时在线车主数
        result.put("timestamp", new Date()); // 当前时间戳
        result.put("description", "实时在线车主数，评估平台当前的履约能力");

        return result;
    }

    /**
     * 获取实时发单量和接单量统计
     *
     * @return 发单量和接单量数据
     */
    public LinkedHashMap<String, Object> getRealtimeOrderStats() {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Date today = new Date();

        // 查询今日发单量（乘客）
        List<RouteInfo> todayPassengerOrders = routeInfoService.list(
                new LambdaQueryWrapper<RouteInfo>()
                        .apply("DATE(create_date) = DATE({0})", today)
        );

        // 查询今日接单量（车主）
        List<OrderInfo> todayStaffOrders = this.list(
                new LambdaQueryWrapper<OrderInfo>()
                        .apply("DATE(create_date) = DATE({0})", today)
        );

        // 按小时统计发单量和接单量
        List<Integer> hourlyPassengerOrders = new ArrayList<>();
        List<Integer> hourlyStaffOrders = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            int finalHour = hour;
            int passengerCount = (int) todayPassengerOrders.stream()
                    .filter(order -> DateUtil.hour(DateUtil.parseDate(order.getCreateDate()), true) == finalHour)
                    .count();
            hourlyPassengerOrders.add(passengerCount);

            int staffCount = (int) todayStaffOrders.stream()
                    .filter(order -> DateUtil.hour(DateUtil.parseDate(order.getCreateDate()), true) == finalHour)
                    .count();
            hourlyStaffOrders.add(staffCount);
        }

        // 计算供需比
        double supplyDemandRatio = todayStaffOrders.size() > 0 ?
                (double) todayPassengerOrders.size() / todayStaffOrders.size() : Double.MAX_VALUE;

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("todayPassengerOrders", todayPassengerOrders.size()); // 今日发单量
        result.put("todayStaffOrders", todayStaffOrders.size());       // 今日接单量
        result.put("supplyDemandRatio", supplyDemandRatio);           // 供需比
        result.put("hourlyPassengerOrders", hourlyPassengerOrders);   // 按小时统计的发单量
        result.put("hourlyStaffOrders", hourlyStaffOrders);          // 按小时统计的接单量
        result.put("timestamp", new Date());                         // 当前时间戳

        // 判断是否需要调度补贴
        boolean needsSubsidy = supplyDemandRatio > 2.0; // 当供需比超过2:1时，考虑发放调度补贴
        result.put("needsSubsidy", needsSubsidy);
        result.put("subsidyRecommendation", needsSubsidy ?
                "供需失衡，建议发放调度补贴吸引车主接单" : "供需平衡，无需调度补贴");

        return result;
    }

    /**
     * 月度交易分析
     *
     * @param dateStr 日期字符串（YYYY-MM格式）
     * @return 月度交易分析数据
     */
    public LinkedHashMap<String, Object> monthlyTransactionAnalysis(String dateStr) {
        String date = dateStr + "-01";
        Date analysisDate = DateUtil.parseDate(date);
        int year = DateUtil.year(analysisDate);
        int month = DateUtil.month(analysisDate) + 1;

        // 查询指定月份的交易数据
        List<RouteInfo> transactions = routeInfoService.list(
                new LambdaQueryWrapper<RouteInfo>()
                        .apply("DATE_FORMAT(create_date, '%Y%m') = {0}",
                                year + "" + ((month < 10) ? "0" + month : month))
        );

        // 计算交易总额
        BigDecimal totalAmount = transactions.stream()
                .filter(e -> "-1".equals(e.getStatus()) && e.getDistance() != null)
                .map(e -> e.getDistance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 按公里数区间统计
        Map<String, Integer> distanceRangeCount = new LinkedHashMap<>();
        Map<String, BigDecimal> distanceRangeAmount = new LinkedHashMap<>();

        // 初始化各区间
        String[] ranges = {"0-100", "100-300", "300-500", "500-800", "800-1500", "1500+"};
        for (String range : ranges) {
            distanceRangeCount.put(range, 0);
            distanceRangeAmount.put(range, BigDecimal.ZERO);
        }

        // 统计各区间数据
        for (RouteInfo transaction : transactions) {
            if (transaction.getDistance() != null) {
                double distance = transaction.getDistance().doubleValue();
                String range = getDistanceRange(distance);

                distanceRangeCount.put(range, distanceRangeCount.get(range) + 1);
                if ("-1".equals(transaction.getStatus())) {
                    distanceRangeAmount.put(range,
                            distanceRangeAmount.get(range).add(transaction.getDistance()));
                }
            }
        }

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("year", year);
        result.put("month", month);
        result.put("totalTransactions", transactions.size()); // 总交易数
        result.put("totalAmount", totalAmount);              // 交易总额
        result.put("distanceRangeCount", distanceRangeCount); // 各区间交易次数
        result.put("distanceRangeAmount", distanceRangeAmount); // 各区间交易金额

        return result;
    }

    /**
     * 根据距离获取所属区间
     *
     * @param distance 距离
     * @return 区间字符串
     */
    private String getDistanceRange(double distance) {
        if (distance < 100) {
            return "0-100";
        } else if (distance < 300) {
            return "100-300";
        } else if (distance < 500) {
            return "300-500";
        } else if (distance < 800) {
            return "500-800";
        } else if (distance < 1500) {
            return "800-1500";
        } else {
            return "1500+";
        }
    }

    /**
     * 年统计订单及收益
     *
     * @param date 年份
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectStoreStatisticsByYear(String date) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        int year = DateUtil.year(new Date());
        if (StrUtil.isNotEmpty(date)) {
            year = Integer.parseInt(date);
        }

        List<RouteInfo> scenicOrderList = routeInfoService.list(new LambdaQueryWrapper<RouteInfo>()
                .apply("DATE_FORMAT(create_date, '%Y') = {0}", year));

        for (RouteInfo scenicOrder : scenicOrderList) {
            scenicOrder.setMonth(DateUtil.month(DateUtil.parseDate(scenicOrder.getCreateDate())) + 1);
        }

        Map<Integer, List<RouteInfo>> orderOutMonthMap = scenicOrderList.stream()
                .collect(Collectors.groupingBy(RouteInfo::getMonth));

        // 按月统计的其他数据（如司机路线信息）
        List<RouteStaffInfo> hotelOrderList = routeStaffInfoService.list(
                new LambdaQueryWrapper<RouteStaffInfo>().apply("DATE_FORMAT(create_date, '%Y') = {0}", year));

        for (RouteStaffInfo orderInfo : hotelOrderList) {
            orderInfo.setMonth(DateUtil.month(DateUtil.parseDate(orderInfo.getCreateDate())) + 1);
        }

        Map<Integer, List<RouteStaffInfo>> orderPutMonthMap = hotelOrderList.stream()
                .collect(Collectors.groupingBy(RouteStaffInfo::getDay));

        // 本年订单量
        result.put("orderNum", scenicOrderList.size());

        // 本年总收益
        BigDecimal totalPrice = scenicOrderList.stream()
                .filter(e -> "-1".equals(e.getStatus()))
                .map(e -> e.getDistance() != null ? e.getDistance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalPrice", totalPrice);

        // 统计 highwayTolls 字段分布
        Map<String, Long> highwayTollsCountMap = scenicOrderList.stream()
                .filter(e -> e.getHighwayTolls() != null)
                .collect(Collectors.groupingBy(RouteInfo::getHighwayTolls, Collectors.counting()));
        result.put("highwayTollsCountMap", highwayTollsCountMap);

        // 统计 type 字段分布
        Map<String, Long> typeCountMap = scenicOrderList.stream()
                .filter(e -> e.getType() != null)
                .collect(Collectors.groupingBy(RouteInfo::getType, Collectors.counting()));
        result.put("typeCountMap", typeCountMap);

        result.put("putNum", hotelOrderList.size());

        // 计算平均距离
        double averageDistance = hotelOrderList.stream()
                .filter(item -> item != null && item.getDistance() != null)
                .mapToDouble(item -> item.getDistance().doubleValue())
                .average()
                .orElse(0.0);
        result.put("outlayPrice", averageDistance);

        List<Integer> orderNumList = new ArrayList<>();
        List<BigDecimal> orderPriceList = new ArrayList<>();
        List<Integer> outlayNumList = new ArrayList<>();
        List<BigDecimal> outlayPriceList = new ArrayList<>();

        // 月度 highwayTolls 和 type 分布
        List<Map<String, Integer>> monthlyHighwayTollsDistribution = new ArrayList<>();
        List<Map<String, Integer>> monthlyTypeDistribution = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            List<RouteInfo> currentMonthOutList = orderOutMonthMap.get(i);

            if (CollectionUtil.isEmpty(currentMonthOutList)) {
                orderNumList.add(0);
                orderPriceList.add(BigDecimal.ZERO);

                // 添加空的月度分布数据
                monthlyHighwayTollsDistribution.add(new HashMap<>());
                monthlyTypeDistribution.add(new HashMap<>());
            } else {
                orderNumList.add(currentMonthOutList.size());

                BigDecimal currentMonthOutPrice = currentMonthOutList.stream()
                        .filter(e -> e != null && e.getStatus() != null && !"-1".equals(e.getStatus()))
                        .map(e -> e.getDistance() != null ? e.getDistance() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                orderPriceList.add(currentMonthOutPrice);

                // 统计当月的 highwayTolls 分布
                Map<String, Long> monthlyHighwayTollsCount = currentMonthOutList.stream()
                        .filter(e -> e.getHighwayTolls() != null)
                        .collect(Collectors.groupingBy(RouteInfo::getHighwayTolls, Collectors.counting()));
                Map<String, Integer> monthlyHighwayTollsMap = new HashMap<>();
                for (Map.Entry<String, Long> entry : monthlyHighwayTollsCount.entrySet()) {
                    monthlyHighwayTollsMap.put(entry.getKey(), entry.getValue().intValue());
                }
                monthlyHighwayTollsDistribution.add(monthlyHighwayTollsMap);

                // 统计当月的 type 分布
                Map<String, Long> monthlyTypeCount = currentMonthOutList.stream()
                        .filter(e -> e.getType() != null)
                        .collect(Collectors.groupingBy(RouteInfo::getType, Collectors.counting()));
                Map<String, Integer> monthlyTypeMap = new HashMap<>();
                for (Map.Entry<String, Long> entry : monthlyTypeCount.entrySet()) {
                    monthlyTypeMap.put(entry.getKey(), entry.getValue().intValue());
                }
                monthlyTypeDistribution.add(monthlyTypeMap);
            }

            List<RouteStaffInfo> currentMonthPutList = orderPutMonthMap.get(i);
            if (CollectionUtil.isEmpty(currentMonthPutList)) {
                outlayNumList.add(0);
                outlayPriceList.add(BigDecimal.ZERO);
            } else {
                outlayNumList.add(currentMonthPutList.size());
                BigDecimal currentMonthPutPrice = currentMonthPutList.stream()
                        .filter(e -> e != null && e.getStatus() != null && !"0".equals(e.getStatus()))
                        .map(e -> e.getDistance() != null ? e.getDistance() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                outlayPriceList.add(currentMonthPutPrice);
            }
        }

        result.put("orderPriceList", orderPriceList);
        result.put("orderNumList", orderNumList);
        result.put("outlayPriceList", outlayPriceList);
        result.put("outlayNumList", outlayNumList);

        // 添加月度的 highwayTolls 和 type 分布数据
        result.put("monthlyHighwayTollsDistribution", monthlyHighwayTollsDistribution);
        result.put("monthlyTypeDistribution", monthlyTypeDistribution);

        return result;
    }

    /**
     * 月统计订单及收益
     *
     * @param dateStr 日期
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectStoreStatisticsByMonth(String dateStr) {
        String date = dateStr + "-01";
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        int year = DateUtil.year(new Date());
        int month = DateUtil.month(new Date()) + 1;
        if (StrUtil.isNotEmpty(date)) {
            year = DateUtil.year(DateUtil.parseDate(date));
            month = DateUtil.month(DateUtil.parseDate(date)) + 1;
        }

        List<RouteInfo> scenicOrderList = routeInfoService.list(new LambdaQueryWrapper<RouteInfo>().apply("DATE_FORMAT(create_date, '%Y%m') = {0}", (year + "" + ((month < 10) ? "0" + month : month))));
        for (RouteInfo scenicOrder : scenicOrderList) {
            scenicOrder.setMonth(DateUtil.month(DateUtil.parseDate(scenicOrder.getCreateDate())) + 1);
            scenicOrder.setDay(DateUtil.dayOfMonth(DateUtil.parseDate(scenicOrder.getCreateDate())));
        }
        Map<Integer, List<RouteInfo>> orderOutDayMap = scenicOrderList.stream().collect(Collectors.groupingBy(RouteInfo::getDay));
        List<RouteStaffInfo> hotelOrderList = routeStaffInfoService.list(new LambdaQueryWrapper<RouteStaffInfo>().apply("DATE_FORMAT(completion_time, '%Y%m') = {0}", (year + "" + ((month < 10) ? "0" + month : month))));
        for (RouteStaffInfo orderInfo : hotelOrderList) {
            orderInfo.setMonth(DateUtil.month(DateUtil.parseDate(orderInfo.getCreateDate())) + 1);
            orderInfo.setDay(DateUtil.dayOfMonth(DateUtil.parseDate(orderInfo.getCreateDate())));
        }
        Map<Integer, List<RouteStaffInfo>> orderPutDayMap = hotelOrderList.stream().collect(Collectors.groupingBy(RouteStaffInfo::getDay));

        // 本月订单量
        result.put("orderNum", scenicOrderList.size());
        // 本月总收益
        BigDecimal totalPrice = scenicOrderList.stream().filter(e -> "-1".equals(e.getStatus())).map(e -> e.getDistance() != null ? e.getDistance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalPrice", totalPrice);

        // 统计 highwayTolls 字段分布
        Map<String, Long> highwayTollsCountMap = scenicOrderList.stream()
                .filter(e -> e.getHighwayTolls() != null)
                .collect(Collectors.groupingBy(RouteInfo::getHighwayTolls, Collectors.counting()));
        result.put("highwayTollsCountMap", highwayTollsCountMap);

        // 统计 type 字段分布
        Map<String, Long> typeCountMap = scenicOrderList.stream()
                .filter(e -> e.getType() != null)
                .collect(Collectors.groupingBy(RouteInfo::getType, Collectors.counting()));
        result.put("typeCountMap", typeCountMap);

        result.put("putNum", hotelOrderList.size());

        // 计算平均分
        double averageDistance = hotelOrderList.stream()
                .filter(item -> item != null && item.getDistance() != null)
                .mapToDouble(item -> item.getDistance().doubleValue())
                .average()
                .orElse(0.0);
        result.put("outlayPrice", averageDistance);

        List<Integer> orderNumList = new ArrayList<>();
        List<BigDecimal> orderPriceList = new ArrayList<>();
        List<Integer> outlayNumList = new ArrayList<>();
        List<BigDecimal> outlayPriceList = new ArrayList<>();
        int days = DateUtil.getLastDayOfMonth(DateUtil.parseDate(date));

        // 本月日期
        List<String> dateTimeList = new ArrayList<>();

        // 为每天准备 highwayTolls 和 type 的分布数据
        List<Map<String, Integer>> dailyHighwayTollsDistribution = new ArrayList<>();
        List<Map<String, Integer>> dailyTypeDistribution = new ArrayList<>();

        for (int i = 1; i <= days; i++) {
            dateTimeList.add(month + "月" + i + "日");
            List<RouteInfo> currentDayOutList = orderOutDayMap.get(i);
            if (CollectionUtil.isEmpty(currentDayOutList)) {
                orderNumList.add(0);
                orderPriceList.add(BigDecimal.ZERO);

                // 添加空的当日分布数据
                dailyHighwayTollsDistribution.add(new HashMap<>());
                dailyTypeDistribution.add(new HashMap<>());
            } else {
                orderNumList.add(currentDayOutList.size());
                BigDecimal currentDayOutPrice = currentDayOutList.stream()
                        .filter(e -> e != null && e.getStatus() != null && !"-1".equals(e.getStatus()))
                        .map(e -> e.getDistance() != null ? e.getDistance() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                orderPriceList.add(currentDayOutPrice);

                // 统计当天的 highwayTolls 分布
                Map<String, Long> dailyHighwayTollsCount = currentDayOutList.stream()
                        .filter(e -> e.getHighwayTolls() != null)
                        .collect(Collectors.groupingBy(RouteInfo::getHighwayTolls, Collectors.counting()));
                Map<String, Integer> dailyHighwayTollsMap = new HashMap<>();
                for (Map.Entry<String, Long> entry : dailyHighwayTollsCount.entrySet()) {
                    dailyHighwayTollsMap.put(entry.getKey(), entry.getValue().intValue());
                }
                dailyHighwayTollsDistribution.add(dailyHighwayTollsMap);

                // 统计当天的 type 分布
                Map<String, Long> dailyTypeCount = currentDayOutList.stream()
                        .filter(e -> e.getType() != null)
                        .collect(Collectors.groupingBy(RouteInfo::getType, Collectors.counting()));
                Map<String, Integer> dailyTypeMap = new HashMap<>();
                for (Map.Entry<String, Long> entry : dailyTypeCount.entrySet()) {
                    dailyTypeMap.put(entry.getKey(), entry.getValue().intValue());
                }
                dailyTypeDistribution.add(dailyTypeMap);
            }

            // 本天入库
            List<RouteStaffInfo> currentDayPutList = orderPutDayMap.get(i);
            if (CollectionUtil.isEmpty(currentDayPutList)) {
                outlayNumList.add(0);
                outlayPriceList.add(BigDecimal.ZERO);
            } else {
                outlayNumList.add(currentDayPutList.size());
                BigDecimal currentDayPutPrice = currentDayPutList.stream()
                        .filter(e -> e != null && e.getStatus() != null && !"0".equals(e.getStatus()))
                        .map(e -> e.getDistance() != null ? e.getDistance() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                outlayPriceList.add(currentDayPutPrice);
            }
        }

        result.put("orderPriceList", orderPriceList);
        result.put("orderNumList", orderNumList);
        result.put("outlayPriceList", outlayPriceList);
        result.put("outlayNumList", outlayNumList);

        result.put("dateList", dateTimeList);

        // 添加每日的 highwayTolls 和 type 分布数据
        result.put("dailyHighwayTollsDistribution", dailyHighwayTollsDistribution);
        result.put("dailyTypeDistribution", dailyTypeDistribution);

        return result;
    }

    /**
     * 订单成功率
     *
     * @return 订单成功率
     */
    @Override
    public LinkedHashMap<String, Object> orderSuccessRate() {
        int currentYear = DateUtil.year(new Date());

        // 查询本年所有乘客发布的订单
        List<RouteInfo> allPassengerRoutes = routeInfoService.list(
                new LambdaQueryWrapper<RouteInfo>()
                        .apply("DATE_FORMAT(create_date, '%Y') = {0}", currentYear)
        );

        // 按月份分组订单
        Map<Integer, List<RouteInfo>> ordersByMonth = allPassengerRoutes.stream()
                .collect(Collectors.groupingBy(route ->
                        DateUtil.month(DateUtil.parseDate(route.getCreateDate())) + 1
                ));

        // 统计全年总订单数
        long totalOrders = allPassengerRoutes.size();

        // 统计全年已接单的订单数
        long successOrders = allPassengerRoutes.stream()
                .filter(route -> route.getOrderId() != null)
                .count();

        // 计算全年成功率
        double annualSuccessRate = totalOrders > 0 ? (double) successOrders / totalOrders * 100 : 0.0;

        // 初始化月度数据
        List<Integer> monthlyTotalOrders = new ArrayList<>();
        List<Integer> monthlySuccessOrders = new ArrayList<>();
        List<Double> monthlySuccessRates = new ArrayList<>();
        List<Double> monthlyGrowthRates = new ArrayList<>(); // 同比增长率
        List<Double> monthlyChainRates = new ArrayList<>();  // 环比增长率

        // 上一个月的成功率，用于计算环比
        double previousMonthRate = 0.0;

        // 计算各月数据
        for (int month = 1; month <= 12; month++) {
            List<RouteInfo> monthOrders = ordersByMonth.getOrDefault(month, new ArrayList<>());
            long monthTotal = monthOrders.size();
            long monthSuccess = monthOrders.stream()
                    .filter(route -> route.getOrderId() != null)
                    .count();
            double monthRate = monthTotal > 0 ? (double) monthSuccess / monthTotal * 100 : 0.0;

            monthlyTotalOrders.add((int) monthTotal);
            monthlySuccessOrders.add((int) monthSuccess);
            monthlySuccessRates.add(monthRate);

            // 计算同比增长率（与去年同月比较）
            // 查询去年同月的订单数据
            List<RouteInfo> lastYearMonthOrders = routeInfoService.list(
                    new LambdaQueryWrapper<RouteInfo>()
                            .apply("DATE_FORMAT(create_date, '%Y-%m') = {0}",
                                    currentYear - 1 + "-" + (month < 10 ? "0" + month : month))
            );
            long lastYearMonthTotal = lastYearMonthOrders.size();
            long lastYearMonthSuccess = lastYearMonthOrders.stream()
                    .filter(route -> route.getOrderId() != null)
                    .count();
            double lastYearMonthRate = lastYearMonthTotal > 0 ? (double) lastYearMonthSuccess / lastYearMonthTotal * 100 : 0.0;

            double growthRate = lastYearMonthRate != 0 ? (monthRate - lastYearMonthRate) / lastYearMonthRate * 100 : 0.0;
            monthlyGrowthRates.add(growthRate);

            // 计算环比增长率（与上月比较）
            double chainRate = previousMonthRate != 0 ? (monthRate - previousMonthRate) / previousMonthRate * 100 : 0.0;
            monthlyChainRates.add(chainRate);

            previousMonthRate = monthRate;
        }

        // 构建返回结果
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("year", currentYear);                   // 统计年份
        result.put("totalOrders", totalOrders);            // 全年总订单数
        result.put("successOrders", successOrders);        // 全年成功接单数
        result.put("failedOrders", totalOrders - successOrders); // 全年未接单数
        result.put("annualSuccessRate", annualSuccessRate); // 全年成功率

        // 月度数据
        result.put("monthlyTotalOrders", monthlyTotalOrders);     // 各月总订单数
        result.put("monthlySuccessOrders", monthlySuccessOrders); // 各月成功订单数
        result.put("monthlySuccessRates", monthlySuccessRates);   // 各月成功率
        result.put("monthlyGrowthRates", monthlyGrowthRates);     // 各月同比增长率
        result.put("monthlyChainRates", monthlyChainRates);       // 各月环比增长率

        return result;
    }

    /**
     * 订单拼成率
     *
     * @return 订单拼成率
     */
    @Override
    public LinkedHashMap<String, Object> assemblyRate() {
        int currentYear = DateUtil.year(new Date());

        // 查询本年所有已接单的订单
        List<OrderInfo> allOrders = this.list(
                new LambdaQueryWrapper<OrderInfo>()
                        .apply("DATE_FORMAT(create_date, '%Y') = {0}", currentYear)
        );

        // 按月份和司机路线ID分组订单
        Map<Integer, Map<Integer, List<OrderInfo>>> ordersByMonthAndRoute = allOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> DateUtil.month(DateUtil.parseDate(order.getCreateDate())) + 1,
                        Collectors.groupingBy(OrderInfo::getStaffRouteId)
                ));

        // 统计全年总司机路线数
        long totalRoutes = allOrders.stream()
                .collect(Collectors.groupingBy(OrderInfo::getStaffRouteId)).size();

        // 统计全年拼载路线数（一个司机路线上有2个及以上订单）
        long assemblyRoutes = allOrders.stream()
                .collect(Collectors.groupingBy(OrderInfo::getStaffRouteId))
                .values()
                .stream()
                .filter(orders -> orders.size() >= 2)
                .count();

        // 计算全年拼成率
        double annualAssemblyRate = totalRoutes > 0 ? (double) assemblyRoutes / totalRoutes * 100 : 0.0;

        // 初始化月度数据
        List<Integer> monthlyTotalRoutes = new ArrayList<>();
        List<Integer> monthlyAssemblyRoutes = new ArrayList<>();
        List<Double> monthlyAssemblyRates = new ArrayList<>();
        List<Double> monthlyGrowthRates = new ArrayList<>(); // 同比增长率
        List<Double> monthlyChainRates = new ArrayList<>();  // 环比增长率

        // 上一个月的拼成率，用于计算环比
        double previousMonthRate = 0.0;

        // 计算各月数据
        for (int month = 1; month <= 12; month++) {
            Map<Integer, List<OrderInfo>> monthOrdersByRoute = ordersByMonthAndRoute.getOrDefault(month, new HashMap<>());
            long monthTotalRoutes = monthOrdersByRoute.size();
            long monthAssemblyRoutes = monthOrdersByRoute.values().stream()
                    .filter(orders -> orders.size() >= 2)
                    .count();
            double monthAssemblyRate = monthTotalRoutes > 0 ? (double) monthAssemblyRoutes / monthTotalRoutes * 100 : 0.0;

            monthlyTotalRoutes.add((int) monthTotalRoutes);
            monthlyAssemblyRoutes.add((int) monthAssemblyRoutes);
            monthlyAssemblyRates.add(monthAssemblyRate);

            // 计算同比增长率（与去年同月比较）
            // 查询去年同月的订单数据
            List<OrderInfo> lastYearMonthOrders = this.list(
                    new LambdaQueryWrapper<OrderInfo>()
                            .apply("DATE_FORMAT(create_date, '%Y-%m') = {0}",
                                    currentYear - 1 + "-" + (month < 10 ? "0" + month : month))
            );
            Map<Integer, List<OrderInfo>> lastYearMonthOrdersByRoute = lastYearMonthOrders.stream()
                    .collect(Collectors.groupingBy(OrderInfo::getStaffRouteId));
            long lastYearMonthTotalRoutes = lastYearMonthOrdersByRoute.size();
            long lastYearMonthAssemblyRoutes = lastYearMonthOrdersByRoute.values().stream()
                    .filter(orders -> orders.size() >= 2)
                    .count();
            double lastYearMonthAssemblyRate = lastYearMonthTotalRoutes > 0 ? (double) lastYearMonthAssemblyRoutes / lastYearMonthTotalRoutes * 100 : 0.0;

            double growthRate = lastYearMonthAssemblyRate != 0 ? (monthAssemblyRate - lastYearMonthAssemblyRate) / lastYearMonthAssemblyRate * 100 : 0.0;
            monthlyGrowthRates.add(growthRate);

            // 计算环比增长率（与上月比较）
            double chainRate = previousMonthRate != 0 ? (monthAssemblyRate - previousMonthRate) / previousMonthRate * 100 : 0.0;
            monthlyChainRates.add(chainRate);

            previousMonthRate = monthAssemblyRate;
        }

        // 构建返回结果
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("year", currentYear);                     // 统计年份
        result.put("totalRoutes", totalRoutes);              // 全年总司机路线数
        result.put("assemblyRoutes", assemblyRoutes);        // 全年拼载路线数
        result.put("nonAssemblyRoutes", totalRoutes - assemblyRoutes); // 全年非拼载路线数
        result.put("annualAssemblyRate", annualAssemblyRate); // 全年拼成率

        // 月度数据
        result.put("monthlyTotalRoutes", monthlyTotalRoutes);      // 各月总司机路线数
        result.put("monthlyAssemblyRoutes", monthlyAssemblyRoutes); // 各月拼载路线数
        result.put("monthlyAssemblyRates", monthlyAssemblyRates);   // 各月拼成率
        result.put("monthlyGrowthRates", monthlyGrowthRates);      // 各月同比增长率
        result.put("monthlyChainRates", monthlyChainRates);        // 各月环比增长率

        return result;
    }

    /**
     * 平均顺路程度
     *
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> queryConvenience() {
        int currentYear = DateUtil.year(new Date());
        // 查询本年所有车主路线
        List<RouteStaffInfo> allStaffRoutes = routeStaffInfoService.list(
                new LambdaQueryWrapper<RouteStaffInfo>()
                        .apply("DATE_FORMAT(create_date, '%Y') = {0}", currentYear)
        );

        // 按月份分组路线
        Map<Integer, List<RouteStaffInfo>> routesByMonth = allStaffRoutes.stream()
                .collect(Collectors.groupingBy(route ->
                        DateUtil.month(DateUtil.parseDate(route.getCreateDate())) + 1
                ));
        // 计算全年平均匹配率
        double annualAverageMatchRate = calculateAverageMatchRate(allStaffRoutes);

        // 初始化月度数据
        List<Double> monthlyAverageMatchRates = new ArrayList<>();
        List<Double> monthlyGrowthRates = new ArrayList<>(); // 同比增长率
        List<Double> monthlyChainRates = new ArrayList<>();  // 环比增长率

        // 上一个月的匹配率，用于计算环比
        double previousMonthMatchRate = 0.0;

        // 计算各月数据
        for (int month = 1; month <= 12; month++) {
            List<RouteStaffInfo> monthRoutes = routesByMonth.getOrDefault(month, new ArrayList<>());
            double monthMatchRate = calculateAverageMatchRate(monthRoutes);
            monthlyAverageMatchRates.add(monthMatchRate);

            // 计算同比增长率（与去年同月比较）
            List<RouteStaffInfo> lastYearMonthRoutes = routeStaffInfoService.list(
                    new LambdaQueryWrapper<RouteStaffInfo>()
                            .apply("DATE_FORMAT(create_date, '%Y-%m') = {0}",
                                    currentYear - 1 + "-" + (month < 10 ? "0" + month : month))
            );
            double lastYearMonthMatchRate = calculateAverageMatchRate(lastYearMonthRoutes);
            double growthRate = lastYearMonthMatchRate != 0 ?
                    (monthMatchRate - lastYearMonthMatchRate) / lastYearMonthMatchRate * 100 : 0.0;
            monthlyGrowthRates.add(growthRate);

            // 计算环比增长率（与上月比较）
            double chainRate = previousMonthMatchRate != 0 ?
                    (monthMatchRate - previousMonthMatchRate) / previousMonthMatchRate * 100 : 0.0;
            monthlyChainRates.add(chainRate);

            previousMonthMatchRate = monthMatchRate;
        }

        // 构建返回结果
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("year", currentYear);                           // 统计年份
        result.put("annualAverageMatchRate", annualAverageMatchRate); // 全年平均匹配率

        // 月度数据
        result.put("monthlyAverageMatchRates", monthlyAverageMatchRates); // 各月平均匹配率
        result.put("monthlyGrowthRates", monthlyGrowthRates);           // 各月同比增长率
        result.put("monthlyChainRates", monthlyChainRates);             // 各月环比增长率

        return result;
    }

    /**
     * 计算一组路线的平均匹配率
     *
     * @param routes 路线列表
     * @return 平均匹配率
     */
    private double calculateAverageMatchRate(List<RouteStaffInfo> routes) {
        if (routes.isEmpty()) {
            return 0.0;
        }
        // 过滤掉 matchRate 为 null 的记录并计算平均值
        return routes.stream()
                .filter(route -> route.getMatchRate() != null)
                .mapToInt(RouteStaffInfo::getMatchRate)
                .average()
                .orElse(0.0);
    }

}
