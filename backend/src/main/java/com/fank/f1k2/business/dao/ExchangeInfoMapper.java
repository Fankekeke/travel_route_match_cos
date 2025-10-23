package com.fank.f1k2.business.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ExchangeInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface ExchangeInfoMapper extends BaseMapper<ExchangeInfo> {

    /**
     * 分页获取积分兑换
     *
     * @param page       分页对象
     * @param queryFrom 积分兑换
     * @return 结果
     */
    IPage<LinkedHashMap<String, Objects>> queryPage(Page<ExchangeInfo> page, @Param("queryFrom") ExchangeInfo queryFrom);
}
