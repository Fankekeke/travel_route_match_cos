package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ComplaintInfo;
import com.fank.f1k2.business.dao.ComplaintInfoMapper;
import com.fank.f1k2.business.service.IComplaintInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class ComplaintInfoServiceImpl extends ServiceImpl<ComplaintInfoMapper, ComplaintInfo> implements IComplaintInfoService {

    /**
     * 分页获取投诉记录
     *
     * @param page      分页对象
     * @param queryFrom 投诉记录
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Objects>> queryComplaintPage(Page<ComplaintInfo> page, ComplaintInfo queryFrom) {
        return baseMapper.queryComplaintPage(page, queryFrom);
    }
}
