package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.AuditInfo;
import com.fank.f1k2.business.dao.AuditInfoMapper;
import com.fank.f1k2.business.service.IAuditInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class AuditInfoServiceImpl extends ServiceImpl<AuditInfoMapper, AuditInfo> implements IAuditInfoService {

    /**
     * 分页获取审核管理
     *
     * @param page      分页对象
     * @param queryFrom 审核管理
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryAuditPage(Page<AuditInfo> page, AuditInfo queryFrom) {
        return baseMapper.queryAuditPage(page, queryFrom);
    }
}
