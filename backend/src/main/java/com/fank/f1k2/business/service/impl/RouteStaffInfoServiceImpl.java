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
import com.fank.f1k2.business.entity.PriceRules;
import com.fank.f1k2.business.entity.RouteInfo;
import com.fank.f1k2.business.entity.RouteStaffInfo;
import com.fank.f1k2.business.dao.RouteStaffInfoMapper;
import com.fank.f1k2.business.service.IPriceRulesService;
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

    private final IPriceRulesService priceRulesService;

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

    /**
     * 添加车主路线
     *
     * @param routeStaffInfo 车主路线
     * @return 结果
     */
    @Override
    public Boolean addRouteStaff(RouteStaffInfo routeStaffInfo) {
        routeStaffInfo.setStatus("0");
        // 计算预估价格
        BigDecimal estimatedPrice = calculateEstimatedPrice(routeStaffInfo);
        routeStaffInfo.setPlanPriceUnit(estimatedPrice);

        return null;
    }

    /**
     * 根据路线信息和价格规则计算预估价格
     *
     * @param routeStaffInfo 路线信息
     * @return 预估价格
     */
    private BigDecimal calculateEstimatedPrice(RouteStaffInfo routeStaffInfo) {
        List<PriceRules> rulesList = priceRulesService.list();

        // 按照规则类型分类
        PriceRules startRule = findRuleByCode(rulesList, "DIST_START");  // 起步价
        PriceRules nearRule = findRuleByCode(rulesList, "DIST_NEAR");    // 近途单价
        PriceRules midRule = findRuleByCode(rulesList, "DIST_MID");      // 中途单价
        PriceRules farRule = findRuleByCode(rulesList, "DIST_FAR");      // 远途单价
        PriceRules timeRule = findRuleByCode(rulesList, "TIME_WAIT");    // 时长费
        PriceRules nightRule = findRuleByCode(rulesList, "NIGHT_RATIO"); // 夜间系数

        // 获取路线距离
        BigDecimal totalDistance = routeStaffInfo.getDistance() != null ? routeStaffInfo.getDistance() : BigDecimal.ZERO;

        // 获取预计用时（分钟）
        BigDecimal totalTime = routeStaffInfo.getDuration() != null ?
                new BigDecimal(routeStaffInfo.getDuration()) : BigDecimal.ZERO;

        // 计算基础费用
        BigDecimal basePrice = calculateBasePrice(totalDistance, startRule, nearRule, midRule, farRule);

        // 计算等待时间费用
        BigDecimal waitPrice = timeRule != null ?
                totalTime.multiply(timeRule.getUnitPrice()) : BigDecimal.ZERO;

        // 计算总费用
        BigDecimal totalPrice = basePrice.add(waitPrice);

        // 判断是否为夜间服务并应用系数
        DateTime departureEarliestTime = DateUtil.parse(routeStaffInfo.getEarliestTime());
        DateTime departureLatestTime = DateUtil.parse(routeStaffInfo.getEarliestTime());
        int earliestHour = departureEarliestTime.hour(true);
        int latestHour = departureLatestTime.hour(true);
        if (isNightHour(earliestHour) || isNightHour(latestHour)) {
            // 应用夜间系数
            BigDecimal nightRatio = nightRule != null ? nightRule.getUnitPrice() : new BigDecimal("1.2");
            totalPrice = totalPrice.multiply(nightRatio);
        }

        return totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP); // 保留两位小数
    }

    /**
     * 计算基础价格（按距离分段计费）
     */
    private BigDecimal calculateBasePrice(BigDecimal distance, PriceRules startRule,
                                          PriceRules nearRule, PriceRules midRule, PriceRules farRule) {
        BigDecimal basePrice = BigDecimal.ZERO;
        BigDecimal remainingDistance = distance;

        if (remainingDistance.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 1. 计算起步价（0-3公里）
        if (startRule != null) {
            // 固定起步价
            basePrice = startRule.getUnitPrice();
            remainingDistance = remainingDistance.subtract(new BigDecimal("3"));

            if (remainingDistance.compareTo(BigDecimal.ZERO) <= 0) {
                // 如果总距离不超过3公里，只收起步价
                return basePrice;
            }
        } else {
            // 如果没有起步价规则，从原距离开始计算
            remainingDistance = distance;
        }

        // 2. 计算3-20公里部分
        if (nearRule != null && remainingDistance.compareTo(BigDecimal.ZERO) > 0) {
            // 最多17公里（20-3）
            BigDecimal nearDistance = remainingDistance.min(new BigDecimal("17"));
            basePrice = basePrice.add(nearDistance.multiply(nearRule.getUnitPrice()));
            remainingDistance = remainingDistance.subtract(nearDistance);

            if (remainingDistance.compareTo(BigDecimal.ZERO) <= 0) {
                return basePrice;
            }
        }

        // 3. 计算20-100公里部分
        if (midRule != null && remainingDistance.compareTo(BigDecimal.ZERO) > 0) {
            // 最多80公里（100-20）
            BigDecimal midDistance = remainingDistance.min(new BigDecimal("80"));
            basePrice = basePrice.add(midDistance.multiply(midRule.getUnitPrice()));
            remainingDistance = remainingDistance.subtract(midDistance);

            if (remainingDistance.compareTo(BigDecimal.ZERO) <= 0) {
                return basePrice;
            }
        }

        // 4. 计算100公里以上部分
        if (farRule != null && remainingDistance.compareTo(BigDecimal.ZERO) > 0) {
            basePrice = basePrice.add(remainingDistance.multiply(farRule.getUnitPrice()));
        }

        return basePrice;
    }

    /**
     * 根据编码查找价格规则
     */
    private PriceRules findRuleByCode(List<PriceRules> rulesList, String code) {
        return rulesList.stream()
                .filter(rule -> code.equals(rule.getCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断是否为夜间时段
     */
    private boolean isNightHour(int hour) {
        // 23点到次日5点
        return hour >= 23 || hour < 5;
    }
}
