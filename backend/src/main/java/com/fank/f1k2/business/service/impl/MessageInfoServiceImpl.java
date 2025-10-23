package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.MessageInfo;
import com.fank.f1k2.business.dao.MessageInfoMapper;
import com.fank.f1k2.business.service.IMessageInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class MessageInfoServiceImpl extends ServiceImpl<MessageInfoMapper, MessageInfo> implements IMessageInfoService {

    /**
     * 分页获取消息管理
     *
     * @param page      分页对象
     * @param queryFrom 消息管理
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<MessageInfo> page, MessageInfo queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
