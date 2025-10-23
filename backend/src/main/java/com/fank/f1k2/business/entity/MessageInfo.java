package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 消息管理
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MessageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 发送人
     */
    private Integer sendUser;

    /**
     * 接收人
     */
    private Integer takeUser;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 发送时间
     */
    private String createDate;

    /**
     * 状态 0.未读 1.已读
     */
    private Integer taskStatus;


}
