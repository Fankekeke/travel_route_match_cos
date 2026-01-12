package com.fank.f1k2.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.dao.StaffInfoMapper;
import com.fank.f1k2.business.dao.UserInfoMapper;
import com.fank.f1k2.business.entity.RouteInfo;
import com.fank.f1k2.business.entity.RouteStaffInfo;
import com.fank.f1k2.business.dao.RouteStaffInfoMapper;
import com.fank.f1k2.business.service.IRouteInfoService;
import com.fank.f1k2.business.service.IRouteStaffInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fank.f1k2.business.service.IStaffIncomeService;
import com.fank.f1k2.common.exception.F1k2Exception;
import com.fank.f1k2.common.utils.TrajectoryMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RouteStaffInfoServiceImpl extends ServiceImpl<RouteStaffInfoMapper, RouteStaffInfo> implements IRouteStaffInfoService {

    private final IStaffIncomeService staffIncomeService;

    private final IRouteInfoService routeInfoService;

    private final UserInfoMapper userInfoMapper;

    private final StaffInfoMapper staffInfoMapper;

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

    /**
     * 车找人
     *
     * @param staffRouteId 车主路线ID
     * @return 结果
     */
    @Override
    public List<RouteInfo> queryRouteUserList(Integer staffRouteId) throws F1k2Exception {
        // 获取车主路线信息
        RouteStaffInfo routeStaffInfo = this.getById(staffRouteId);
        if (routeStaffInfo == null) {
            throw new F1k2Exception("没有找到该车主路线信息");
        }
        DateTime earliestTime = DateUtil.parse(routeStaffInfo.getEarliestTime());
        DateTime latestTime = DateUtil.parse(routeStaffInfo.getLatestTime());
        // 获取所有未接单的用户路线
        List<RouteInfo> routeInfoList = routeInfoService.list(Wrappers.<RouteInfo>lambdaQuery().eq(RouteInfo::getStatus, -1)
                .between(RouteInfo::getEarliestTime, earliestTime, latestTime).or().between(RouteInfo::getLatestTime, earliestTime, latestTime));
        if (CollectionUtil.isEmpty(routeInfoList)) {
            return Collections.emptyList();
        }
        JSONArray historyTrack = JSONUtil.parseArray(routeStaffInfo.getPath());
        // 计算轨迹匹配度
        for (RouteInfo routeInfo : routeInfoList) {
            JSONArray roadCoordinates = JSONUtil.parseArray(routeInfo.getPath());
            BigDecimal matchRate = TrajectoryMatcher.calculatePointMatchRate(historyTrack, roadCoordinates);
            routeInfo.setMatchRate(matchRate.intValue());
        }
        // 用户信息
        List<Integer> userIdList = routeInfoList.stream().map(RouteInfo::getUserId).distinct().collect(Collectors.toList());
        List<LinkedHashMap<String, Object>> userInfoList = userInfoMapper.queryUserByIds(userIdList);
        for (RouteInfo routeInfo : routeInfoList) {
            routeInfo.setUserInfo(userInfoList.stream().filter(userInfo -> userInfo.get("id").equals(routeInfo.getUserId())).findFirst().orElse(null));
        }
        return routeInfoList;
    }

    /**
     * 人找车
     *
     * @param userRouteId 车主路线ID
     * @return 结果
     */
    @Override
    public List<RouteStaffInfo> queryRouteStaffList(Integer userRouteId) throws F1k2Exception {
        // 获取用户路线信息
        RouteInfo routeInfo = routeInfoService.getById(userRouteId);
        if (routeInfo == null) {
            throw new F1k2Exception("没有找到该用户路线信息");
        }
        DateTime earliestTime = DateUtil.parse(routeInfo.getEarliestTime());
        DateTime latestTime = DateUtil.parse(routeInfo.getLatestTime());
        // 获取所有未接单的车主路线
        List<RouteStaffInfo> routeStaffInfoList = this.list(Wrappers.<RouteStaffInfo>lambdaQuery().eq(RouteStaffInfo::getStatus, 0)
                .between(RouteStaffInfo::getEarliestTime, earliestTime, latestTime).or().between(RouteStaffInfo::getLatestTime, earliestTime, latestTime));
        if (CollectionUtil.isEmpty(routeStaffInfoList)) {
            return Collections.emptyList();
        }
        JSONArray historyTrack = JSONUtil.parseArray(routeInfo.getPath());
        // 计算轨迹匹配度
        for (RouteStaffInfo routeStaffInfo : routeStaffInfoList) {
            JSONArray roadCoordinates = JSONUtil.parseArray(routeStaffInfo.getPath());
            BigDecimal matchRate = TrajectoryMatcher.calculatePointMatchRate(historyTrack, roadCoordinates);
            routeStaffInfo.setMatchRate(matchRate.intValue());
        }
        // 用户信息
        List<Integer> staffIdList = routeStaffInfoList.stream().map(RouteStaffInfo::getStaffId).distinct().collect(Collectors.toList());
        List<LinkedHashMap<String, Object>> staffInfoList = staffInfoMapper.queryStaffByIds(staffIdList);
        for (RouteStaffInfo routeStaffInfo : routeStaffInfoList) {
            routeStaffInfo.setStaffInfo(staffInfoList.stream().filter(staffInfo -> staffInfo.get("id").equals(routeStaffInfo.getStaffId())).findFirst().orElse(null));
        }
        return routeStaffInfoList;
    }
}
