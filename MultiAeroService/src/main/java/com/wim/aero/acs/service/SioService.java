package com.wim.aero.acs.service;

import com.wim.aero.acs.db.entity.DevInputDetail;
import com.wim.aero.acs.db.entity.DevOutputDetail;
import com.wim.aero.acs.db.entity.DevReaderDetail;
import com.wim.aero.acs.db.entity.DevXDetail;
import com.wim.aero.acs.db.service.impl.DevInputDetailServiceImpl;
import com.wim.aero.acs.db.service.impl.DevOutputDetailServiceImpl;
import com.wim.aero.acs.db.service.impl.DevReaderDetailServiceImpl;
import com.wim.aero.acs.db.service.impl.DevXDetailServiceImpl;
import com.wim.aero.acs.message.RequestMessage;
import com.wim.aero.acs.model.command.ScpCmd;
import com.wim.aero.acs.model.command.ScpCmdResponse;
import com.wim.aero.acs.model.request.AcrRequestInfo;
import com.wim.aero.acs.model.request.CpRequestInfo;
import com.wim.aero.acs.model.request.ScpRequestInfo;
import com.wim.aero.acs.model.request.TaskRequest;
import com.wim.aero.acs.protocol.device.*;
import com.wim.aero.acs.protocol.device.cp.ControlPointCommand;
import com.wim.aero.acs.protocol.device.cp.ControlPointCommandType;
import com.wim.aero.acs.protocol.device.cp.ControlPointConfig;
import com.wim.aero.acs.protocol.device.cp.OutputPointSpecification;
import com.wim.aero.acs.protocol.device.mp.InputPointSpecification;
import com.wim.aero.acs.protocol.device.mp.MonitorPointConfig;
import com.wim.aero.acs.protocol.device.mp.MonitorPointMask;
import com.wim.aero.acs.protocol.device.mp.MpGroupCommand;
import com.wim.aero.acs.protocol.device.reader.*;
import com.wim.aero.acs.util.IdUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @title: SIOService
 * @author: Ellie
 * @date: 2022/03/23 15:38
 * @description: SIO板配置及读卡器、报警点、控制点远程控制命令下发
 **/
@Service
@Slf4j
public class SioService {
    private final DevXDetailServiceImpl sioDetailService;
    private final DevInputDetailServiceImpl inputDetailService;
    private final DevOutputDetailServiceImpl outputDetailService;
    private final DevReaderDetailServiceImpl readerDetailService;
    private final RestUtil restUtil;
    private final RequestPendingCenter requestPendingCenter;
    @Autowired
    public SioService(DevXDetailServiceImpl sioDetailService,
                      DevInputDetailServiceImpl inputDetailService,
                      DevOutputDetailServiceImpl outputDetailService,
                      DevReaderDetailServiceImpl readerDetailService,
                      RestUtil restUtil,
                      RequestPendingCenter requestPendingCenter) {
        this.sioDetailService = sioDetailService;
        this.inputDetailService = inputDetailService;
        this.outputDetailService = outputDetailService;
        this.readerDetailService = readerDetailService;
        this.restUtil = restUtil;
        this.requestPendingCenter = requestPendingCenter;
    }

    /**
     * 硬件配置
     * @param
     */
    public void configSioForScp(ScpRequestInfo requestInfo, List<ScpCmd> cmdList) {
        int scpId = requestInfo.getScpId();
//        List<ScpCmd> cmdList = new ArrayList<>();

        // 按照sio板配置
        sioDriverConfig(scpId, cmdList);
        sioAllConfig(scpId, cmdList);
//        sioConfig(scpId, cmdList);
//        inputConfig(scpId, cmdList);
//        outputConfig(scpId, cmdList);
//        readerConfig(scpId, cmdList);

        // readerLED配置
        readerLEDConfig(scpId, cmdList);


        log.info("[{} - sio配置]", scpId);
//        for(ScpCmd cmd:cmdList) {
//            log.info(cmd.getCommand());
//        }

//        requestPendingCenter.sendCmdList(requestInfo, cmdList);
    }


    /**
     * 读卡器mode设置
     * @param scpId
     * @param acrId
     * @param mode
     */
    public int setAcrMode(TaskRequest request, int scpId, int acrId, int mode) {
        ACRModeConfig config = new ACRModeConfig(scpId, acrId, mode);
        String msg = RequestMessage.encode(scpId, config);
        log.info("[{} - ACR Mode]常开常闭 msg={}", scpId, msg);

        ScpCmd cmd = new ScpCmd(scpId, msg, IdUtil.nextId());
        return requestPendingCenter.sendCmd(request, cmd);
    }

