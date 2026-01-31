package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.dao.UserInfoMapper;
import com.fank.f1k2.business.entity.RouteInfo;
import com.fank.f1k2.business.dao.RouteInfoMapper;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IRouteInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fank.f1k2.common.exception.F1k2Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class RouteInfoServiceImpl extends ServiceImpl<RouteInfoMapper, RouteInfo> implements IRouteInfoService {

    private final UserInfoMapper userInfoMapper;

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

    /**
     * 添加用户路线
     *
     * @param routeInfo 用户路线
     * @return 添加结果
     */
    @Override
    public Boolean addRouteUser(RouteInfo routeInfo) throws F1k2Exception {
        // 获取用户信息
        UserInfo userInfo = userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, routeInfo.getUserId()));
        if (userInfo == null) {
            throw new F1k2Exception("用户不存在");
        }
        routeInfo.setUserId(userInfo.getId());
        routeInfo.setStatus("-1");
        // 判断用户是否存在未完成的路线
        List<RouteInfo> unfinishedRoute = list(Wrappers.<RouteInfo>lambdaQuery().eq(RouteInfo::getUserId, routeInfo.getUserId()).ne(RouteInfo::getStatus, "3"));
        if (unfinishedRoute != null && unfinishedRoute.size() > 0) {
            throw new F1k2Exception("用户存在未完成路线");
        }
        return save(routeInfo);
    }
}
