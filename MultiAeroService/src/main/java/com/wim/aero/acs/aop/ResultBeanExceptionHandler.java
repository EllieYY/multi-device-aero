package com.wim.aero.acs.aop;

import com.wim.aero.acs.aop.excption.DataDaoException;
import com.wim.aero.acs.aop.excption.ParamException;
import com.wim.aero.acs.aop.excption.ServiceException;
import com.wim.aero.acs.model.result.RespCode;
import com.wim.aero.acs.model.result.ResultBean;
import com.wim.aero.acs.model.result.ResultBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;


/**
 * @Description : 异常处理AOP
 * @Author : Ellie
 */
@Slf4j
@RestControllerAdvice
public class ResultBeanExceptionHandler {

    /**
     * 参数绑定错误时跳转到该处理器处理
     */
    @ExceptionHandler(BindException.class)
    public ResultBean<?> handleBeanPropertyBindingException(BindException exception, HttpServletResponse response) {

        StringBuilder builder = new StringBuilder();

        BindingResult result = exception.getBindingResult();
        List<FieldError> errList = result.getFieldErrors();
        for (FieldError fieldError : errList) {
            builder = builder.append("参数绑定错误，出错对象：").append(fieldError.getObjectName()).append("，出错字段：")
                    .append(fieldError.getField()).append("，错误值：").append(fieldError.getRejectedValue())
                    .append("，错误信息：").append(fieldError.getDefaultMessage()).append("。");
        }

        // 打印日志
        log.error(new String(builder), exception);
        return ResultBeanUtil.makeResp(exception);
    }

    /**
     * 如果数据库发生了异常（违反唯一约束，未找到父项关键字，数据库连接异常等），则由该
     * 异常处理器处理。该方法会对本次响应设置500响应状态码，并在message中返回异常的详细信息
     */
    @ExceptionHandler(SQLException.class)
    public ResultBean<?> handleSQLIntegrityException(SQLException exception, HttpServletResponse response) {
        log.error("SQL异常", exception);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Throwable throwable = exception;
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return ResultBeanUtil.makeResp(exception);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultBean<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("缺少请求参数", e);
        return ResultBeanUtil.makeParamErrResp("缺少请求参数");
    }

    /**
     * 缺少请求体异常处理器
     *
     * @param e 缺少请求体异常
     * @return ResponseResult
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultBean<?> parameterBodyMissingExceptionHandler(HttpMessageNotReadableException e) {
        return ResultBeanUtil.makeParamErrResp("参数体不能为空");
    }

    /**
     * 参数效验异常处理器
     *
     * @param e 参数验证异常
     * @return ResponseInfo
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultBean<?> parameterExceptionHandler(MethodArgumentNotValidException e) {
        BindingResult exceptions = e.getBindingResult();
        // 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                // 这里列出了全部错误参数，按正常逻辑，只需要第一条错误即可
                FieldError fieldError = (FieldError) errors.get(0);
                return ResultBeanUtil.makeParamErrResp(fieldError.getDefaultMessage());
            }
        }
        return ResultBeanUtil.makeParamErrResp("error");
    }

//    /**
//     * 自定义参数错误异常处理器
//     *
//     * @param e 自定义参数
//     * @return ResponseInfo
//     */
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler({ParamaErrorException.class})
//    public ResultBean<?> paramExceptionHandler(ParamaErrorException e) {
//        //log.error("", e);
//        // 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息
//        if (!StringUtils.hasText(e.getMessage())) {
//            return ResultBeanUtil.makePARAMErrResp(e.getMessage());
//        }
//        return ResultBeanUtil.makePARAMErrResp("error");
//    }


    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultBean<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("不支持当前请求方法", e);
        return ResultBeanUtil.makeCustomErrResp("request_method_not_supported");
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResultBean<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("不支持当前媒体类型", e);
        return ResultBeanUtil.makeCustomErrResp("content_type_not_supported");
    }

    /**
     * 业务层需要自己声明异常的情况
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException.class)
    public ResultBean<?> handleServiceException(ServiceException e) {
        log.error("业务逻辑异常", e);
        return ResultBeanUtil.makeCustomErrResp("业务逻辑异常：" + e.getMessage());
    }

    /**
     * 操作数据或库出现异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataDaoException.class)
    public ResultBean<?> handleException(DataDaoException e) {
        log.error("操作数据库异常:", e);
        return ResultBeanUtil.makeCustomErrResp("操作数据库出现异常：字段重复、有外键关联等");
    }


    /**
     * 参数异常
     */
    @ExceptionHandler(ParamException.class)
    public ResultBean<?> handleException(ParamException e) {
        log.error("参数异常:", e);
        return ResultBeanUtil.makeResp(RespCode.ERROR_PARAM, e.toString(), null);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResultBean<?> handleException(RuntimeException exception, HttpServletResponse response) {

        log.error("其他异常", exception);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Throwable throwable = exception;
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }

        return ResultBeanUtil.makeResp(exception);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ConnectTimeoutException.class)
    public ResultBean<?> handleException(ResourceAccessException exception, HttpServletResponse response) {

        log.error("通信服务异常", exception);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Throwable throwable = exception;
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }

        return ResultBeanUtil.makeResp(RespCode.COMM_SERVICE_FAIL, exception.getMessage());
    }

    @ExceptionHandler(AssertionError.class)
    public ResultBean<?> handleException(AssertionError exception, HttpServletResponse response) {

        log.error("数据异常", exception);
        return ResultBeanUtil.makeResp(RespCode.ERROR_PARAM, exception.getMessage());
    }
}