    /**
     * 读卡器开门
     * @param scpId
     * @param acrId
     * @param strikeTM 开门时间
     */
    public int doorMomentaryUnlock(TaskRequest request, int scpId, int acrId, int strikeTM) {
        // 311
        MomentaryUnlock config = new MomentaryUnlock();
        config.setScpNumber(scpId);
        config.setAcrNumber(acrId);
        config.setStrkTm(strikeTM);

        String msg = RequestMessage.encode(scpId, config);
        log.info("[{} - ACR MomentaryUnlock]开门 msg={}", scpId, msg);

        ScpCmd cmd = new ScpCmd(scpId, msg, IdUtil.nextId());
        return requestPendingCenter.sendCmd(request, cmd);
    }

    /**
     * 读卡器开门
     * @param scpId
     * @param acrId
     * @param setAlarm true设防 false 撤防
     */
    public int acrMask(TaskRequest request, int scpId, int acrId, boolean setAlarm) {
        int isClear = setAlarm ? 0 : 1;

        List<ScpCmd> cmdList = new ArrayList<>();

        HeldOpenMask heldOpenMask = new HeldOpenMask(scpId, acrId, isClear);
        String heldOpenMaskMsg = RequestMessage.encode(scpId, heldOpenMask);
        log.info("[{} - HeldOpenMask]设防撤防 msg={}", scpId, heldOpenMaskMsg);
        cmdList.add(new ScpCmd(scpId, heldOpenMaskMsg, IdUtil.nextId()));

        ForcedOpenMask forcedOpenMask = new ForcedOpenMask(scpId, acrId, isClear);
        String forceOpenMaskMsg = RequestMessage.encode(scpId, forcedOpenMask);
        log.info("[{} - forceOpenMask]设防撤防 msg={}", scpId, forceOpenMaskMsg);
        cmdList.add(new ScpCmd(scpId, forceOpenMaskMsg, IdUtil.nextId()));

        return requestPendingCenter.sendCmdList(request, cmdList);
    }


    /**
     * 控制点远程控制命令
     * @param scpId
     * @param cpId
     * @param type
     */
    public int sendControlPointCommand(TaskRequest requestInfo, int scpId, int cpId, ControlPointCommandType type) {
        ControlPointCommand command = new ControlPointCommand(scpId, cpId, type.getCode());
        String msg = RequestMessage.encode(scpId, command);
        log.info("[{} - 控制点命令发送] msg={}", scpId, msg);

        ScpCmd cmd = new ScpCmd(scpId, msg, IdUtil.nextId());
        return requestPendingCenter.sendCmd(requestInfo, cmd);
    }

    /**
     * 报警点设防和撤防
     * @param isMask true撤防 false设防
     */
    public int maskMp(TaskRequest requestInfo, int scpId, int mpId, boolean isMask) {
        MonitorPointMask mask = new MonitorPointMask(scpId, mpId, isMask);
        String msg = RequestMessage.encode(scpId, mask);
        log.info("[{} - MP - 设防/撤防] msg={}", scpId, msg);

        ScpCmd cmd = new ScpCmd(scpId, msg, IdUtil.nextId());
        return requestPendingCenter.sendCmd(requestInfo, cmd);
    }

    /**
     * 防区一键撤防和设防
     * @param scpId
     * @param mpgId
     * @param isMask true撤防 false设防
     * @return
     */
    public int maskMpg(TaskRequest request, int scpId, int mpgId, boolean isMask) {
        MpGroupCommand mask = MpGroupCommand.setMask(scpId, mpgId, isMask);
        String msg = RequestMessage.encode(scpId, mask);
        log.info("[{} - MPGroup - 设防/撤防] msg={}", scpId, msg);

        ScpCmd cmd = new ScpCmd(scpId, msg, IdUtil.nextId());
        return requestPendingCenter.sendCmd(request, cmd);
    }


