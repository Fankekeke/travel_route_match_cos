package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.StaffInfo;
import com.fank.f1k2.business.service.IStaffInfoService;
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
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/vehicle-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleInfoController {

    private final IVehicleInfoService vehicleInfoService;

    private final IStaffInfoService staffInfoService;

    /**
     * 分页获取车辆管理
     *
     * @param page      分页对象
     * @param queryFrom 车辆管理
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<VehicleInfo> page, VehicleInfo queryFrom) {
        return R.ok(vehicleInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询车辆管理详情
     *
     * @param userId 车主ID
     * @return 详情
     */
    @GetMapping("/queryVehicleByStaff")
    public R queryVehicleByStaff(Integer userId) {
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, userId));
        return R.ok(vehicleInfoService.list(Wrappers.<VehicleInfo>lambdaQuery().eq(VehicleInfo::getStaffId, staffInfo.getId())));
    }

    /**
     * 查询车辆管理详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(vehicleInfoService.getById(id));
    }

    /**
     * 查询车辆管理列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(vehicleInfoService.list());
    }

    /**
     * 新增车辆管理
     *
     * @param addFrom 车辆管理对象
     * @return 结果
     */
    @PostMapping
    public R save(VehicleInfo addFrom) {
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, addFrom.getStaffId()));
        addFrom.setStaffId(staffInfo.getId());
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(vehicleInfoService.save(addFrom));
    }

    /**
     * 修改车辆管理
     *
     * @param editFrom 车辆管理对象
     * @return 结果
     */
    @PutMapping
    public R edit(VehicleInfo editFrom) {
        return R.ok(vehicleInfoService.updateById(editFrom));
    }

    /**
     * 删除车辆管理
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(vehicleInfoService.removeByIds(ids));
    }

}
