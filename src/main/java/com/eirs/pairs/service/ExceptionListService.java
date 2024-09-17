package com.eirs.pairs.service;

import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.repository.entity.ExceptionList;
import com.eirs.pairs.repository.entity.ExceptionListHis;

import java.util.List;

public interface ExceptionListService {

    ExceptionList save(ExceptionList exceptionList);

    ExceptionListHis save(ExceptionListHis exceptionListHis);

    List<ExceptionList> getVIPImsi(String imsi);

    List<ExceptionList> getByImeiAndImsi(String imei, String imsi);

    List<ExceptionList> getByImeiAndImsiAndMsisdn(String imei, String imsi, String msisdn);

    void add(ValidateOtpRequestDto validateOtpRequestDto, String source);

    void add(PairDto pairDto, String txnId, String source);

    void add(RecordDataDto fileDataDto, String source);

    void delete(PairDto pairDto, String source, List<ExceptionList> exceptionLists);
}
