package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.MaterialInfo;
import com.fank.f1k2.business.service.IMaterialInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 物品积分 控制层
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/material-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MaterialInfoController {

    private final IMaterialInfoService bulletinInfoService;

    /**
    * 分页获取物品积分
    *
    * @param page       分页对象
    * @param queryFrom 物品积分
    * @return 结果
    */
    @GetMapping("/page")
    public R page(Page<MaterialInfo> page, MaterialInfo queryFrom) {
        return R.ok();
    }

    /**
    * 查询物品积分详情
    *
    * @param id 主键ID
    * @return 结果
    */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
    * 查询物品积分列表
    *
    * @return 结果
    */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
    * 新增物品积分
    *
    * @param addFrom 物品积分对象
    * @return 结果
    */
    @PostMapping
    public R save(MaterialInfo addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
    * 修改物品积分
    *
    * @param editFrom 物品积分对象
    * @return 结果
    */
    @PutMapping
    public R edit(MaterialInfo editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
    * 删除物品积分
    *
    * @param ids 删除的主键ID
    * @return 结果
    */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
