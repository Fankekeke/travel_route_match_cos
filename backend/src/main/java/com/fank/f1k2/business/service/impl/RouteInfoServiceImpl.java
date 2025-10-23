package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RouteInfo;
import com.fank.f1k2.business.dao.RouteInfoMapper;
import com.fank.f1k2.business.service.IRouteInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class RouteInfoServiceImpl extends ServiceImpl<RouteInfoMapper, RouteInfo> implements IRouteInfoService {

    /**
     * 分页获取用户路线
     *
     * @param page       分页对象
     * @param queryFrom 用户路线
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<RouteInfo> page, RouteInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
