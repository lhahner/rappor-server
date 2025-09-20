package org.wearables.randomizedresponse.differentialprivacy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterService;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ParameterServiceTest {

  @Autowired private ParameterService parameterService;

  @Test
  void getDefaultParameterEntity_allValuesAreDefault() {
    assertDoesNotThrow(() -> parameterService.getDefaultParameterEntity());
  }
}
