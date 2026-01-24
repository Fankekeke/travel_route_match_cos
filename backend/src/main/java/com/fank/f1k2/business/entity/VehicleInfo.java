package com.fank.f1k2.business.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 车辆管理
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class VehicleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 车辆编号
     */
    private String vehicleNo;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 车辆颜色
     */
    private String vehicleColor;

    /**
     * 车辆名称
     */
    private String name;

    /**
     * 发动机号码
     */
    private String engineNo;

    /**
     * 载客数量
     */
    private Integer carryPassengers;

    /**
     * 所属品牌
     */
    private String brand;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 出场日期
     */
    private LocalDate factoryDate;

    /**
     * 车辆类型（1.轿车 2.商务车 3.大巴）
     */
    private Integer useType;

    /**
     * 排放标准
     */
    private String emissionStandard;

    /**
     * 燃料类型（1.燃油 2.柴油 3.油电混动 4.电能）
     */
    private String fuelType;

    /**
     * 照片
     */
    private String images;

    /**
     * 备注
     */
    private String content;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 所属车主
     */
    private Integer staffId;

    /**
     * 座位数量
     */
    private Integer seatNum;

}
