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
        return null;
//        // 返回数据
//        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
//            {
//                put("orderNum", 0);
//                put("orderPrice", 0);
//                put("staffNum", 0);
//                put("memberNum", 0);
//            }
//        };
//
//        if (userId != null) {
//            // 获取企业信息
//            EnterpriseInfo enterpriseInfo = enterpriseInfoService.getOne(Wrappers.<EnterpriseInfo>lambdaQuery().eq(EnterpriseInfo::getUserId, userId));
//            if (enterpriseInfo != null) {
//                userId = enterpriseInfo.getId();
//            }
//        }
//
//        // 本月预约数量
//        List<ReserveInfo> orderMonthList = baseMapper.selectOrderByMonth(userId);
//        result.put("monthOrderNum", CollectionUtil.isEmpty(orderMonthList) ? 0 : orderMonthList.size());
//        // 获取本月面试投递
//        List<InterviewInfo> interviewMonthList = baseMapper.selectInterviewByMonth(userId);
//        result.put("monthOrderTotal", CollectionUtil.isEmpty(interviewMonthList) ? 0 : interviewMonthList.size());
//
//        // 本年预约数量
//        List<ReserveInfo> orderYearList = baseMapper.selectOrderByYear(userId);
//        result.put("yearOrderNum", CollectionUtil.isEmpty(orderYearList) ? 0 : orderYearList.size());
//        // 本年面试投递
//        List<InterviewInfo> interviewYearList = baseMapper.selectInterviewByYear(userId);
//        result.put("yearOrderTotal", CollectionUtil.isEmpty(interviewYearList) ? 0 : interviewYearList.size());
//
//        // 近十天预约统计
//        result.put("orderNumDayList", baseMapper.selectOrderNumWithinDays(userId));
//        // 近十天面试统计
//        result.put("priceDayList", baseMapper.selectOrderPriceWithinDays(userId));
//
//        // 公告信息
//        result.put("bulletinInfoList", bulletinInfoService.list(Wrappers.<BulletinInfo>lambdaQuery().eq(BulletinInfo::getRackUp, 1)));
//
//        return result;
    }

    /**
     * 年统计订单及收益
     *
     * @param date 年份
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectStoreStatisticsByYear(String date) {
        return null;
//        // 返回数据
//        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
//
//        int year =  DateUtil.year(new Date());
//        if (StrUtil.isNotEmpty(date)) {
//            year = Integer.parseInt(date);
//        }
//
//        List<InterviewInfo> scenicOrderList = this.list(new LambdaQueryWrapper<InterviewInfo>().apply("DATE_FORMAT(create_date, '%Y') = {0}", year));
//        for (InterviewInfo scenicOrder : scenicOrderList) {
//            scenicOrder.setMonth(DateUtil.month(DateUtil.parseDate(scenicOrder.getCreateDate())) + 1);
//        }
//        Map<Integer, List<InterviewInfo>> orderOutMonthMap = scenicOrderList.stream().collect(Collectors.groupingBy(InterviewInfo::getMonth));
//        List<AiInterview> hotelOrderList = aiInterviewService.list(new LambdaQueryWrapper<AiInterview>().apply("DATE_FORMAT(completion_time, '%Y') = {0}", year));
//        for (AiInterview orderInfo : hotelOrderList) {
//            // 提取AI分数
//            if (StrUtil.isNotEmpty(orderInfo.getAiRemark())) {
//                // 使用预编译的正则表达式常量
//                Matcher matcher = MATCH_SCORE_PATTERN.matcher(orderInfo.getAiRemark());
//                if (matcher.find()) {
//                    try {
//                        int score = Integer.parseInt(matcher.group(1));
//                        orderInfo.setScore(score);
//                    } catch (NumberFormatException e) {
//                        // 处理解析分数时可能出现的异常
//                        orderInfo.setScore(0);
//                    }
//                }
//            } else {
//                orderInfo.setScore(0);
//            }
//            orderInfo.setMonth(DateUtil.month(DateUtil.parseDate(orderInfo.getCompletionTime())) + 1);
//        }
//        Map<Integer, List<AiInterview>> orderPutMonthMap = hotelOrderList.stream().collect(Collectors.groupingBy(AiInterview::getMonth));
//
//        result.put("orderNum", scenicOrderList.size());
//        BigDecimal totalPrice = BigDecimal.valueOf(scenicOrderList.stream().filter(e -> (4 == e.getStatus() || 5 == e.getStatus())).count());
//        result.put("totalPrice", totalPrice);
//        result.put("putNum", hotelOrderList.size());
//        // 计算平均分
//        double averageScore = hotelOrderList.stream()
//                .mapToInt(AiInterview::getScore)
//                .average()
//                .orElse(0.0);
//        result.put("outlayPrice", BigDecimal.valueOf(averageScore));
//
//        List<Integer> orderNumList = new ArrayList<>();
//        List<BigDecimal> orderPriceList = new ArrayList<>();
//        List<Integer> outlayNumList = new ArrayList<>();
//        List<BigDecimal> outlayPriceList = new ArrayList<>();
//        for (int i = 1; i <= 12; i++) {
//
//            List<InterviewInfo> currentMonthOutList = orderOutMonthMap.get(i);
//            if (CollectionUtil.isEmpty(currentMonthOutList)) {
//                orderNumList.add(0);
//                orderPriceList.add(BigDecimal.ZERO);
//            } else {
//                orderNumList.add(currentMonthOutList.size());
//                BigDecimal currentMonthOutPrice = BigDecimal.valueOf(currentMonthOutList.stream().filter(e -> (4 == e.getStatus() || 5 == e.getStatus())).count());
//                orderPriceList.add(currentMonthOutPrice);
//            }
//
//            List<AiInterview> currentMonthPutList = orderPutMonthMap.get(i);
//            if (CollectionUtil.isEmpty(currentMonthPutList)) {
//                outlayNumList.add(0);
//                outlayPriceList.add(BigDecimal.ZERO);
//            } else {
//                outlayNumList.add(currentMonthPutList.size());
//                double currentMonthPutPrice = currentMonthPutList.stream()
//                        .mapToInt(AiInterview::getScore)
//                        .average()
//                        .orElse(0.0);
//                outlayPriceList.add(BigDecimal.valueOf(currentMonthPutPrice));
//            }
//
//        }
//        result.put("orderPriceList", orderPriceList);
//        result.put("orderNumList", orderNumList);
//        result.put("outlayPriceList", outlayPriceList);
//        result.put("outlayNumList", orderNumList);
////        result.put("outlayNumList", outlayNumList);
//
//        // 岗位销量排行
//        List<LinkedHashMap<String, Object>> saleRank = new ArrayList<>();
//        List<LinkedHashMap<String, Object>> salePriceRank = new ArrayList<>();
//        LinkedHashMap<String, Integer> saleTypeRankMap = new LinkedHashMap<>();
//
//        Map<Integer, List<InterviewInfo>> recordInfoMap = scenicOrderList.stream().collect(Collectors.groupingBy(InterviewInfo::getBaseId));
//
//        // 岗位信息
//        Set<Integer> scenicCodeList = recordInfoMap.keySet();
//        List<PostInfo> scenicInfoList = postInfoService.list(Wrappers.<PostInfo>lambdaQuery().in(CollectionUtil.isNotEmpty(scenicCodeList), PostInfo::getId, scenicCodeList));
//        Map<Integer, PostInfo> scenicMap = scenicInfoList.stream().collect(Collectors.toMap(PostInfo::getId, e -> e));
//        List<String> scenicTypeList = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8");
//
//        recordInfoMap.forEach((key, value) -> {
//            PostInfo scenic= scenicMap.get(key);
//            if (scenic != null) {
//                saleRank.add(new LinkedHashMap<String, Object>() {
//                    {
//                        put("name", scenic.getPostName());
//                        put("num", value.size());
//                    }
//                });
//                salePriceRank.add(new LinkedHashMap<String, Object>() {
//                    {
//                        put("name", scenic.getPostName());
//                        put("num", value.stream().filter(e -> (4 == e.getStatus() || 5 == e.getStatus())).count());
//                    }
//                });
//
//                saleTypeRankMap.merge(StrUtil.toString(scenic.getAcademic()), value.size(), Integer::sum);
//            }
//        });
//        result.put("saleRank", saleRank);
//        result.put("salePriceRank", salePriceRank);
//        // 销售岗位分类
//        LinkedHashMap<String, Integer> saleTypeRankMapCopy = new LinkedHashMap<>();
//        for (String level : scenicTypeList) {
//            saleTypeRankMapCopy.put(level, saleTypeRankMap.get(level) == null ? 0 : saleTypeRankMap.get(level));
//        }
//        result.put("saleTypeRankMapCopy", saleTypeRankMapCopy);
//        return result;
    }

    /**
     * 月统计订单及收益
     *
     * @param dateStr 日期
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectStoreStatisticsByMonth(String dateStr) {
        return null;
//        String date = dateStr + "-01";
//        // 返回数据
//        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
//
//        int year =  DateUtil.year(new Date());
//        int month =  DateUtil.month(new Date()) + 1;
//        if (StrUtil.isNotEmpty(date)) {
//            year = DateUtil.year(DateUtil.parseDate(date));
//            month = DateUtil.month(DateUtil.parseDate(date)) + 1;
//        }
//
//        List<RouteInfo> scenicOrderList = routeInfoService.list(new LambdaQueryWrapper<RouteInfo>().apply("DATE_FORMAT(create_date, '%Y%m') = {0}", (year + "" + ((month < 10) ? "0" + month : month))));
//        for (RouteInfo scenicOrder : scenicOrderList) {
//            scenicOrder.setMonth(DateUtil.month(DateUtil.parseDate(scenicOrder.getCreateDate())) + 1);
//            scenicOrder.setDay(DateUtil.dayOfMonth(DateUtil.parseDate(scenicOrder.getCreateDate())));
//        }
//        Map<Integer, List<RouteInfo>> orderOutDayMap = scenicOrderList.stream().collect(Collectors.groupingBy(RouteInfo::getDay));
//        List<RouteStaffInfo> hotelOrderList = routeStaffInfoService.list(new LambdaQueryWrapper<RouteStaffInfo>().apply("DATE_FORMAT(completion_time, '%Y%m') = {0}", (year + "" + ((month < 10) ? "0" + month : month))));
//        for (RouteStaffInfo orderInfo : hotelOrderList) {
//            orderInfo.setMonth(DateUtil.month(DateUtil.parseDate(orderInfo.getCreateDate())) + 1);
//            orderInfo.setDay(DateUtil.dayOfMonth(DateUtil.parseDate(orderInfo.getCreateDate())));
//        }
//        Map<Integer, List<RouteStaffInfo>> orderPutDayMap = hotelOrderList.stream().collect(Collectors.groupingBy(RouteStaffInfo::getDay));
//
//        // 本月订单量
//        result.put("orderNum", scenicOrderList.size());
//        // 本月总收益
//        BigDecimal totalPrice = BigDecimal.valueOf(scenicOrderList.stream().filter(e -> 4 == e.getStatus() || 5 == e.getStatus()).count());
//        result.put("totalPrice", totalPrice);
//        result.put("putNum", hotelOrderList.size());
//        // 计算平均分
//        double averageScore = hotelOrderList.stream()
//                .mapToInt(AiInterview::getScore)
//                .average()
//                .orElse(0.0);
//        result.put("outlayPrice", averageScore);
//
//        List<Integer> orderNumList = new ArrayList<>();
//        List<BigDecimal> orderPriceList = new ArrayList<>();
//        List<Integer> outlayNumList = new ArrayList<>();
//        List<BigDecimal> outlayPriceList = new ArrayList<>();
//        int days = DateUtil.getLastDayOfMonth(DateUtil.parseDate(date));
//
//        // 本月日期
//        List<String> dateTimeList = new ArrayList<>();
//
//        for (int i = 1; i <= days; i++) {
//            dateTimeList.add(month + "月" + i + "日");
//            List<InterviewInfo> currentDayOutList = orderOutDayMap.get(i);
//            if (CollectionUtil.isEmpty(currentDayOutList)) {
//                orderNumList.add(0);
//                orderPriceList.add(BigDecimal.ZERO);
//            } else {
//                orderNumList.add(currentDayOutList.size());
//                BigDecimal currentDayOutPrice = BigDecimal.valueOf(currentDayOutList.stream().filter(e -> 4 == e.getStatus() || 5 == e.getStatus()).count());
//                orderPriceList.add(currentDayOutPrice);
//            }
//
//            // 本天入库
//            List<AiInterview> currentDayPutList = orderPutDayMap.get(i);
//            if (CollectionUtil.isEmpty(currentDayPutList)) {
//                outlayNumList.add(0);
//                outlayPriceList.add(BigDecimal.ZERO);
//            } else {
//                outlayNumList.add(currentDayPutList.size());
//                BigDecimal currentDayPutPrice = BigDecimal.valueOf(currentDayPutList.stream().filter(e -> e.getScore() > 80).count());
//                outlayPriceList.add(currentDayPutPrice);
//            }
//
//        }
//        result.put("orderPriceList", orderPriceList);
//        result.put("orderNumList", orderNumList);
//        result.put("outlayPriceList", outlayPriceList);
//        result.put("outlayNumList", orderNumList);
////        result.put("outlayNumList", outlayNumList);
//
//        result.put("dateList", dateTimeList);
//        // 岗位销量排行
//        List<LinkedHashMap<String, Object>> saleRank = new ArrayList<>();
//        List<LinkedHashMap<String, Object>> salePriceRank = new ArrayList<>();
//        LinkedHashMap<String, Integer> saleTypeRankMap = new LinkedHashMap<>();
//
//        Map<Integer, List<InterviewInfo>> recordInfoMap = scenicOrderList.stream().collect(Collectors.groupingBy(InterviewInfo::getBaseId));
//
//        // 岗位信息
//        Set<Integer> scenicIdList = recordInfoMap.keySet();
//        List<PostInfo> scenicInfoList = postInfoService.list(Wrappers.<PostInfo>lambdaQuery().in(CollectionUtil.isNotEmpty(scenicIdList), PostInfo::getId, scenicIdList));
//        Map<Integer, PostInfo> scenicMap = scenicInfoList.stream().collect(Collectors.toMap(PostInfo::getId, e -> e));
//
//        recordInfoMap.forEach((key, value) -> {
//            PostInfo scenicInfo = scenicMap.get(key);
//            if (scenicInfo != null) {
//                saleRank.add(new LinkedHashMap<String, Object>() {
//                    {
//                        put("name", scenicInfo.getPostName());
//                        put("num", value.size());
//                    }
//                });
//                salePriceRank.add(new LinkedHashMap<String, Object>() {
//                    {
//                        put("name", scenicInfo.getPostName());
//                        put("num", value.stream().filter(e -> 4 == e.getStatus() || 5 == e.getStatus()).count());
//                    }
//                });
//
//                saleTypeRankMap.merge(StrUtil.toString(scenicInfo.getAcademic()), value.size(), Integer::sum);
//            }
//        });
//        result.put("saleRank", saleRank);
//        result.put("salePriceRank", salePriceRank);
//        // 销售岗位分类
//        LinkedHashMap<String, Integer> saleTypeRankMapCopy = new LinkedHashMap<>();
//        List<String> scenicTypeList = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8");
//        for (String level : scenicTypeList) {
//            saleTypeRankMapCopy.put(level, saleTypeRankMap.get(level) == null ? 0 : saleTypeRankMap.get(level));
//        }
//        result.put("saleTypeRankMapCopy", saleTypeRankMapCopy);
//        return result;
    }
}
