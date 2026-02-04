package com.fank.f1k2.business.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.*;
import com.fank.f1k2.business.service.*;
import com.fank.f1k2.common.exception.F1k2Exception;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.common.utils.RoutePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 订单信息 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/order-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderInfoController {

    private final IOrderInfoService orderInfoService;

    private final IStaffIncomeService staffIncomeService;

    private final IRouteInfoService routeInfoService;

    private final IRouteStaffInfoService routeStaffInfoService;

    private final IUserInfoService userInfoService;

    private final IStaffInfoService staffInfoService;

    private final RoutePlanService routePlanService;

    private final IDiscountInfoService discountInfoService;


    /**
     * 分页获取订单信息
     *
     * @param page      分页对象
     * @param queryFrom 订单信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<OrderInfo> page, OrderInfo queryFrom) {
        return R.ok(orderInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询订单信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(orderInfoService.getById(id));
    }

    /**
     * 查询订单信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(orderInfoService.list());
    }

    /**
     * 新增订单信息
     *
     * @param addFrom 订单信息对象
     * @return 结果
     */
    @PostMapping
    public R save(OrderInfo addFrom) throws F1k2Exception {
        return R.ok(orderInfoService.orderAdd(addFrom));
    }

    /**
     * 查询当前用户进行中路线
     *
     * @param userId 用户ID
     * @return 列表
     */
    @GetMapping("/queryCurrentRouteByUser")
    public R queryCurrentRouteByUser(Integer userId) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
        List<RouteInfo> routeInfoList = routeInfoService.list(Wrappers.<RouteInfo>lambdaQuery().eq(RouteInfo::getUserId, userInfo.getId()).ne(RouteInfo::getStatus, "3"));
        if (CollectionUtil.isEmpty(routeInfoList)) {
            return R.ok(new LinkedHashMap<String, Object>() {
                {
                    put("userInfo", userInfo);
                }
            });
        }
        return R.ok(orderInfoService.queryRouteUserDetail(routeInfoList.get(0).getId()));
    }

    /**
     * 查询当前用户进行中路线
     *
     * @param staffId 车主ID
     * @return 列表
     */
    @GetMapping("/queryCurrentRouteByStaff")
    public R queryCurrentRouteByStaff(Integer staffId) {
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, staffId));
        List<RouteStaffInfo> routeInfoList = routeStaffInfoService.list(Wrappers.<RouteStaffInfo>lambdaQuery().eq(RouteStaffInfo::getStaffId, staffInfo.getId()).ne(RouteStaffInfo::getStatus, "1"));
        if (CollectionUtil.isEmpty(routeInfoList)) {
            return R.ok(new LinkedHashMap<String, Object>() {
                {
                    put("staffInfo", staffInfo);
                }
            });
        }
        return R.ok(orderInfoService.queryRouteStaffDetail(routeInfoList.get(0).getId()));
    }

    /**
     * 查询用户行程订单信息详情
     *
     * @param routeId 行程ID
     * @return 订单信息
     */
    @GetMapping("/queryRouteUserDetail")
    public R queryRouteUserDetail(Integer routeId) {
        return R.ok(orderInfoService.queryRouteUserDetail(routeId));
    }

    /**
     * 查询车主行程订单信息详情
     *
     * @param routeId 行程ID
     * @return 订单信息
     */
    @GetMapping("/queryRouteStaffDetail")
    public R queryRouteStaffDetail(Integer routeId) {
        return R.ok(orderInfoService.queryRouteStaffDetail(routeId));
    }

    /**
     * 车主审核订单
     *
     * @param orderId 订单ID
     * @param status  订单状态
     * @return 订单信息
     */
    @GetMapping("/auditOrderByStaff")
    public R auditOrderByStaff(Integer orderId, String status) {
        orderInfoService.auditOrderByStaff(orderId, status);
        orderInfoService.checkOrderStatus(orderId);
        return R.ok(true);
    }

    /**
     * 用户取消订单
     *
     * @param orderId 订单ID
     * @return 订单信息
     */
    @GetMapping("/cancelOrder")
    public R cancelOrder(Integer orderId) {
        orderInfoService.cancelOrder(orderId);
        orderInfoService.checkOrderStatus(orderId);
        return R.ok(true);
    }

    /**
     * 查询首页数据
     *
     * @return 订单信息
     */
    @GetMapping("/queryHomeData")
    public R queryHomeData() {
        return R.ok(orderInfoService.homeData(null));
    }

    /**
     * 年统计
     *
     * @param date 年份
     * @return 结果
     */
    @GetMapping("/statistics/year")
    public R selectStoreStatisticsByYear(String date) {
        return R.ok(orderInfoService.selectStoreStatisticsByYear(date));
    }

    /**
     * 月统计
     *
     * @param date 日期
     * @return 结果
     */
    @GetMapping("/statistics/month")
    public R selectStoreStatisticsByMonth(String date) {
        return R.ok(orderInfoService.selectStoreStatisticsByMonth(date));
    }

    /**
     * 修改订单状态
     *
     * @param orderId     主键ID
     * @param status 订单状态
     * @return 订单信息
     */
    @GetMapping("/updateOrderStatus")
    public R updateOrderStatus(Integer orderId, String status) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setStatus(status);
        if ("3".equals(status)) {
            orderInfo.setPayDate(DateUtil.formatDateTime(new Date()));
            OrderInfo orderInfo1 = orderInfoService.getById(orderId);
            // 更新车主收益
            StaffIncome staffIncome = new StaffIncome();
            staffIncome.setStaffId(orderInfo1.getStaffId());
            staffIncome.setOrderId(orderId);
            staffIncome.setTotalPrice(orderInfo1.getAfterOrderPrice());
            staffIncome.setCreateDate(DateUtil.formatDateTime(new Date()));
            staffIncomeService.save(staffIncome);
        }
        if ("1".equals(status)) {
            orderInfo.setReceiveDate(DateUtil.formatDateTime(new Date()));
        }
        if ("2".equals(status)) {
            orderInfo.setDeliveryDate(DateUtil.formatDateTime(new Date()));
        }
        orderInfoService.updateById(orderInfo);
        orderInfoService.checkOrderStatus(orderId);
        return R.ok(true);
    }

    /**
     * 订单支付
     *
     * @param orderCode 订单编号
     * @return 订单信息
     */
    @GetMapping("/orderPay")
    public R orderPay(String orderCode) {
        OrderInfo orderInfo = orderInfoService.getOne(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getCode, orderCode));
        orderInfo.setPayDate(DateUtil.formatDateTime(new Date()));
        orderInfo.setStatus("3");
        if (orderInfo.getDiscountId() != null) {
            discountInfoService.update(Wrappers.<DiscountInfo>lambdaUpdate().set(DiscountInfo::getStatus, "1").eq(DiscountInfo::getId, orderInfo.getDiscountId()));
        }
        UserInfo userInfo = userInfoService.getById(orderInfo.getUserId());
        // 添加用户积分
        userInfo.setIntegral(NumberUtil.add((userInfo.getIntegral() != null ? userInfo.getIntegral() : BigDecimal.ZERO), (orderInfo.getIntegral() != null ? orderInfo.getIntegral() : BigDecimal.ZERO)));
        userInfoService.updateById(userInfo);

        // 更新车主收益
        StaffIncome staffIncome = new StaffIncome();
        staffIncome.setStaffId(orderInfo.getStaffId());
        staffIncome.setOrderId(orderInfo.getId());
        staffIncome.setTotalPrice(orderInfo.getAfterOrderPrice());
        staffIncome.setCreateDate(DateUtil.formatDateTime(new Date()));
        staffIncomeService.save(staffIncome);

        orderInfoService.updateById(orderInfo);
        orderInfoService.checkOrderStatus(orderInfo.getId());
        return R.ok(true);
    }

    /**
     * 修改订单信息
     *
     * @param editFrom 订单信息对象
     * @return 结果
     */
    @PutMapping
    public R edit(OrderInfo editFrom) {
        return R.ok(orderInfoService.updateById(editFrom));
    }

    /**
     * 删除订单信息
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(orderInfoService.removeByIds(ids));
    }

    /**
     * 查询线路规划设置
     *
     * @param startLongitude 起始经度
     * @param startLatitude  起始纬度
     * @param endLongitude   终点经度
     * @param endLatitude    终点纬度
     * @return 线路规划结果
     */
    @GetMapping("/routeSet")
    public R queryRouteSet(Double startLongitude, Double startLatitude,
                           Double endLongitude, Double endLatitude) {
        try {
            // 参数校验
            if (startLongitude == null || startLatitude == null ||
                    endLongitude == null || endLatitude == null) {
                return R.error("起始位置和终点位置不能为空");
            }

            String result = routePlanService.getRoutePlan(startLongitude, startLatitude,
                    endLongitude, endLatitude);
            return R.ok(JSONUtil.parse(result));
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取路线规划失败: " + e.getMessage());
        }
    }

}
