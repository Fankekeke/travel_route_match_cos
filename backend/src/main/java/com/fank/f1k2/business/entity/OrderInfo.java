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
 * 订单信息
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 订单编号
     */
    private String code;

    /**
     * 订单名称
     */
    private String orderName;

    /**
     * 所属用户
     */
    private Integer userId;

    /**
     * 总价格
     */
    private BigDecimal total;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 订单状态（-1.确认中 0.已确认 1.已接客 2.已送达 3.已支付 4.已拒绝 5.已取消）
     */
    private String status;

    /**
     * 车主ID
     */
    private Integer staffId;

    /**
     * 下单时间
     */
    private String payDate;

    /**
     * 用户路线
     */
    private Integer userRouteId;

    /**
     * 车主路线
     */
    private Integer staffRouteId;

    /**
     * 接客时间
     */
    private String receiveDate;

    /**
     * 送达时间
     */
    private String deliveryDate;

    /**
     * 公里数
     */
    private BigDecimal kilometre;

    /**
     * 订单价格
     */
    private BigDecimal orderPrice;

    /**
     * 优惠券ID
     */
    private Integer discountId;

    /**
     * 折扣后价格
     */
    private BigDecimal afterOrderPrice;

    /**
     * 积分
     */
    private BigDecimal integral;

    /**
     * 预计分钟
     */
    private Integer planMinute;

    /**
     * 实际分钟
     */
    private Integer actualMinute;

    /**
     * AI价格备注
     */
    private String aiRemark;

    /**
     * 配送排序
     */
    private Integer deliverIndexNo;

    /**
     * 用户路线信息
     */
    @TableField(exist = false)
    private RouteInfo routeInfo;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private UserInfo userInfo;

    /**
     * 评价信息
     */
    @TableField(exist = false)
    private EvaluateInfo evaluateInfo;

    @TableField(exist = false)
    private String staffName;

    @TableField(exist = false)
    private String userName;
}
