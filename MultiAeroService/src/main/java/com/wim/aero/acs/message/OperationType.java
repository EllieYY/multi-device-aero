package com.wim.aero.acs.message;

import com.wim.aero.acs.protocol.DaylightSavingTimeConfiguration;
import com.wim.aero.acs.protocol.TimeSet;
import com.wim.aero.acs.protocol.TransactionLogSetting;
import com.wim.aero.acs.protocol.accessLevel.*;
import com.wim.aero.acs.protocol.apb.AccessAreaConfig;
import com.wim.aero.acs.protocol.card.*;
import com.wim.aero.acs.protocol.device.*;
import com.wim.aero.acs.protocol.device.cp.ControlPointCommand;
import com.wim.aero.acs.protocol.device.cp.ControlPointConfig;
import com.wim.aero.acs.protocol.device.cp.ControlPointStatusCommand;
import com.wim.aero.acs.protocol.device.cp.OutputPointSpecification;
import com.wim.aero.acs.protocol.device.mp.*;
import com.wim.aero.acs.protocol.device.reader.*;
import com.wim.aero.acs.protocol.timezone.Holiday;
import com.wim.aero.acs.protocol.timezone.TimeZone;
import com.wim.aero.acs.protocol.trigger.*;

import java.util.function.Predicate;

/**
 * @title: OperationType
 * @author: Ellie
 * @date: 2022/02/10 14:03
 * @description:
 **/
public enum OperationType {
    /**-------------------------------------------------------------------------------*/
    /** 配置 */
    // 设备基本配置
    SCP_Driver_1013(1013, SCPDriver.class),
    DST(1116, DaylightSavingTimeConfiguration.class),
    SCP_CONFIG_1107(1107, SCPSpecification.class),
    SCP_SIO_108(108, SIODriver.class),
    ADS_1105(1105, AccessDatabaseSpecification.class),
    EL_501(501, ElevatorALsSpecification.class),
    TRANS_LOG(303, TransactionLogSetting.class),

    // sio
    SIO_109(109, SIOSpecification.class),
    INPUT_110(110, InputPointSpecification.class),
    OUTPUT_111(111, OutputPointSpecification.class),
    READER_112(112, ReaderSpecification.class),
    READER_LED(122, ReaderLED.class),
    MP_113(113, MonitorPointConfig.class),
    CP_114(114, ControlPointConfig.class),
    ACR_115(115, ACRConfig.class),
    ELE_ACR_115(115, EleACRConfig.class),


    MP_GROUP_120(120, MpGroupSpecification.class),

    // apb
    APB_1121(1121, AccessAreaConfig.class),

    // card
    CARD_MT_1102(1102, MT2CardFormat.class),
    CARD_WIEGAND_1102(1102, WiegandCardFormat.class),
    CARD_INFO_5304(5304, CardAdd.class),
    CARD_DEL(3305, CardDelete.class),

    // 访问级别
    AL_LIST_2116(2116, AccessLevelExtended.class),
    AL_124(124, AccessLevelTest.class),
    EAL_502(502, ElevatorALsConfiguration.class),
    READER_EL(1220, AccessLevelException.class),

    // 时间组
    HOLIDAY_1104(1104, Holiday.class),
    TIME_ZONE_3103(3103, TimeZone.class),

    // trigger
    PROCEDURE_CTRL(312, ProcedureControl.class),
    TRIGGER_VAR(313, TriggerVariableControl.class),
    TRIGGER_SPEC(117, TriggerSpecification.class),
    TRIGGER_SPEC_EX(1117, TriggerSpecificationExtend.class),
    ACTION_SPEC(118, ActionSpecification.class),
    ACTION_REMOVE(119, RemoveMatchingActions.class),

    /**-------------------------------------------------------------------------------*/
    /** 指令 */
    SCP_REST(301, ScpReset.class),
    SCP_REBOOT(913, ScpReboot.class),
    SCP_DETACH(15, ScpDelete.class),

    MP_MASK(306, MonitorPointMask.class),
    CP_COMMAND(307, ControlPointCommand.class),
    ACR_MODE(308, ACRModeConfig.class),
    FORCED_OPEN_MASK(309, ForcedOpenMask.class),
    HELD_OPEN_MASK(310, HeldOpenMask.class),
    ACR_UNLOCK(311, MomentaryUnlock.class),

    MP_GROUP_MASK(321, MpGroupCommand.class),

    TIME_SET(302, TimeSet.class),

    // user commands
    CMD_401(401, ScpIDRequest.class),
    CMD_404(404, SIOStatusCommand.class),
    CMD_405(405, MonitorPointStatusCommand.class),
    CMD_406(406, ControlPointStatusCommand.class),
    CMD_407(407, AcrStatusCommand.class);
    /**-------------------------------------------------------------------------------*/

    private int opCode;
    private Class<? extends Operation> operationClazz;

    OperationType(int opCode, Class<? extends Operation> operationClazz) {
        this.opCode = opCode;
        this.operationClazz = operationClazz;
    }

    public int getOpCode(){
        return opCode;
    }

    public Class<? extends Operation> getOperationClazz() {
        return operationClazz;
    }

    public static OperationType fromOpCode(int type){
        return getOperationType(requestType -> requestType.opCode == type);
    }

    public static OperationType fromOperation(Operation operation){
        return getOperationType(requestType -> requestType.operationClazz == operation.getClass());
    }

    private static OperationType getOperationType(Predicate<OperationType> predicate){
        OperationType[] values = values();
        for (OperationType operationType : values) {
            if(predicate.test(operationType)){
                return operationType;
            }
        }

        throw new AssertionError("no found type");
    }

    public static boolean isProtocolOpCode(int opCode) {
        OperationType[] values = values();
        for (OperationType operationType : values) {
            if(operationType.opCode == opCode){
                return true;
            }
        }
        return false;
    }
}
