package com.eirs.pairs.controller;

import com.eirs.pairs.constants.UrlConstants;
import com.eirs.pairs.dto.*;
import com.eirs.pairs.orchestrator.ManualPairingOrchestrator;
import com.eirs.pairs.service.PortalAccessAuditTrailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = UrlConstants.MANUAL_PAIR)
public class ManualPairingController {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private ManualPairingOrchestrator orchestrator;

    @Autowired
    PortalAccessAuditTrailService portalAccessAuditTrailService;

    private final String userAgentHeader = "User-Agent";

    @GetMapping()
    public ResponseEntity<ResponseDto> add() {
        return new ResponseEntity<ResponseDto>(ResponseDtoUtil.getSuccessResponseDto("Success"),
                HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ResponseDto> add(@RequestBody @Valid ManualPairRequestDto requestDto, HttpServletRequest request) {
        portalAccessAuditTrailService.captureAudit(request.getRemoteAddr(), request.getHeader(userAgentHeader), String.valueOf(System.currentTimeMillis()), "Pair");
        return new ResponseEntity<ResponseDto>(orchestrator.submitForManualPairing(requestDto),
                HttpStatus.CREATED);
    }

    @PostMapping(value = "/verify-otp")
    public ResponseEntity<ResponseDto> get(@RequestBody OtpVerifyRequestDto requestDto, HttpServletRequest request) {
        portalAccessAuditTrailService.captureAudit(request.getRemoteAddr(), request.getHeader(userAgentHeader), String.valueOf(System.currentTimeMillis()), "VerifyOtp");
        return new ResponseEntity<ResponseDto>(orchestrator.verifyOtp(requestDto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/status")
    public ResponseEntity<ResponseDto> statusRequest(@RequestBody @Valid PairStatusRequestDto pairStatusRequestDto, HttpServletRequest request) {
        portalAccessAuditTrailService.captureAudit(request.getRemoteAddr(), request.getHeader(userAgentHeader), String.valueOf(System.currentTimeMillis()), "Status");
        return new ResponseEntity<>(orchestrator.submitForPairStatus(pairStatusRequestDto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/repair")
    public ResponseEntity<ResponseDto> rePairRequest(@RequestBody @Valid RePairRequestDto rePairRequestDto, HttpServletRequest request) {
        portalAccessAuditTrailService.captureAudit(request.getRemoteAddr(), request.getHeader(userAgentHeader), String.valueOf(System.currentTimeMillis()), "Repair");
        return new ResponseEntity<>(orchestrator.submitForRePair(rePairRequestDto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/resend-otp")
    public ResponseEntity<ResponseDto> reSendOtp(@RequestBody @Valid ReSendOtpRequestDto reSendOtpRequestDto, HttpServletRequest request) {
        portalAccessAuditTrailService.captureAudit(request.getRemoteAddr(), request.getHeader(userAgentHeader), String.valueOf(System.currentTimeMillis()), "ResendOtp");
        return new ResponseEntity<>(orchestrator.reSendOtp(reSendOtpRequestDto), HttpStatus.OK);
    }
}
