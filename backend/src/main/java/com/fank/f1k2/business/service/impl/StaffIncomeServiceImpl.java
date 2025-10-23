package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.StaffIncome;
import com.fank.f1k2.business.dao.StaffIncomeMapper;
import com.fank.f1k2.business.service.IStaffIncomeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class StaffIncomeServiceImpl extends ServiceImpl<StaffIncomeMapper, StaffIncome> implements IStaffIncomeService {

    /**
     * 分页获取车主收益
     *
     * @param page      分页对象
     * @param queryFrom 车主收益
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<StaffIncome> page, StaffIncome queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
