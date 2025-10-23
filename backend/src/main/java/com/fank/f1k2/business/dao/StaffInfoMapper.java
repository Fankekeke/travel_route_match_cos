package com.fank.f1k2.business.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.StaffInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface StaffInfoMapper extends BaseMapper<StaffInfo> {

    /**
     * 分页获取车主管理
     *
     * @param page      分页对象
     * @param queryFrom 车主管理
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<StaffInfo> page, @Param("queryFrom") StaffInfo queryFrom);
}
