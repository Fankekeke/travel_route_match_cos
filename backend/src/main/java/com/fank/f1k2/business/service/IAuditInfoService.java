package com.fank.f1k2.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.AuditInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface IAuditInfoService extends IService<AuditInfo> {

    /**
     * 分页获取审核管理
     *
     * @param page      分页对象
     * @param queryFrom 审核管理
     * @return 结果
     */
    IPage<AuditInfo> queryAuditPage(Page<AuditInfo> page, AuditInfo queryFrom);
}
