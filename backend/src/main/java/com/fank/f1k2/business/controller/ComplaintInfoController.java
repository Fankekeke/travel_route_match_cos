package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ComplaintInfo;
import com.fank.f1k2.business.service.IComplaintInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 投诉记录 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/complaint-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComplaintInfoController {

    private final IComplaintInfoService complaintInfoService;

    /**
     * 分页获取投诉记录
     *
     * @param page      分页对象
     * @param queryFrom 投诉记录
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<ComplaintInfo> page, ComplaintInfo queryFrom) {
        return R.ok(complaintInfoService.queryComplaintPage(page, queryFrom));
    }

    /**
     * 查询投诉记录详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(complaintInfoService.getById(id));
    }

    /**
     * 查询投诉记录列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(complaintInfoService.list());
    }

    /**
     * 新增投诉记录
     *
     * @param addFrom 投诉记录对象
     * @return 结果
     */
    @PostMapping
    public R save(ComplaintInfo addFrom) {
        addFrom.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(complaintInfoService.save(addFrom));
    }

    /**
     * 修改投诉记录
     *
     * @param editFrom 投诉记录对象
     * @return 结果
     */
    @PutMapping
    public R edit(ComplaintInfo editFrom) {
        return R.ok(complaintInfoService.updateById(editFrom));
    }

    /**
     * 删除投诉记录
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(complaintInfoService.removeByIds(ids));
    }

}
