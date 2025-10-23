package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RouteStaffInfo;
import com.fank.f1k2.business.dao.RouteStaffInfoMapper;
import com.fank.f1k2.business.service.IRouteStaffInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class RouteStaffInfoServiceImpl extends ServiceImpl<RouteStaffInfoMapper, RouteStaffInfo> implements IRouteStaffInfoService {

    /**
     * 分页获取车主路线
     *
     * @param page      分页对象
     * @param queryFrom 车主路线
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<RouteStaffInfo> page, RouteStaffInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
