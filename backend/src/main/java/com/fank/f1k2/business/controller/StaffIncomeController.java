package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.StaffIncome;
import com.fank.f1k2.business.service.IStaffIncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 车主收益 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/staff-income")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StaffIncomeController {

    private final IStaffIncomeService staffIncomeService;

    /**
     * 分页获取车主收益
     *
     * @param page      分页对象
     * @param queryFrom 车主收益
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<StaffIncome> page, StaffIncome queryFrom) {
        return R.ok(staffIncomeService.queryPage(page, queryFrom));
    }

    /**
     * 查询车主收益详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(staffIncomeService.getById(id));
    }

    /**
     * 查询车主收益列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(staffIncomeService.list());
    }

    /**
     * 新增车主收益
     *
     * @param addFrom 车主收益对象
     * @return 结果
     */
    @PostMapping
    public R save(StaffIncome addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(staffIncomeService.save(addFrom));
    }

    /**
     * 修改车主收益
     *
     * @param editFrom 车主收益对象
     * @return 结果
     */
    @PutMapping
    public R edit(StaffIncome editFrom) {
        return R.ok(staffIncomeService.updateById(editFrom));
    }

    /**
     * 删除车主收益
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(staffIncomeService.removeByIds(ids));
    }

}
