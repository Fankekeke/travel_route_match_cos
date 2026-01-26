package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.VehicleInfo;
import com.fank.f1k2.business.dao.VehicleInfoMapper;
import com.fank.f1k2.business.service.IVehicleInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class VehicleInfoServiceImpl extends ServiceImpl<VehicleInfoMapper, VehicleInfo> implements IVehicleInfoService {

    /**
     * 分页获取车辆管理
     *
     * @param page      分页对象
     * @param queryFrom 车辆管理
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<VehicleInfo> page, VehicleInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
