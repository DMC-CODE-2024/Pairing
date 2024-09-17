package com.eirs.pairs.dto;


public class ResponseDtoUtil {

    public static final String success = "Success";

    public static final String failure = "Failure";

    public static ResponseDto getSuccessResponseDto(Object result) {
        return ResponseDto.builder()
                .errorInfo(ErrorInfo.builder().errorCode(0).errorLevel(ErrorEnum.OK).errorMessage(success).build())
                .status(success)
                .result(result)
                .build();
    }

    public static ResponseDto getSuccessResponseDto(Object result, String successSms) {
        return ResponseDto.builder()
                .errorInfo(ErrorInfo.builder().errorCode(0).errorLevel(ErrorEnum.OK).errorMessage(success).build())
                .status(successSms)
                .result(result)
                .build();
    }

    public static ResponseDto getSuccessResponseDto(String successMessage) {
        return ResponseDto.builder()
                .errorInfo(ErrorInfo.builder().errorCode(0).errorLevel(ErrorEnum.OK).errorMessage(successMessage).build())
                .status(success)
                .build();
    }

    public static ResponseDto getErrorResponseDto(String errorMessage) {
        return ResponseDto.builder()
                .errorInfo(ErrorInfo.builder().errorCode(3).errorLevel(ErrorEnum.ERROR).errorMessage(errorMessage).build())
                .status(failure)
                .build();
    }

    public static ResponseDto getErrorResponseDto(ErrorInfo errorInfo) {
        return ResponseDto.builder()
                .errorInfo(errorInfo)
                .status(failure)
                .build();
    }

}
