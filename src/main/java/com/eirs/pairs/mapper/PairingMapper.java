package com.eirs.pairs.mapper;

import com.eirs.pairs.dto.PairingDto;
import com.eirs.pairs.repository.entity.ImeiPairDetailHis;
import com.eirs.pairs.repository.entity.Pairing;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PairingMapper {

    PairingDto toPairingDto(Pairing pairing);

    List<PairingDto> toPairingDtos(List<Pairing> pairings);

    ImeiPairDetailHis toImeiPairDetailHis(Pairing pairing);

}
