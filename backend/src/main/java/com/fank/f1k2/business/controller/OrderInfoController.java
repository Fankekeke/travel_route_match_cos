package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.business.entity.StaffIncome;
import com.fank.f1k2.business.service.IStaffIncomeService;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.OrderInfo;
import com.fank.f1k2.business.service.IOrderInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public R save(OrderInfo addFrom) {
        return R.ok(orderInfoService.orderAdd(addFrom));
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
     * @param id     主键ID
     * @param status 订单状态
     * @return 订单信息
     */
    @PutMapping("/updateOrderStatus")
    public R updateOrderStatus(Integer id, String status) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(id);
        orderInfo.setStatus(status);
        if ("3".equals(status)) {
            orderInfo.setPayDate(DateUtil.formatDateTime(new Date()));
            OrderInfo orderInfo1 = orderInfoService.getById(id);
            // 更新车主收益
            StaffIncome staffIncome = new StaffIncome();
            staffIncome.setStaffId(orderInfo1.getStaffId());
            staffIncome.setOrderId(id);
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
        orderInfoService.checkOrderStatus(id);
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

}
