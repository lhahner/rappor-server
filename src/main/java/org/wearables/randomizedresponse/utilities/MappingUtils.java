/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MappingUtils {
  public final ObjectMapper objectMapper;

  @Autowired
  public MappingUtils(ObjectMapper objectMapper) { // injected same bean Spring MVC uses
    this.objectMapper = objectMapper;
  }
}
