package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ExchangeInfo;
import com.fank.f1k2.business.dao.ExchangeInfoMapper;
import com.fank.f1k2.business.service.IExchangeInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class ExchangeInfoServiceImpl extends ServiceImpl<ExchangeInfoMapper, ExchangeInfo> implements IExchangeInfoService {

    /**
     * 分页获取积分兑换
     *
     * @param page       分页对象
     * @param queryFrom 积分兑换
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Objects>> queryPage(Page<ExchangeInfo> page, ExchangeInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
