package com.eirs.pairs.exception;

import com.eirs.pairs.dto.ResponseDto;
import com.eirs.pairs.dto.ResponseDtoUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDto> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        return new ResponseEntity<>(ResponseDtoUtil.getErrorResponseDto(ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ResponseDto> handleBadRequestException(
            InternalServerException ex, WebRequest request) {
        return new ResponseEntity<>(ResponseDtoUtil.getErrorResponseDto(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto> handleBadRequestException(
            ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(ResponseDtoUtil.getErrorResponseDto(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BackEndIPCException.class)
    public ResponseEntity<ResponseDto> handleBackEndIPCException(
            BackEndIPCException ex, WebRequest request) {
        return new ResponseEntity<>(ResponseDtoUtil.getErrorResponseDto(ex.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