    /**
     * 防区一键撤防和设防
     * @param scpId
     * @param mpgIdList
     * @param isMask true撤防 false设防
     * @return
     */
    public int maskMultiMpg(TaskRequest request, int scpId, List<Integer> mpgIdList, boolean isMask) {
        List<ScpCmd> cmdList = new ArrayList<>();
        for (Integer mpgId:mpgIdList) {
            MpGroupCommand mask = MpGroupCommand.setMask(scpId, mpgId, isMask);
            String msg = RequestMessage.encode(scpId, mask);
            log.info("[{} - MPGroup - 设防/撤防] msg={}", scpId, msg);

            ScpCmd cmd = new ScpCmd(scpId, msg, IdUtil.nextId());
            cmdList.add(cmd);
        }

        return requestPendingCenter.sendCmdList(request, cmdList);
    }


    /**
     * sio配置
     * @param scpId
     * @param cmdList
     */
    public void sioConfig(int scpId, List<ScpCmd> cmdList) {
        // MSP1(SIO)Comm. Driver Configuration (Command 108) -- // 一个控制器2个
        cmdList.add(packageDriver(scpId, 0,3, 0, 0));
        // 查询哪个端口配的V系列，哪个是X系列
        List<Integer> xPortList = sioDetailService.getPortBySioModel(scpId, Arrays.asList(193, 194, 195));
        List<Integer> vPortList = sioDetailService.getPortBySioModel(scpId, Arrays.asList(190, 191, 192));

        int xPortSize = xPortList.size();
        int vPortSize = vPortList.size();

        if ((xPortSize + vPortSize) > 2 || xPortSize == 2) {
            log.error("[sio驱动端口配置数据错误]{} - x {}:v {}", scpId, xPortList.toString(), vPortList.toString());
            cmdList.add(packageDriver(scpId, 1, 1, 38400, 0));
            cmdList.add(packageDriver(scpId, 2, 2, 38400, 0));
        } else if (vPortSize == 2) {
            cmdList.add(packageDriver(scpId, 1, 1, 38400, 15));
            cmdList.add(packageDriver(scpId, 2, 2, 38400, 15));
        } else {
            if (xPortSize >= 1) {
                int xPort = xPortList.get(0);
                cmdList.add(packageDriver(scpId, xPort, xPort, 38400, 0));
            }

            if (vPortSize >= 1) {
                int vPort = vPortList.get(0);
                cmdList.add(packageDriver(scpId, vPort, vPort, 38400, 15));
            }
        }

        // 查找所有sio
        List<DevXDetail> sioList = sioDetailService.getByScpId(scpId);
        for (DevXDetail sio:sioList) {
            int sioId = sio.getSioNumber();
            boolean isV = sioDetailService.isVBord(scpId, sioId);

            // SIOPanel Configuration (Command 109)
            SIOSpecification specification = SIOSpecification.fromDb(sio);
            String msg = RequestMessage.encode(scpId, specification);
            cmdList.add(new ScpCmd(scpId, msg, IdUtil.nextId(), isV));
        }
    }

    /**
     * sio配置，包含input | output | reader的配置
     * @param scpId
     * @param cmdList
     */
    public void sioDriverConfig(int scpId, List<ScpCmd> cmdList) {
        // MSP1(SIO)Comm. Driver Configuration (Command 108) -- // 一个控制器2个
        cmdList.add(packageDriver(scpId, 0,3, 0, 0));
        // 查询哪个端口配的V系列，哪个是X系列
        List<Integer> xPortList = sioDetailService.getPortBySioModel(scpId, Arrays.asList(193, 194, 195));
        List<Integer> vPortList = sioDetailService.getPortBySioModel(scpId, Arrays.asList(190, 191, 192));

        int xPortSize = xPortList.size();
        int vPortSize = vPortList.size();

        if ((xPortSize + vPortSize) > 2 || xPortSize == 2) {
            log.error("[sio驱动端口配置数据错误]{} - x {}:v {}", scpId, xPortList.toString(), vPortList.toString());
            cmdList.add(packageDriver(scpId, 1, 1, 38400, 0));
            cmdList.add(packageDriver(scpId, 2, 2, 38400, 0));
        } else if (vPortSize == 2) {
            cmdList.add(packageDriver(scpId, 1, 1, 38400, 15));
            cmdList.add(packageDriver(scpId, 2, 2, 38400, 15));
        } else {
            if (xPortSize >= 1) {
                int xPort = xPortList.get(0);
                cmdList.add(packageDriver(scpId, xPort, xPort, 38400, 0));
            }

            if (vPortSize >= 1) {
                int vPort = vPortList.get(0);
                cmdList.add(packageDriver(scpId, vPort, vPort, 38400, 15));
            }
        }
    }

