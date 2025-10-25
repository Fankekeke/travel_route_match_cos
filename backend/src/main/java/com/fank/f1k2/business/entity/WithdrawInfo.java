package com.fank.f1k2.business.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 提现记录
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WithdrawInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 提现编号
     */
    private String code;


    /**
     * 车主ID
     */
    private Integer staffId;

    /**
     * 提现金额
     */
    private BigDecimal withdrawPrice;

    /**
     * 账户余额
     */
    private BigDecimal accountPrice;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 审核状态（0.待审核 1.通过 2.驳回）
     */
    private String status;


}
