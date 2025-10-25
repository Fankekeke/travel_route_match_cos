package com.fank.f1k2.business.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 订单评价
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EvaluateInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 所属订单
     */
    private Integer orderId;

    /**
     * 评价用户
     */
    private Integer userId;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价分数
     */
    private BigDecimal score;

    /**
     * 评价时间
     */
    private String createDate;

    /**
     * 评价图片
     */
    private String images;

    /**
     * 评价所属车主
     */
    @TableField(exist = false)
    private Integer staffId;


}
