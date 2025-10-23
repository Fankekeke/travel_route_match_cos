package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.OrderInfo;
import com.fank.f1k2.business.dao.OrderInfoMapper;
import com.fank.f1k2.business.service.IOrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

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
}
