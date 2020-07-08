package com.cienet.shipment.exception;

import com.cienet.shipment.vo.ApiCode;
import com.cienet.shipment.vo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler {

    /**
     * @param exception
     * @return
     */
    @ExceptionHandler(value = {ShipmentException.class})
    @ResponseStatus(HttpStatus.OK)
    public ApiResult shipmentExceptionHandler(ShipmentException exception) {
        log.error("shipmentExceptionHandler:{}", exception.getMessage());
        return new ApiResult()
            .setCode(ApiCode.FAIL.getCode())
            .setMsg(exception.getMessage());
    }

    /**
     * @param exception
     * @return
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult accessDeniedExceptionHandler(AccessDeniedException exception) {
        log.error("exception:{}", exception.getMessage());
        return ApiResult.result(ApiCode.NOT_PERMISSION, ApiCode.NOT_PERMISSION.getMsg(), null);
    }

    /**
     * @param exception
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult exceptionHandler(Exception exception) {
        log.error("exception:{}", exception.getMessage());
        return ApiResult.result(ApiCode.FAIL, null);
    }
}
