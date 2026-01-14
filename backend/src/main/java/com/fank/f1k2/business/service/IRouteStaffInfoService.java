package com.fank.f1k2.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RouteInfo;
import com.fank.f1k2.business.entity.RouteStaffInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fank.f1k2.common.exception.F1k2Exception;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface IRouteStaffInfoService extends IService<RouteStaffInfo> {

    /**
     * 分页获取车主路线
     *
     * @param page      分页对象
     * @param queryFrom 车主路线
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<RouteStaffInfo> page, RouteStaffInfo queryFrom);

    /**
     * 车找人
     *
     * @param staffRouteId 车主路线ID
     * @return 结果
     */
    List<RouteInfo> queryRouteUserList(Integer staffRouteId) throws F1k2Exception;

    /**
     * 人找车
     *
     * @param userRouteId 车主路线ID
     * @return 结果
     */
    List<RouteStaffInfo> queryRouteStaffList(Integer userRouteId) throws F1k2Exception;

    /**
     * 添加车主路线
     *
     * @param routeStaffInfo 车主路线
     * @return 结果
     */
    Boolean addRouteStaff(RouteStaffInfo routeStaffInfo) throws F1k2Exception;
}
