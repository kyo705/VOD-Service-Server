package com.ktube.vod.user;

import com.ktube.vod.error.ResponseErrorDto;
import com.ktube.vod.notification.NotificationFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = UserController.class)
public class UserExceptionHandler {

    // request body 데이터가 없을 때
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ResponseErrorDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

        log.info(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.value())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ResponseErrorDto> handleIllegalArgumentException(IllegalArgumentException e) {

        log.info(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.value())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler({IllegalAccessException.class})
    public ResponseEntity<ResponseErrorDto> handleIllegalAccessException(IllegalAccessException e) {

        log.info(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.value())
                        .errorMessage(e.getMessage())
                        .build());
    }

    // modelAttribute @valid 예외 처리
    @ExceptionHandler({BindException.class})
    public ResponseEntity<ResponseErrorDto> handleConstraintViolationException(BindException e) {

        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.info(errorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.value())
                        .errorMessage(errorMessage)
                        .build());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ResponseErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.info(errorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.value())
                        .errorMessage(errorMessage)
                        .build());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ResponseErrorDto> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {

        log.info(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.value())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ResponseErrorDto> handleConversionFailedException(ConversionFailedException e) {

        log.info(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.value())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler({NotificationFailureException.class})
    public ResponseEntity<ResponseErrorDto> handleNotificationFailureException(NotificationFailureException e) {

        log.info(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ResponseErrorDto> handleDataIntegrityViolationException(DataIntegrityViolationException e) {

        log.info(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.CONFLICT.value())
                        .errorMessage(e.getMessage())
                        .build());
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResponseErrorDto> handleAnyException(Exception e) {

        System.out.println(e.getClass());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .errorMessage(e.getMessage())
                        .build());
    }
}
