package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 审核管理
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AuditInfo implements Serializable {

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
     * 车主人脸图片
     */
    private String images;

    /**
     * 申请时间
     */
    private String createDate;

    /**
     * 审核状态 0.未审核 1.审核通过 2.驳回
     */
    private Integer auditStatus;

    /**
     * 状态时间
     */
    private String statusDate;

    /**
     * 审核内容
     */
    private String introduction;

    /**
     * 标签
     */
    private String tag;

    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private String idNumber;

    @TableField(exist = false)
    private String phone;


}
