package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ExchangeInfo;
import com.fank.f1k2.business.service.IExchangeInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 积分兑换 控制层
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/exchange-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExchangeInfoController {

    private final IExchangeInfoService exchangeInfoService;

    /**
    * 分页获取积分兑换
    *
    * @param page       分页对象
    * @param queryFrom 积分兑换
    * @return 结果
    */
    @GetMapping("/page")
    public R page(Page<ExchangeInfo> page, ExchangeInfo queryFrom) {
        return R.ok(exchangeInfoService.queryPage(page, queryFrom));
    }

    /**
    * 查询积分兑换详情
    *
    * @param id 主键ID
    * @return 结果
    */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(exchangeInfoService.getById(id));
    }

    /**
    * 查询积分兑换列表
    *
    * @return 结果
    */
    @GetMapping("/list")
    public R list() {
        return R.ok(exchangeInfoService.list());
    }

    /**
    * 新增积分兑换
    *
    * @param addFrom 积分兑换对象
    * @return 结果
    */
    @PostMapping
    public R save(ExchangeInfo addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(exchangeInfoService.save(addFrom));
    }

    /**
    * 修改积分兑换
    *
    * @param editFrom 积分兑换对象
    * @return 结果
    */
    @PutMapping
    public R edit(ExchangeInfo editFrom) {
        return R.ok(exchangeInfoService.updateById(editFrom));
    }

    /**
    * 删除积分兑换
    *
    * @param ids 删除的主键ID
    * @return 结果
    */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(exchangeInfoService.removeByIds(ids));
    }

}
