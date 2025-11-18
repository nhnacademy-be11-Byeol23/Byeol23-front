package com.nhnacademy.byeol23front.commons.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.ExecutionException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @ExceptionHandler(FeignException.class)
    public ModelAndView handleFeignException(FeignException e) {
        log.error("FeignException 발생: {}", e.getMessage());
        String body = e.contentUTF8();
        try {
            ErrorResponse errorResponse = objectMapper.readValue(body, ErrorResponse.class);
            return resolveErrorPage(errorResponse);
        } catch (JsonProcessingException ex) {
            jsonProcessingExceptionLog(ex);
            try {
                GatewayErrorResponse gatewayErrorResponse = objectMapper.readValue(body, GatewayErrorResponse.class);
                return resolveGatewayErrorPage(gatewayErrorResponse);
            } catch (JsonProcessingException exc) {
                jsonProcessingExceptionLog(ex);
                return resolveErrorPage(feignExceptionJsonProcessingException(e));
            }
        }
    }

    @ExceptionHandler(ExecutionException.class)
    public ModelAndView handleExecutionException(ExecutionException e) {
        Throwable cause = e.getCause();

        if(cause instanceof FeignException feignException) {
            String body = feignException.contentUTF8();
            try {
                ErrorResponse errorResponse = objectMapper.readValue(body, ErrorResponse.class);
                return resolveErrorPage(errorResponse);
            } catch (JsonProcessingException ex) {
                jsonProcessingExceptionLog(ex);
                return resolveErrorPage(feignExceptionJsonProcessingException(feignException));
            }
        }
        log.error("ExecutionException 발생: {}", cause.getMessage());
        return resolveErrorPage(ErrorResponse.defaultErrorResponse());
    }

    @ExceptionHandler(InterruptedException.class)
    public ModelAndView handleInterruptedException(InterruptedException e) {
        log.error("InterruptedException 발생: {}", e.getMessage());
        return resolveErrorPage(ErrorResponse.defaultErrorResponse());
    }
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        log.error("예외 발생: {}", e.getMessage());
        return resolveErrorPage(ErrorResponse.defaultErrorResponse());
    }

    private ModelAndView resolveGatewayErrorPage(GatewayErrorResponse errorResponse) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", errorResponse.status());
        modelAndView.addObject("error", errorResponse.error());
        modelAndView.addObject("path", errorResponse.path());
        modelAndView.addObject("timestamp", errorResponse.timestamp());
        modelAndView.setStatus(HttpStatusCode.valueOf(errorResponse.status()));

        return modelAndView;
    }

    private ModelAndView resolveErrorPage(ErrorResponse errorResponse) {
        ModelAndView modelAndView = new ModelAndView("error");
        int status = errorResponse.status() != -1 ? errorResponse.status() : 500;
        modelAndView.addObject("status", status);
        modelAndView.addObject("message", errorResponse.message());
        modelAndView.addObject("timestamp", errorResponse.timestamp());
        modelAndView.setStatus(HttpStatusCode.valueOf(status));

        return modelAndView;
    }

    private ErrorResponse feignExceptionJsonProcessingException(FeignException e) {
        return ErrorResponse.of(e.status(), "요청 처리 중 예외가 발생했습니다 잠시 후 다시 시도해 주세요.");
    }

    private void jsonProcessingExceptionLog(JsonProcessingException e) {
        log.error("JsonProcessingException 발생: {}", e.getMessage());
    }
}
