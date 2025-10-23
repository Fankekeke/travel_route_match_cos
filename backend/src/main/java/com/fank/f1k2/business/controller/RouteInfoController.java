package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RouteInfo;
import com.fank.f1k2.business.service.IRouteInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 用户路线 控制层
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/route-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RouteInfoController {

    private final IRouteInfoService bulletinInfoService;

    /**
    * 分页获取用户路线
    *
    * @param page       分页对象
    * @param queryFrom 用户路线
    * @return 结果
    */
    @GetMapping("/page")
    public R page(Page<RouteInfo> page, RouteInfo queryFrom) {
        return R.ok();
    }

    /**
    * 查询用户路线详情
    *
    * @param id 主键ID
    * @return 结果
    */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
    * 查询用户路线列表
    *
    * @return 结果
    */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
    * 新增用户路线
    *
    * @param addFrom 用户路线对象
    * @return 结果
    */
    @PostMapping
    public R save(RouteInfo addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
    * 修改用户路线
    *
    * @param editFrom 用户路线对象
    * @return 结果
    */
    @PutMapping
    public R edit(RouteInfo editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
    * 删除用户路线
    *
    * @param ids 删除的主键ID
    * @return 结果
    */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
