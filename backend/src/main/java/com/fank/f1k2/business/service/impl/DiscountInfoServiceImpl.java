package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.DiscountInfo;
import com.fank.f1k2.business.dao.DiscountInfoMapper;
import com.fank.f1k2.business.service.IDiscountInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class DiscountInfoServiceImpl extends ServiceImpl<DiscountInfoMapper, DiscountInfo> implements IDiscountInfoService {

    /**
     * 分页获取优惠券管理
     *
     * @param page       分页对象
     * @param queryFrom 优惠券管理
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<DiscountInfo> page, DiscountInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
