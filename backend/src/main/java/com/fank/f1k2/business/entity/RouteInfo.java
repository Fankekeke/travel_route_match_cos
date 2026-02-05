package com.fank.f1k2.business.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.LinkedHashMap;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 用户路线
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RouteInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 所属用户
     */
    private Integer userId;

    /**
     * 路线信息
     */
    private String path;

    /**
     * 路线距离
     */
    private BigDecimal distance;

    /**
     * 出发地-经度
     */
    private BigDecimal startLongitude;

    /**
     * 出发地-纬度
     */
    private BigDecimal startLatitude;

    /**
     * 目的地-经度
     */
    private BigDecimal endLongitude;

    /**
     * 目的地-纬度
     */
    private BigDecimal endLatitude;

    /**
     * 出发地
     */
    private String startAddress;

    /**
     * 目的地
     */
    private String endAddress;

    /**
     * 最早出发时间
     */
    private String earliestTime;

    /**
     * 最迟出发时间
     */
    private String latestTime;

    /**
     * 类型（0.拼坐 1.独享）
     */
    private String type;

    /**
     * 乘坐人数
     */
    private Integer rideNum;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 高速费协商方式（0.部分协商 1.全部承担 2.不承担）
     */
    private String highwayTolls;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 删除标识
     */
    private String delFlag;

    /**
     * 订单状态（-1.待接单 0.待上车 1.已上车 2.已送达 3.已支付）
     */
    private String status;

    /**
     * 关联订单
     */
    private Integer orderId;

    /**
     * 预计分钟
     */
    private Integer planMinute;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 匹配度
     */
    @TableField(exist = false)
    private Integer matchRate;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private LinkedHashMap<String, Object> userInfo;

    /**
     * 起点距离
     */
    @TableField(exist = false)
    private double startDistance;

    /**
     * 终点距离
     */
    @TableField(exist = false)
    private double endDistance;

    @TableField(exist = false)
    private Integer month;

    @TableField(exist = false)
    private Integer day;

    @TableField(exist = false)
    private String staffName;

    @TableField(exist = false)
    private String userName;
}
