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
@TableName("d_holiday")
@ApiModel(value = "DHoliday对象", description = "")
public class DHoliday implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "holiday_id", type = IdType.AUTO)
    private Integer holidayId;

    @TableField("holiday_name")
    private String holidayName;

    @TableField("holiday_type")
    private Integer holidayType;

    @TableField("begin_date")
    private Date beginDate;

    @TableField("continued_day")
    private Integer continuedDay;

    @TableField("status")
    private String status;

    @TableField("remark")
    private String remark;

    @TableField("create_by")
    private String createBy;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_by")
    private String updateBy;

    @TableField("update_time")
    private Date updateTime;


}
