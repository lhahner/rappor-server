package org.wearables.randomizedresponse.differentialprivacy;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.wearables.randomizedresponse.TestUtil;
import org.wearables.randomizedresponse.differentialprivacy.decoder.AggregationPipe;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterService;
import org.wearables.randomizedresponse.healthdata.HealthDataEntity;

@ExtendWith(MockitoExtension.class)
@Import(AggregationPipe.class)
class AggregationPipeTest {

  @InjectMocks private AggregationPipe<HealthDataEntity> aggregationPipe;

  @InjectMocks private ParameterService parameterService;

  private TestUtil testUtil;

  @BeforeEach
  void setup() {
    testUtil = new TestUtil();
  }

  @Test
  void countNumberOfIndexInCohort() {
    List<HealthDataEntity> healthDataEntities = new ArrayList<HealthDataEntity>();
    for (int i = 0; i < 10; i++) {
      healthDataEntities.add(testUtil.mockHealthDataEntity());
    }
    int[] expectedCounts = {0, 10, 0, 10, 0, 10, 0, 0, 0, 0, 10, 0, 0, 10, 10, 10};
    assertEquals(
        expectedCounts.length,
        aggregationPipe.countNumberOfIndexInCohort(healthDataEntities, 16).length);
    assertArrayEquals(
        expectedCounts, aggregationPipe.countNumberOfIndexInCohort(healthDataEntities, 16));
  }

  @Test
  void estimateExpectedTrueCounts() {
    int[] expectedCounts = {0, 10, 0, 10, 0, 10, 0, 0, 0, 0, 10, 0, 0, 10, 10, 10};
    int numberOfReports = 10;
    double[] results =
        aggregationPipe.estimateExpectedTrueCounts(
            expectedCounts, numberOfReports, testUtil.mockParameterEntity());
    assertEquals(expectedCounts.length, results.length);
  }
}
