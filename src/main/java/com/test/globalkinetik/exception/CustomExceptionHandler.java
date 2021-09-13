package com.test.globalkinetik.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author S'phokuhle on 9/13/2021
 */
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * This method listens to the controllers
     * if Runtime exception is thrown by any of the controller services,
     * this will catch those exceptions and return customized error object
     * @param ex takes runtimeException object
     * @param request takes WebRequest object
     * @return error code, uri path, error message and the timestamp
     */
    @ExceptionHandler(value
            = { Exception.class})
    public final ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
        log.warn("Exception caught");
        CustomExceptionObject customExceptionObject = new CustomExceptionObject();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest servletRequest = ((ServletRequestAttributes)requestAttributes).getRequest();
            customExceptionObject.setPath(servletRequest.getRequestURI());
        }

        if(ex instanceof IllegalArgumentException || ex instanceof HttpClientErrorException.BadRequest) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        customExceptionObject.setError(ex.getMessage());
        customExceptionObject.setStatus(httpStatus.value());
        customExceptionObject.setTimestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
        return handleExceptionInternal(ex, customExceptionObject,
                new HttpHeaders(), httpStatus, request);
    }
}
