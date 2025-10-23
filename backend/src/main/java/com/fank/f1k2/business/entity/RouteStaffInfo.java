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
 * 车主路线
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RouteStaffInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 所属车主
     */
    private Integer staffId;

    /**
     * 路线信息
     */
    private String path;

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
     * 最早出发时间
     */
    private String earliestTime;

    /**
     * 最迟出发时间
     */
    private String latestTime;

    /**
     * 乘坐人数
     */
    private Integer rideNum;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 删除标识
     */
    private String delFlag;

    /**
     * 订单状态（0.候补中 1.已完成 2.暂停）
     */
    private String status;

    /**
     * 是否自动接单（0.否 1.是）
     */
    private String autoOrder;


}
