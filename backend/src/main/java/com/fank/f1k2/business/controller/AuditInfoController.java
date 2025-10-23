package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
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

    private final IAuditInfoService bulletinInfoService;

    /**
     * 分页获取审核管理
     *
     * @param page      分页对象
     * @param queryFrom 审核管理
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AuditInfo> page, AuditInfo queryFrom) {
        return R.ok();
    }

    /**
     * 查询审核管理详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询审核管理列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增审核管理
     *
     * @param addFrom 审核管理对象
     * @return 结果
     */
    @PostMapping
    public R save(AuditInfo addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改审核管理
     *
     * @param editFrom 审核管理对象
     * @return 结果
     */
    @PutMapping
    public R edit(AuditInfo editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除审核管理
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
