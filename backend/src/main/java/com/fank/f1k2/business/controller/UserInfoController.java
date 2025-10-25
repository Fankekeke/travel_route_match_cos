package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IUserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/user-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserInfoController {

    private final IUserInfoService userInfoService;

    /**
     * 分页获取用户管理
     *
     * @param page      分页对象
     * @param queryFrom 用户管理
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<UserInfo> page, UserInfo queryFrom) {
        return R.ok(userInfoService.queryPage(page, queryFrom));
    }

    /**
     * 获取用户ID获取用户详情
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/detailByUserId/{userId}")
    public R detailByUserId(@PathVariable("userId") Integer userId) {
        return R.ok(userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId)));
    }

    /**
     * 查询用户管理详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(userInfoService.getById(id));
    }

    /**
     * 查询用户管理列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(userInfoService.list());
    }

    /**
     * 新增用户管理
     *
     * @param addFrom 用户管理对象
     * @return 结果
     */
    @PostMapping
    public R save(UserInfo addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(userInfoService.save(addFrom));
    }

    /**
     * 修改用户管理
     *
     * @param editFrom 用户管理对象
     * @return 结果
     */
    @PutMapping
    public R edit(UserInfo editFrom) {
        return R.ok(userInfoService.updateById(editFrom));
    }

    /**
     * 删除用户管理
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(userInfoService.removeByIds(ids));
    }

}
