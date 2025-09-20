/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.parameter;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ParameterModeConverter implements Converter<String, ParameterMode> {
  @Override
  public ParameterMode convert(String source) {
    return EnumUtils.getEnum(ParameterMode.class, source.toUpperCase());
  }
}
