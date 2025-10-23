package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.WithdrawInfo;
import com.fank.f1k2.business.dao.WithdrawInfoMapper;
import com.fank.f1k2.business.service.IWithdrawInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class WithdrawInfoServiceImpl extends ServiceImpl<WithdrawInfoMapper, WithdrawInfo> implements IWithdrawInfoService {

    /**
     * 分页获取提现记录
     *
     * @param page      分页对象
     * @param queryFrom 提现记录
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<WithdrawInfo> page, WithdrawInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
