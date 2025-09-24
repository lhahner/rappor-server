package org.wearables.randomizedresponse.differentialprivacy.parameter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParameterMapper {
  @Mapping(source = "parameterId", target = "parameterId")
  @Mapping(source = "profileName", target = "profile")
  @Mapping(source = "kValue", target = "messageBitSize")
  @Mapping(source = "hValue", target = "numberOfHashFunctions")
  @Mapping(source = "fValue", target = "permanentProbability")
  @Mapping(source = "pValue", target = "instantaneousProbabilityForZero")
  @Mapping(source = "qValue", target = "instantaneousProbabilityForOne")
  public ParameterEntity convert(ParameterData parameterData);
}