    /**
     * sio配置，包含input | output | reader的配置
     * @param scpId
     * @param cmdList
     */
    public void sioAllConfig(int scpId, List<ScpCmd> cmdList) {
        // 查找所有sio
        List<DevXDetail> sioList = sioDetailService.getByScpId(scpId);
        for (DevXDetail sio:sioList) {
            int sioId = sio.getSioNumber();
            boolean isV = sioDetailService.isVBord(scpId, sioId);

            // SIOPanel Configuration (Command 109)
            SIOSpecification specification = SIOSpecification.fromDb(sio);
            String msg = RequestMessage.encode(scpId, specification);
            cmdList.add(new ScpCmd(scpId, msg, IdUtil.nextId(), isV));

            inputConfigBySio(scpId, sioId, cmdList);
            outputConfigBySio(scpId, sioId, cmdList);
            readerConfigBySio(scpId, sioId, cmdList);

        }
    }


    //    Type of Protocol
    // *      *  0 = HID Aero™ X100, X200 and X300 protocol
    // *      * 15 = VertX V100, V200 and V300 protocol
    // *      * 16 = Aperio
    private ScpCmd packageDriver(int scpId, int mspNo, int port, int baudRate, int type) {
        // MSP1(SIO)Comm. Driver Configuration (Command 108) -- // 一个控制器3个
        SIODriver driver = new SIODriver(scpId, mspNo, port, baudRate, type);
        String driverMsg = RequestMessage.encode(scpId, driver);
        return new ScpCmd(scpId, driverMsg, IdUtil.nextId());
    }

    /**
     * 输入点（报警点）配置
     * @param scpId
     * @param cmdList
     */
    public void inputConfig(int scpId, List<ScpCmd> cmdList) {
        List<DevInputDetail> devInputDetails = inputDetailService.getByScpId(scpId);
        for (DevInputDetail input:devInputDetails) {
            // 控制器scpId、sio板编号、输入点物理编号
            int sioId = input.getSioNumber();
            int inputNo = input.getInput();
            boolean isV = sioDetailService.isVBord(scpId, sioId);

            // Input Point Configuration (Command 110)
            InputPointSpecification specification = InputPointSpecification.fromDb(input);
            String specificationMsg = RequestMessage.encode(scpId, specification);
            cmdList.add(new ScpCmd(scpId, specificationMsg, IdUtil.nextId(), isV));

            // 判断是否是已配置
            if (readerDetailService.isInputConfigured(scpId, sioId, inputNo)) {
                continue;
            }

            // Monitor Point Configuration(Command113)
            MonitorPointConfig config = MonitorPointConfig.fromDb(input);
            String configMsg = RequestMessage.encode(scpId, config);
            cmdList.add(new ScpCmd(scpId, configMsg, IdUtil.nextId()));
        }
    }

    /**
     * 输入点（报警点）配置
     * @param scpId
     * @param cmdList
     */
    public void inputConfigBySio(int scpId, int sioId, List<ScpCmd> cmdList) {
        List<DevInputDetail> devInputDetails = inputDetailService.getByScpIdAndSioId(scpId, sioId);
        for (DevInputDetail input:devInputDetails) {
            // 控制器scpId、sio板编号、输入点物理编号
            int inputNo = input.getInput();
//            boolean isV = sioDetailService.isVBord(scpId, sioId);

            // Input Point Configuration (Command 110)
            InputPointSpecification specification = InputPointSpecification.fromDb(input);
            String specificationMsg = RequestMessage.encode(scpId, specification);
            cmdList.add(new ScpCmd(scpId, specificationMsg, IdUtil.nextId()));

            // 判断是否是已配置
            if (readerDetailService.isInputConfigured(scpId, sioId, inputNo)) {
                continue;
            }

            // Monitor Point Configuration(Command113)
            MonitorPointConfig config = MonitorPointConfig.fromDb(input);
            String configMsg = RequestMessage.encode(scpId, config);
            cmdList.add(new ScpCmd(scpId, configMsg, IdUtil.nextId()));
        }
    }


