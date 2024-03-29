package com.wim.aero.acs.db.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;

/**
 * <p>
 * 
 * </p>
 *
 * @author Ellie
 * @since 2022-04-07
 */
@Getter
@Setter
@TableName("e_log_record")
@ApiModel(value = "ELogRecord对象", description = "")
public class ELogRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "seq_no", type = IdType.AUTO)
    private Integer seqNo;

    @TableField("event_index")
    private Long eventIndex;

    @TableField("events_time")
    private Date eventsTime;

    @TableField("controller_id")
    private Integer controllerId;

    @TableField("source_type")
    private Integer eventSourceType;

    @TableField("source_number")
    private Integer sourceCode;

    @TableField("tran_type")
    private Integer eventType;

    @TableField("tran_code")
    private Integer eventTypeCode;

    @TableField(value = "ins_time", fill = FieldFill.INSERT)
    private Date insTime;

    @TableField("full_memo")
    private String fullMemo;

    public ELogRecord(Long eventIndex, Date eventsTime,
                      Integer controllerId,
                      Integer eventSourceType, Integer sourceCode,
                      Integer eventType, Integer eventTypeCode,
                      String fullMemo) {
        this.eventIndex = eventIndex;
        this.eventsTime = eventsTime;
        this.controllerId = controllerId;
        this.eventSourceType = eventSourceType;
        this.sourceCode = sourceCode;
        this.eventType = eventType;
        this.eventTypeCode = eventTypeCode;
        this.fullMemo = fullMemo;
    }
}
