package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.INotifyInfoService;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.EvaluateInfo;
import com.fank.f1k2.business.service.IEvaluateInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 订单评价 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/evaluate-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EvaluateInfoController {

    private final IEvaluateInfoService evaluateInfoService;

    private final IUserInfoService userInfoService;

    private final INotifyInfoService notifyInfoService;

    /**
     * 分页获取订单评价
     *
     * @param page      分页对象
     * @param queryFrom 订单评价
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<EvaluateInfo> page, EvaluateInfo queryFrom) {
        return R.ok(evaluateInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询订单评价详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(evaluateInfoService.getById(id));
    }

    /**
     * 查询订单评价列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(evaluateInfoService.list());
    }

    /**
     * 新增订单评价
     *
     * @param addFrom 订单评价对象
     * @return 结果
     */
    @PostMapping
    public R save(EvaluateInfo addFrom) {
        UserInfo userInfo = userInfoService.getById(addFrom.getUserId());
        // 添加车主消息通知
        String content = "用户：" + userInfo.getName() + "评价了出行订单，评价内容：" + addFrom.getContent();
        notifyInfoService.sendNotify(content, addFrom.getStaffId());
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(evaluateInfoService.save(addFrom));
    }

    /**
     * 修改订单评价
     *
     * @param editFrom 订单评价对象
     * @return 结果
     */
    @PutMapping
    public R edit(EvaluateInfo editFrom) {
        return R.ok(evaluateInfoService.updateById(editFrom));
    }

    /**
     * 删除订单评价
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(evaluateInfoService.removeByIds(ids));
    }

}
