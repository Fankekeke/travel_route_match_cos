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
 * 付款记录
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 用户编号
     */
    private String userCode;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 付款金额
     */
    private BigDecimal amount;

    /**
     * 付款时间
     */
    private String createDate;

    /**
     * 所属车主
     */
    private Integer staffId;

    @TableField(exist = false)
    private String staffName;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private Integer userId;




}
