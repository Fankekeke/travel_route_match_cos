package com.fank.f1k2.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface IOrderInfoService extends IService<OrderInfo> {

    /**
     * 分页获取订单信息
     *
     * @param page      分页对象
     * @param queryFrom 订单信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<OrderInfo> page, OrderInfo queryFrom);

    /**
     * 用户添加订单信息
     *
     * @param orderInfo 订单信息
     * @return 添加结果
     */
    Boolean orderAdd(OrderInfo orderInfo);

    /**
     * 车主审核订单
     *
     * @param orderId 订单ID
     * @param status  订单状态
     * @return 订单信息
     */
    Boolean auditOrderByStaff(Integer orderId, String status);

    /**
     * 用户取消订单
     *
     * @param orderId 订单ID
     * @return 订单信息
     */
    Boolean cancelOrder(Integer orderId);

    /**
     * 检查订单状态
     *
     * @param orderId 订单ID
     */
    void checkOrderStatus(Integer orderId);

    /**
     * 查询用户行程订单信息详情
     *
     * @param routeId 行程ID
     * @return 订单信息
     */
    LinkedHashMap<String, Object> queryRouteUserDetail(Integer routeId);

    /**
     * 获取车主行程订单信息详情
     *
     * @param routeId 行程ID
     * @return 订单信息
     */
    LinkedHashMap<String, Object> queryRouteStaffDetail(Integer routeId);
}
