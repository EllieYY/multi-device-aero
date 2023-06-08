package com.wim.aero.acs.model.scp.transaction;

import com.wim.aero.acs.model.mq.LogMessage;
import com.wim.aero.acs.service.QueueProducer;
import lombok.Data;

import java.util.Date;

/**
 * @title: TypeAsci
 * @author: Ellie
 * @date: 2022/04/20 11:05
 * @description:
 **/
@Data
public class TypeAsci extends TransactionBody {
    private String bfr;

    @Override
    public void process(QueueProducer queueProducer, SCPReplyTransaction transaction) {
//        int scpId = transaction.getScpId();
//        long date = transaction.getTime() * 1000;
//        long index = transaction.getSerNum();
//        int sourceType = transaction.getSourceType();
//        int sourceNum = transaction.getSourceNumber();
//        int tranType = transaction.getTranType();
//        int tranCode = transaction.getTranCode();
//
//        queueProducer.sendLogMessage(new LogMessage(index, date, scpId, sourceType, sourceNum, tranType, tranCode, this.toString()));
    }
}
