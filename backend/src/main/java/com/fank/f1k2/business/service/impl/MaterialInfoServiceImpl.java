package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.MaterialInfo;
import com.fank.f1k2.business.dao.MaterialInfoMapper;
import com.fank.f1k2.business.service.IMaterialInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class MaterialInfoServiceImpl extends ServiceImpl<MaterialInfoMapper, MaterialInfo> implements IMaterialInfoService {

    /**
     * 分页获取物品积分
     *
     * @param page       分页对象
     * @param queryFrom 物品积分
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<MaterialInfo> page, MaterialInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
