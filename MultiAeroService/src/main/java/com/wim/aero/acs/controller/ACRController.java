package com.wim.aero.acs.controller;

import com.wim.aero.acs.config.Constants;
import com.wim.aero.acs.model.request.AcrMaskRequestInfo;
import com.wim.aero.acs.model.request.AcrRequestInfo;
import com.wim.aero.acs.model.request.AcrUnlockRequestInfo;
import com.wim.aero.acs.model.result.RespCode;
import com.wim.aero.acs.model.result.ResultBean;
import com.wim.aero.acs.model.result.ResultBeanUtil;
import com.wim.aero.acs.protocol.device.cp.ControlPointCommandType;
import com.wim.aero.acs.protocol.device.reader.AcrMode;
import com.wim.aero.acs.service.SioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: ACRController
 * @author: Ellie
 * @date: 2022/03/21 09:56
 * @description:
 **/
@RestController
@Slf4j
@RequestMapping("/device/acr")
@Api(tags = "读卡器远程控制接口")
public class ACRController {
    private final SioService sioService;
    @Autowired
    public ACRController(SioService sioService) {
        this.sioService = sioService;
    }

    @Deprecated
    @ApiOperation(value = "开门、关门 （弃用）")
    @RequestMapping(value = "/strike/command", method = {RequestMethod.POST})
    public ResultBean<String> doorOpenAndClose(@RequestBody AcrRequestInfo request) {
//        // 命令类型校验
//        int commandCode = request.getCommand();
//        ControlPointCommandType type = ControlPointCommandType.fromTypeCode(commandCode);
//        if (type == ControlPointCommandType.UNKNOWN) {
//            return ResultBeanUtil.makeResp(RespCode.INVALID_PARAM, request.toString());
//        }
//        int code = sioService.sendControlPointCommand(request, request.getScpId(), request.getId(), type);
//        if (code == Constants.REST_CODE_SUCCESS) {
//            return ResultBeanUtil.makeOkResp("命令下发成功");
//        }

        return ResultBeanUtil.makeResp(RespCode.CMD_DOWNLOAD_FAIL, "接口已弃用，请修改调用");
    }

    @ApiOperation(value = "开门")
    @RequestMapping(value = "/strike/open", method = {RequestMethod.POST})
    public ResultBean<String> doorOpen(@RequestBody AcrUnlockRequestInfo request) {

        int code = sioService.doorMomentaryUnlock(request, request.getScpId(), request.getAcrId(), request.getStrikeTime());
        if (code == Constants.REST_CODE_SUCCESS) {
            return ResultBeanUtil.makeOkResp("命令下发成功");
        }

        return ResultBeanUtil.makeResp(RespCode.CMD_DOWNLOAD_FAIL, "错误码：" + code);
    }

    @ApiOperation(value = "读卡器绑定输入点设防撤防")
    @RequestMapping(value = "/mask", method = {RequestMethod.POST})
    public ResultBean<String> acrMask(@RequestBody AcrMaskRequestInfo request) {

        int code = sioService.acrMask(request, request.getScpId(), request.getAcrId(), request.isFlag());
        if (code == Constants.REST_CODE_SUCCESS) {
            return ResultBeanUtil.makeOkResp("命令下发成功");
        }

        return ResultBeanUtil.makeResp(RespCode.CMD_DOWNLOAD_FAIL, "错误码：" + code);
    }


    @ApiOperation(value = "设置读卡器模式 - 常开、常闭等")
    @RequestMapping(value = "/mode", method = {RequestMethod.POST})
    public ResultBean<String> doorClose(@RequestBody AcrRequestInfo request) {
        // 命令类型校验
        int modeCode = request.getCommand();
        AcrMode mode = AcrMode.fromTypeCode(modeCode);
        if (mode == AcrMode.UNKNOWN) {
            return ResultBeanUtil.makeResp(RespCode.INVALID_PARAM, request.toString());
        }

        int code = sioService.setAcrMode(request, request.getScpId(), request.getId(), request.getCommand());
        if (code == Constants.REST_CODE_SUCCESS) {
            return ResultBeanUtil.makeOkResp("命令下发成功");
        }

        return ResultBeanUtil.makeResp(RespCode.CMD_DOWNLOAD_FAIL, "错误码：" + code);
    }


}
