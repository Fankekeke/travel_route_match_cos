package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.StaffInfo;
import com.fank.f1k2.business.service.INotifyInfoService;
import com.fank.f1k2.business.service.IStaffInfoService;
import com.fank.f1k2.common.exception.F1k2Exception;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.AuditInfo;
import com.fank.f1k2.business.service.IAuditInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 审核管理 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/audit-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuditInfoController {

    private final IAuditInfoService auditInfoService;

    private final INotifyInfoService notifyInfoService;

    private final IStaffInfoService staffInfoService;

    /**
     * 分页获取审核管理
     *
     * @param page      分页对象
     * @param queryFrom 审核管理
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AuditInfo> page, AuditInfo queryFrom) {
        return R.ok(auditInfoService.queryAuditPage(page, queryFrom));
    }

    /**
     * 审核车主
     *
     * @param auditInfo 审核管理对象
     * @return 结果
     */
    @PutMapping("/auditStaff")
    public R auditStaff(AuditInfo auditInfo) {
        String content = "";
        if (auditInfo.getAuditStatus() == 1) {
            content = "您好，您的申请已审核通过";
            staffInfoService.update(Wrappers.<StaffInfo>lambdaUpdate().set(StaffInfo::getStatus, "1").eq(StaffInfo::getId, auditInfo.getUserId()));
        } else if (auditInfo.getAuditStatus() == 2) {
            content = "您好，您的申请未通过审核，请检查相关信息";
        }
        notifyInfoService.sendNotify(content, auditInfo.getUserId());
        return R.ok(auditInfoService.updateById(auditInfo));
    }

    /**
     * 查询审核管理详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(auditInfoService.getById(id));
    }

    /**
     * 查询审核管理列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(auditInfoService.list());
    }

    /**
     * 新增审核管理
     *
     * @param addFrom 审核管理对象
     * @return 结果
     */
    @PostMapping
    public R save(AuditInfo addFrom) throws F1k2Exception {
        // 获取车主信息
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, addFrom.getUserId()));
        if (staffInfo == null) {
            throw new F1k2Exception("未找到该车主信息");
        }
        addFrom.setUserId(staffInfo.getId());
        addFrom.setAuditStatus(0);
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(auditInfoService.save(addFrom));
    }

    /**
     * 修改审核管理
     *
     * @param editFrom 审核管理对象
     * @return 结果
     */
    @PutMapping
    public R edit(AuditInfo editFrom) {
        return R.ok(auditInfoService.updateById(editFrom));
    }

    /**
     * 删除审核管理
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(auditInfoService.removeByIds(ids));
    }

}
