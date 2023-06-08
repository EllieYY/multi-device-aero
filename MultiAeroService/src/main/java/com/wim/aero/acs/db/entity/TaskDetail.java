package com.wim.aero.acs.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Ellie
 * @since 2022-04-21
 */
@Getter
@Setter
@AllArgsConstructor
@TableName("task_detail")
@ApiModel(value = "TaskDetail对象", description = "")
public class TaskDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("task_id")
    private long taskId;

    @TableField("task_name")
    private String taskName;

    @TableField("task_source")
    private Integer taskSource;

    @TableField("message")
    private String message;

    @TableField("start_time")
    private Date startTime;

    @TableField("msg_return_time")
    private Date msgReturnTime;

    @TableField("msg_over_time")
    private Date msgOverTime;

    @TableField("status")
    private String status;

    @TableId("uid")
    private String uid;

    @TableField("detail")
    private String detail;

    @TableField("card_no")
    private String cardNo;

    @TableField("scp_id")
    private Integer scpId;

    public TaskDetail(long taskId, String taskName, Integer taskSource,
                      String message, Date startTime, Date msgReturnTime, Date msgOverTime,
                      String status, String uid, String detail) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskSource = taskSource;
        this.message = message;
        this.startTime = startTime;
        this.msgReturnTime = msgReturnTime;
        this.msgOverTime = msgOverTime;
        this.status = status;
        this.uid = uid;
        this.detail = detail;
    }
}
