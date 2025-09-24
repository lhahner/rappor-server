package org.wearables.randomizedresponse.differentialprivacy.decoder.substance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wearables.randomizedresponse.differentialprivacy.hyperparameter.HyperParameterConfiguration;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterEntity;
import org.wearables.randomizedresponse.healthdata.HealthDataEntity;

@Mapper(componentModel = "spring")
public interface SubstanceMapper {

  @Mapping(source = "parameterEntity.messageBitSize", target = "messageBitSize")
  @Mapping(source = "parameterEntity", target = "parameterEntity")
  @Mapping(source = "hyperParameterConfiguration.lambdas", target = "lambdas")
  @Mapping(source = "hyperParameterConfiguration.rangeIterator", target = "rangeIterator")
  @Mapping(source = "hyperParameterConfiguration.startRange", target = "startRange")
  Substance<HealthDataEntity> convertMulti(
      ParameterEntity parameterEntity, HyperParameterConfiguration hyperParameterConfiguration);
}
