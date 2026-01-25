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
 * 优惠券管理
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DiscountInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 编号
     */
    private String code;

    /**
     * 满减金额
     */
    private BigDecimal discountPrice;

    /**
     * 门槛金额
     */
    private BigDecimal threshold;

    /**
     * 所属用户
     */
    private Integer userId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 类型（1.满减 2.折扣）
     */
    private String type;

    /**
     * 发放时间
     */
    private String createDate;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String content;

    /**
     * 折扣
     */
    private BigDecimal rebate;

    /**
     * 用户名
     */
    @TableField(exist = false)
    private BigDecimal userName;


}
