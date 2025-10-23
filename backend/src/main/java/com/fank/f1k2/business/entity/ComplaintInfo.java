package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 投诉记录
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ComplaintInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 投诉时间
     */
    private String createDate;

    /**
     * 所属车主
     */
    private Integer staffId;

    /**
     * 投诉内容
     */
    private String content;

    /**
     * 状态（0.未处理 1.已处理）
     */
    private String status;


}
