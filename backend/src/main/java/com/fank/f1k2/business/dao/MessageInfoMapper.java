package com.fank.f1k2.business.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.MessageInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface MessageInfoMapper extends BaseMapper<MessageInfo> {

    /**
     * 分页获取消息管理
     *
     * @param page      分页对象
     * @param queryFrom 消息管理
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<MessageInfo> page, @Param("queryFrom") MessageInfo queryFrom);
}
