package com.fank.f1k2.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.NotifyInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface INotifyInfoService extends IService<NotifyInfo> {

    /**
     * 分页获取消息通知
     *
     * @param page      分页对象
     * @param queryFrom 消息通知
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<NotifyInfo> page, NotifyInfo queryFrom);

    /**
     * 发送消息通知
     *
     * @param content 消息内容
     * @param userId  用户ID
     */
    void sendNotify(String content, Integer userId);
}
