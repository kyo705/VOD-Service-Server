package com.ktube.vod.identification;

import com.ktube.vod.error.ResponseErrorDto;
import lombok.extern.slf4j.Slf4j;
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
@RestControllerAdvice(assignableTypes = IdentificationController.class)
public class IdentificationExceptionHandler {

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

    // modelAttribute 에서 @valid 검증 실패시
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

    // request body 에서 @valid 검증 실패시
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

    //requestParam 에서 예외 발생시
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

    @ExceptionHandler({IdentificationFailureException.class})
    public ResponseEntity<ResponseErrorDto> handleIdentificationFailureException(IdentificationFailureException e) {

        log.info(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.value())
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

        System.out.println(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseErrorDto.builder()
                        .errorCode(HttpStatus.BAD_REQUEST.value())
                        .errorMessage(e.getMessage())
                        .build());
    }
}
