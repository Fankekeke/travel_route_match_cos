package com.fank.f1k2.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ExchangeInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fank.f1k2.common.exception.F1k2Exception;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface IExchangeInfoService extends IService<ExchangeInfo> {

    /**
     * 分页获取积分兑换
     *
     * @param page       分页对象
     * @param queryFrom 积分兑换
     * @return 结果
     */
    IPage<LinkedHashMap<String, Objects>> queryPage(Page<ExchangeInfo> page, ExchangeInfo queryFrom);

    /**
     * 新增积分兑换信息
     *
     * @param exchangeInfo 积分兑换信息
     * @return 结果
     */
    boolean addExchange(ExchangeInfo exchangeInfo) throws F1k2Exception;

    /**
     * 获取ID获取积分兑换详情
     *
     * @param id 主键
     * @return 结果
     */
    LinkedHashMap<String, Object> exchangeDetail(Integer id);
}
