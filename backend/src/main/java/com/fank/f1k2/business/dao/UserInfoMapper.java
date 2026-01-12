package com.fank.f1k2.business.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 分页获取用户管理
     *
     * @param page      分页对象
     * @param queryFrom 用户管理
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<UserInfo> page, @Param("queryFrom") UserInfo queryFrom);

    /**
     * 根据用户ID列表查询用户信息
     *
     * @param userIdList 用户ID列表
     * @return 用户信息列表
     */
    List<LinkedHashMap<String, Object>> queryUserByIds(@Param("userIdList") List<Integer> userIdList);
}
