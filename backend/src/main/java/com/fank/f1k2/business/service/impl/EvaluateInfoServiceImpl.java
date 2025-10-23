package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.EvaluateInfo;
import com.fank.f1k2.business.dao.EvaluateInfoMapper;
import com.fank.f1k2.business.service.IEvaluateInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class EvaluateInfoServiceImpl extends ServiceImpl<EvaluateInfoMapper, EvaluateInfo> implements IEvaluateInfoService {

    /**
     * 分页获取订单评价
     *
     * @param page      分页对象
     * @param queryFrom 订单评价
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<EvaluateInfo> page, EvaluateInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
