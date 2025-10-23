package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.PriceRules;
import com.fank.f1k2.business.dao.PriceRulesMapper;
import com.fank.f1k2.business.service.IPriceRulesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class PriceRulesServiceImpl extends ServiceImpl<PriceRulesMapper, PriceRules> implements IPriceRulesService {

    /**
     * 分页获取价格规则
     *
     * @param page      分页对象
     * @param queryFrom 价格规则
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<PriceRules> page, PriceRules queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
