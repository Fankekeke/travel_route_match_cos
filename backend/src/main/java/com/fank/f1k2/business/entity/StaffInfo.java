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
 * 车主管理
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StaffInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 车主编号
     */
    private String code;

    /**
     * 车主姓名
     */
    private String name;

    /**
     * 性别（1.男 2.女）
     */
    private String sex;

    /**
     * 状态（-1.未审核 1.接单中 2.离开）
     */
    private String status;

    /**
     * 照片
     */
    private String images;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 所属账户
     */
    private Integer userId;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 余额
     */
    private BigDecimal price;

    /**
     * 身份证地址
     */
    private String address;

    /**
     * 民族
     */
    private String ethnicity;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 出生日期
     */
    private String birthDate;

    /**
     * 身份证正面
     */
    private String idCardFrontImages;

    /**
     * 身份证反面
     */
    private String idCardReverseImages;
}
