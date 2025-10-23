package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.PaymentRecord;
import com.fank.f1k2.business.service.IPaymentRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 付款记录 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/payment-record")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentRecordController {

    private final IPaymentRecordService paymentRecordService;

    /**
     * 分页获取付款记录
     *
     * @param page      分页对象
     * @param queryFrom 付款记录
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<PaymentRecord> page, PaymentRecord queryFrom) {
        return R.ok(paymentRecordService.queryPage(page, queryFrom));
    }

    /**
     * 查询付款记录详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(paymentRecordService.getById(id));
    }

    /**
     * 查询付款记录列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(paymentRecordService.list());
    }

    /**
     * 新增付款记录
     *
     * @param addFrom 付款记录对象
     * @return 结果
     */
    @PostMapping
    public R save(PaymentRecord addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(paymentRecordService.save(addFrom));
    }

    /**
     * 修改付款记录
     *
     * @param editFrom 付款记录对象
     * @return 结果
     */
    @PutMapping
    public R edit(PaymentRecord editFrom) {
        return R.ok(paymentRecordService.updateById(editFrom));
    }

    /**
     * 删除付款记录
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(paymentRecordService.removeByIds(ids));
    }

}
