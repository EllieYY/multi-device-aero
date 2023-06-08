package com.wim.aero.acs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Ellie
 * @since 2022-03-22
 */
@Getter
@Setter
@TableName("d_access_level")
@ApiModel(value = "DAccessLevel对象", description = "")
public class DAccessLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "access_level_id", type = IdType.AUTO)
    private Integer accessLevelId;

    @TableField("access_level_name")
    private String accessLevelName;

    @TableField("begin_date")
    private Date beginDate;

    @TableField("end_date")
    private Date endDate;

    @TableField("flag")
    private String flag;

    @TableField("status")
    private String status;

    @TableField("create_by")
    private String createBy;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_by")
    private String updateBy;

    @TableField("update_time")
    private Date updateTime;


}
