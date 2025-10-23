package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.StaffInfo;
import com.fank.f1k2.business.service.IStaffInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 车主管理 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/staff-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StaffInfoController {

    private final IStaffInfoService staffInfoService;

    /**
     * 分页获取车主管理
     *
     * @param page      分页对象
     * @param queryFrom 车主管理
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<StaffInfo> page, StaffInfo queryFrom) {
        return R.ok(staffInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询车主管理详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(staffInfoService.getById(id));
    }

    /**
     * 查询车主管理列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(staffInfoService.list());
    }

    /**
     * 新增车主管理
     *
     * @param addFrom 车主管理对象
     * @return 结果
     */
    @PostMapping
    public R save(StaffInfo addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(staffInfoService.save(addFrom));
    }

    /**
     * 修改车主管理
     *
     * @param editFrom 车主管理对象
     * @return 结果
     */
    @PutMapping
    public R edit(StaffInfo editFrom) {
        return R.ok(staffInfoService.updateById(editFrom));
    }

    /**
     * 删除车主管理
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(staffInfoService.removeByIds(ids));
    }

}
