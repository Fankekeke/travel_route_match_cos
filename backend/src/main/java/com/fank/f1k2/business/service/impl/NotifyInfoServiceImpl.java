package com.fank.f1k2.business.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.NotifyInfo;
import com.fank.f1k2.business.dao.NotifyInfoMapper;
import com.fank.f1k2.business.service.INotifyInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class NotifyInfoServiceImpl extends ServiceImpl<NotifyInfoMapper, NotifyInfo> implements INotifyInfoService {

    /**
     * 分页获取消息通知
     *
     * @param page      分页对象
     * @param queryFrom 消息通知
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<NotifyInfo> page, NotifyInfo queryFrom) {
        return baseMapper.queryPageStaff(page, queryFrom);
    }

    /**
     * 分页获取消息通知
     *
     * @param page      分页对象
     * @param queryFrom 消息通知
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPageUser(Page<NotifyInfo> page, NotifyInfo queryFrom) {
        return baseMapper.queryPageUser(page, queryFrom);
    }

    /**
     * 发送消息通知
     *
     * @param content 消息内容
     * @param userId  用户ID
     */
    @Override
    public void sendNotify(String content, Integer userId) {
        NotifyInfo notifyInfo = new NotifyInfo();
        notifyInfo.setUserId(userId);
        notifyInfo.setContent(content);
        notifyInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        notifyInfo.setDelFlag(0);
        this.save(notifyInfo);
    }
}