    /**
     * 输出点（控制点）配置
     * @param scpId
     * @param cmdList
     */
    public void outputConfig(int scpId, List<ScpCmd> cmdList) {
        List<DevOutputDetail> devOutputDetails = outputDetailService.getByScpId(scpId);
        for (DevOutputDetail output:devOutputDetails) {
            int sioId = output.getSioNumber();
//            boolean isV = sioDetailService.isVBord(scpId, sioId);

            // OutputPointConfiguration (Command 111)
            OutputPointSpecification specification = OutputPointSpecification.fromDb(output);
            String specificationMsg = RequestMessage.encode(scpId, specification);
            cmdList.add(new ScpCmd(scpId, specificationMsg, IdUtil.nextId()));

            // 控制器scpId、sio板编号、输出点物理编号
            int outputNo = output.getOutput();
            // 判断是否是已配置
            if (readerDetailService.isOutputConfigured(scpId, sioId, outputNo)) {
                continue;
            }

            // ControlPointConfiguration (Command 114)
            ControlPointConfig config = ControlPointConfig.fromDb(output);
            String configMsg = RequestMessage.encode(scpId, config);
            cmdList.add(new ScpCmd(scpId, configMsg, IdUtil.nextId()));

        }
    }

    /**
     * 输出点（控制点）配置
     * @param scpId
     * @param cmdList
     */
    public void outputConfigBySio(int scpId, int sioId, List<ScpCmd> cmdList) {
        List<DevOutputDetail> devOutputDetails = outputDetailService.getByScpIdAndSio(scpId, sioId);
        for (DevOutputDetail output:devOutputDetails) {
//            boolean isV = sioDetailService.isVBord(scpId, sioId);

            // OutputPointConfiguration (Command 111)
            OutputPointSpecification specification = OutputPointSpecification.fromDb(output);
            String specificationMsg = RequestMessage.encode(scpId, specification);
            cmdList.add(new ScpCmd(scpId, specificationMsg, IdUtil.nextId()));

            // 控制器scpId、sio板编号、输出点物理编号
            int outputNo = output.getOutput();
            // 判断是否是已配置
            if (readerDetailService.isOutputConfigured(scpId, sioId, outputNo)) {
                continue;
            }

            // ControlPointConfiguration (Command 114)
            ControlPointConfig config = ControlPointConfig.fromDb(output);
            String configMsg = RequestMessage.encode(scpId, config);
            cmdList.add(new ScpCmd(scpId, configMsg, IdUtil.nextId()));

        }
    }


    /**
     * 读卡器（ACR）配置
     * @param scpId
     * @param cmdList
     */
    public void readerConfig(int scpId, List<ScpCmd> cmdList) {
        List<DevReaderDetail> readerDetails = readerDetailService.getByScpId(scpId);
        for (DevReaderDetail reader:readerDetails) {
            int sioId = reader.getSioNumber();
            boolean isV = sioDetailService.isVBord(scpId, sioId);

            // Card Reader Configuration(Command112)
            ReaderSpecification specification = ReaderSpecification.fromDb(reader);
            String specificationMsg = RequestMessage.encode(scpId, specification);
            cmdList.add(new ScpCmd(scpId, specificationMsg, IdUtil.nextId(), isV));

            // Access Control Reader Configuration(Command115)
            ACRConfig config = ACRConfig.fromDb(reader);
            String configMsg = RequestMessage.encode(scpId, config);
            cmdList.add(new ScpCmd(scpId, configMsg, IdUtil.nextId()));
        }
    }

    /**
     * 读卡器（ACR）配置
     * @param scpId
     * @param cmdList
     */
    public void readerConfigBySio(int scpId, int sioId, List<ScpCmd> cmdList) {
        List<DevReaderDetail> readerDetails = readerDetailService.getByScpIdAndSio(scpId, sioId);
        for (DevReaderDetail reader:readerDetails) {
//            int sioId = reader.getSioNumber();
            boolean isV = sioDetailService.isVBord(scpId, sioId);

            // Card Reader Configuration(Command112)
            ReaderSpecification specification = ReaderSpecification.fromDb(reader);
            String specificationMsg = RequestMessage.encode(scpId, specification);
            cmdList.add(new ScpCmd(scpId, specificationMsg, IdUtil.nextId(), isV));

            // Access Control Reader Configuration(Command115)
            ACRConfig config = ACRConfig.fromDb(reader);
            String configMsg = RequestMessage.encode(scpId, config);
            cmdList.add(new ScpCmd(scpId, configMsg, IdUtil.nextId()));
        }
    }

    /**
     * reader LED配置
     * @param scpId
     * @param cmdList
     */
    public void readerLEDConfig(int scpId, List<ScpCmd> cmdList) {
        ReaderLED operation = ReaderLED.defualtSetting(scpId);
        String msg = RequestMessage.encode(scpId, operation);
        cmdList.add(new ScpCmd(scpId, msg, IdUtil.nextId()));
    }


}
