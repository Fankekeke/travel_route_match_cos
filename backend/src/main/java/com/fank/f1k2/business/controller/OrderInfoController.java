package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
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
        }
        if ("1".equals(status)) {
            orderInfo.setReceiveDate(DateUtil.formatDateTime(new Date()));
        }
        if ("2".equals(status)) {
            orderInfo.setDeliveryDate(DateUtil.formatDateTime(new Date()));
        }
        return R.ok(orderInfoService.updateById(orderInfo));
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
