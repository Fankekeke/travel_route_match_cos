package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.PaymentRecord;
import com.fank.f1k2.business.dao.PaymentRecordMapper;
import com.fank.f1k2.business.service.IPaymentRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements IPaymentRecordService {

    /**
     * 分页获取付款记录
     *
     * @param page      分页对象
     * @param queryFrom 付款记录
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<PaymentRecord> page, PaymentRecord queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
