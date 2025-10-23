package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.PriceRules;
import com.fank.f1k2.business.service.IPriceRulesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 价格规则 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/price-rules")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PriceRulesController {

    private final IPriceRulesService priceRulesService;

    /**
     * 分页获取价格规则
     *
     * @param page      分页对象
     * @param queryFrom 价格规则
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<PriceRules> page, PriceRules queryFrom) {
        return R.ok(priceRulesService.queryPage(page, queryFrom));
    }

    /**
     * 查询价格规则详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(priceRulesService.getById(id));
    }

    /**
     * 查询价格规则列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(priceRulesService.list());
    }

    /**
     * 新增价格规则
     *
     * @param addFrom 价格规则对象
     * @return 结果
     */
    @PostMapping
    public R save(PriceRules addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(priceRulesService.save(addFrom));
    }

    /**
     * 修改价格规则
     *
     * @param editFrom 价格规则对象
     * @return 结果
     */
    @PutMapping
    public R edit(PriceRules editFrom) {
        return R.ok(priceRulesService.updateById(editFrom));
    }

    /**
     * 删除价格规则
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(priceRulesService.removeByIds(ids));
    }

}
