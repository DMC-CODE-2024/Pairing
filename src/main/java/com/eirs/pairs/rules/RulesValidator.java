package com.eirs.pairs.rules;

import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;

public interface RulesValidator {
    Boolean validate(PairDto pairDto, ValidateOtpRequestDto validateOtpRequestDto);

}
