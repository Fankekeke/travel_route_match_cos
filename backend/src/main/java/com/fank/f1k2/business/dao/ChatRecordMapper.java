package com.fank.f1k2.business.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fank.f1k2.business.entity.ChatRecord;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface ChatRecordMapper extends BaseMapper<ChatRecord> {

    /**
     * 根据车主ID获取沟通联系人列表
     *
     * @param staffId 车主ID
     * @return 联系人列表
     */
    List<LinkedHashMap<String, Object>> getContactsByStaffId(@Param("staffId") Integer staffId);

    /**
     * 根据用户ID获取沟通联系人列表
     *
     * @param userId 用户ID
     * @return 联系人列表
     */
    List<LinkedHashMap<String, Object>> getContactsByUserId(@Param("userId") Integer userId);
}
