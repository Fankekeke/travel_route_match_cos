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
 * 车主收益
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StaffIncome implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 车主ID
     */
    private Integer staffId;

    /**
     * 所属订单
     */
    private Integer orderId;

    /**
     * 订单收益
     */
    private BigDecimal income;

    /**
     * 配送费用
     */
    private BigDecimal deliveryPrice;

    /**
     * 总收益
     */
    private BigDecimal totalPrice;

    /**
     * 创建时间
     */
    private String createDate;

    @TableField(exist = false)
    private String staffName;

    @TableField(exist = false)
    private String userName;


}
