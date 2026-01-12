package com.fank.f1k2.business.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.OrderInfo;
import com.fank.f1k2.business.dao.OrderInfoMapper;
import com.fank.f1k2.business.entity.RouteInfo;
import com.fank.f1k2.business.entity.RouteStaffInfo;
import com.fank.f1k2.business.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    private final IUserInfoService userInfoService;

    private final IStaffInfoService staffInfoService;

    private final IRouteStaffInfoService routeStaffInfoService;

    private final IRouteInfoService routeInfoService;

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
     * 添加订单信息
     *
     * @param orderInfo 订单信息
     * @return 添加结果
     */
    @Override
    public Boolean orderAdd(OrderInfo orderInfo) {
        RouteStaffInfo routeStaffInfo = routeStaffInfoService.getById(orderInfo.getStaffRouteId());
        RouteInfo routeInfo = routeInfoService.getById(orderInfo.getUserRouteId());
        orderInfo.setUserId(routeInfo.getUserId());
        orderInfo.setStaffId(routeStaffInfo.getStaffId());
        orderInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        orderInfo.setCode("ORD-" + System.currentTimeMillis());
        orderInfo.setStatus("-1");
        orderInfo.setOrderName(DateUtil.formatChineseDate(new Date(), true, false) + " " +routeInfo.getStartAddress() + " - " + routeInfo.getEndAddress());
        return this.save(orderInfo);
    }
}
