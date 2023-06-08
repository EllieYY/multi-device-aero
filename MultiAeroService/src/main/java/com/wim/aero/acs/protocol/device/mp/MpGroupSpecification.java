package com.wim.aero.acs.protocol.device.mp;

import com.wim.aero.acs.message.Operation;
import com.wim.aero.acs.util.ProtocolFiledUtil.CmdProp;
import lombok.Data;

import java.util.List;

/**
 * @title: MpGroupSpecification
 * @author: Ellie
 * @date: 2022/03/11 16:33
 * @description: 7.4 Command 120: Configure Monitor Point Group
 **/
@Data
public class MpGroupSpecification extends Operation {
    @CmdProp(index = 2)
    private int lastModified = 0;

    @CmdProp(index = 3)
    private int scpNumber;

    @CmdProp(index = 4)
    private int mpgNumber; // Monitor point group number (0-255)

    @CmdProp(index = 5)
    private int nMpCount;  // Number of monitor points in this group. Maximum value is 128.

    @CmdProp(index = 6, enCodec = "formatMpList")
    private List<MpType> nMpList;

    public void updateMpCount() {
        nMpCount = nMpList.size();
    }
}
