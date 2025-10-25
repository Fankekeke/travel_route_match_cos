package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.VehicleInfo;
import com.fank.f1k2.business.service.IVehicleInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 车辆管理 控制层
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/vehicle-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleInfoController {

    private final IVehicleInfoService bulletinInfoService;

    /**
    * 分页获取车辆管理
    *
    * @param page       分页对象
    * @param queryFrom 车辆管理
    * @return 结果
    */
    @GetMapping("/page")
    public R page(Page<VehicleInfo> page, VehicleInfo queryFrom) {
        return R.ok();
    }

    /**
    * 查询车辆管理详情
    *
    * @param id 主键ID
    * @return 结果
    */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
    * 查询车辆管理列表
    *
    * @return 结果
    */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
    * 新增车辆管理
    *
    * @param addFrom 车辆管理对象
    * @return 结果
    */
    @PostMapping
    public R save(VehicleInfo addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
    * 修改车辆管理
    *
    * @param editFrom 车辆管理对象
    * @return 结果
    */
    @PutMapping
    public R edit(VehicleInfo editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
    * 删除车辆管理
    *
    * @param ids 删除的主键ID
    * @return 结果
    */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
