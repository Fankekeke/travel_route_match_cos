package com.fank.f1k2.business.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ExchangeInfo;
import com.fank.f1k2.business.dao.ExchangeInfoMapper;
import com.fank.f1k2.business.entity.MaterialInfo;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IExchangeInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fank.f1k2.business.service.IMaterialInfoService;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.common.exception.F1k2Exception;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class ExchangeInfoServiceImpl extends ServiceImpl<ExchangeInfoMapper, ExchangeInfo> implements IExchangeInfoService {

    @Resource
    private IUserInfoService userInfoService;

    @Resource
    private IMaterialInfoService materialInfoService;

    /**
     * 分页获取积分兑换
     *
     * @param page      分页对象
     * @param queryFrom 积分兑换
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Objects>> queryPage(Page<ExchangeInfo> page, ExchangeInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }

    /**
     * 新增积分兑换信息
     *
     * @param exchangeInfo 积分兑换信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addExchange(ExchangeInfo exchangeInfo) throws F1k2Exception {
        // 获取所属用户
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, exchangeInfo.getUserId()));
        // 优惠券信息
        MaterialInfo materialInfo = materialInfoService.getById(exchangeInfo.getMaterialId());

        if (userInfo == null || materialInfo == null) {
            throw new F1k2Exception("系统异常");
        }
        if (materialInfo.getIntegral().compareTo(userInfo.getIntegral()) > 0) {
            throw new F1k2Exception("用户积分为【" + userInfo.getIntegral() + "】 无法兑换");
        }
        exchangeInfo.setUserId(userInfo.getId());
        exchangeInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        this.save(exchangeInfo);

        // 添加优惠券销量
        materialInfo.setSaleNum(materialInfo.getSaleNum() + 1);
        materialInfoService.updateById(materialInfo);

        // 更新用户积分
        userInfo.setIntegral(NumberUtil.sub(userInfo.getIntegral(), materialInfo.getIntegral()));
        return userInfoService.updateById(userInfo);
    }

    /**
     * 获取ID获取积分兑换详情
     *
     * @param id 主键
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> exchangeDetail(Integer id) {
        // 获取兑换信息
        ExchangeInfo exchangeInfo = this.getById(id);
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("exchange", null);
                put("user", null);
                put("material", null);
            }
        };
        result.put("exchange", exchangeInfo);

        // 用户信息
        UserInfo userInfo = userInfoService.getById(exchangeInfo.getUserId());
        result.put("user", userInfo);

        // 优惠券信息
        MaterialInfo materialInfo = materialInfoService.getById(exchangeInfo.getMaterialId());
        result.put("material", materialInfo);
        return result;
    }
}
