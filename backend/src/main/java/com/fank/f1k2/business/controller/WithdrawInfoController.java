package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.StaffInfo;
import com.fank.f1k2.business.service.IStaffInfoService;
import com.fank.f1k2.common.exception.F1k2Exception;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.WithdrawInfo;
import com.fank.f1k2.business.service.IWithdrawInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 提现记录 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/withdraw-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WithdrawInfoController {

    private final IWithdrawInfoService withdrawInfoService;

    private final IStaffInfoService staffInfoService;

    /**
     * 分页获取提现记录
     *
     * @param page      分页对象
     * @param queryFrom 提现记录
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<WithdrawInfo> page, WithdrawInfo queryFrom) {
        return R.ok(withdrawInfoService.queryPage(page, queryFrom));
    }

    /**
     * 新增提现记录信息
     *
     * @param withdrawInfo 提现记录信息
     * @return 结果
     */
    @PostMapping
    public R save(WithdrawInfo withdrawInfo) throws F1k2Exception {
        // 校验此车主是否有提现正在审核中
        int count = withdrawInfoService.count(Wrappers.<WithdrawInfo>lambdaQuery().eq(WithdrawInfo::getStatus, 0));
        if (count > 0) {
            throw new F1k2Exception("存在正在审核的提现记录！");
        }
        withdrawInfo.setCode("WD-" + System.currentTimeMillis());
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, withdrawInfo.getStaffId()));
        if (staffInfo != null) {
            withdrawInfo.setStaffId(staffInfo.getId());
        }
        withdrawInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(withdrawInfoService.save(withdrawInfo));
    }

    /**
     * 查询提现记录详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(withdrawInfoService.getById(id));
    }

    /**
     * 查询提现记录列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(withdrawInfoService.list());
    }

    /**
     * 修改提现记录
     *
     * @param editFrom 提现记录对象
     * @return 结果
     */
    @PutMapping
    public R edit(WithdrawInfo editFrom) {
        return R.ok(withdrawInfoService.updateById(editFrom));
    }

    /**
     * 删除提现记录
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(withdrawInfoService.removeByIds(ids));
    }

}
