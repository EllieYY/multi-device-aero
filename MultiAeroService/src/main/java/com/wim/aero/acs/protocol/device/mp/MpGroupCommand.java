package com.wim.aero.acs.protocol.device.mp;

import com.wim.aero.acs.message.Operation;
import com.wim.aero.acs.util.ProtocolFiledUtil.CmdProp;
import lombok.Data;

/**
 * @title: MpGroupCommand
 * @author: Ellie
 * @date: 2022/03/11 21:24
 * @description: 一键设防、撤防。 7.7 Command 321: Monitor Point Group Arm/Disarm Command
 *
 * Command Command code description
 * 1    Access:If the mask count is zero, mask all monitor points and increment the mask count
 *      by one.
 * 2    Override:Set mask count to arg1. If arg1 is zero, then all points get unmasked. If arg1
 *      is not zero, then all points get masked.
 * 3    Force Arm:If the mask count > 1 then decrement the mask count by 1. Otherwise, if the
 *      mask count is equal to 1, unmask all non-active monitor points and set the mask count to
 *      zero.
 * 4    Arm:If the mask count > 1 then decrement the mask count by one. Otherwise, if the mask
 *      count is equal to 1 and no monitor points are active, unmask all monitor points. and set
 *      the mask count to zero.
 * 5    Override arm:If the mask count > 1 then decrement the mask count by one, otherwise if
 *      the mask count is 1 unmask all monitor points and set the mask count to zero
 **/
@Data
public class MpGroupCommand extends Operation {
    @CmdProp(index = 2)
    private int scpNumber;

    @CmdProp(index = 3)
    private int mpgNumber;

    @CmdProp(index = 4)
    private int command;

    @CmdProp(index = 5)
    private int arg = 0;

    // isMask true撤防 false设防
    public static MpGroupCommand setMask(int scpNumber, int mpgNumber, boolean isMask) {
        MpGroupCommand result = new MpGroupCommand();
        result.setScpNumber(scpNumber);
        result.setMpgNumber(mpgNumber);
        result.setCommand(2);

        if (isMask) {
            result.setArg(1);
        }

        return result;
    }
}
