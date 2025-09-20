package org.wearables.randomizedresponse.differentialprivacy;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.wearables.randomizedresponse.differentialprivacy.decoder.DebiasPipe;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterService;
import org.wearables.randomizedresponse.healthdata.HealthDataEntity;

@ExtendWith(MockitoExtension.class)
@Import(DebiasPipe.class)
class DebiasPipeTest {

  @InjectMocks private DebiasPipe<HealthDataEntity> debiasPipe;

  @InjectMocks private ParameterService parameterService;

  @Test
  void mapCandidateStringsToIndex() {
    int[] indexes = debiasPipe.mapCandidateStringsToIndex(0, 100, 16, null);
    int[] diffIndexes = debiasPipe.mapCandidateStringsToIndex(100, 200, 16, null);
    assertEquals(2, indexes.length);
    assertFalse(Arrays.equals(indexes, diffIndexes));
    assertDoesNotThrow(() -> debiasPipe.mapCandidateStringsToIndex(40000, 50000, 16, null));
  }

  @Test
  void buildBinMap() {
    Map<String, int[]> binMap = debiasPipe.buildBinMap(1000, 100);
    assertDoesNotThrow(() -> debiasPipe.buildBinMap(1000, 10));
    assertEquals(10, binMap.size());
  }

  @Test
  void buildDesignMatrix() {
    Map<String, int[]> binMap =
        Map.of(
            "0-100", new int[] {2, 1},
            "100-200", new int[] {2, 3},
            "200-300", new int[] {4, 5});
    double[][] expectedResult = {
      {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    assertDoesNotThrow(() -> debiasPipe.convertClassesToArray(binMap, 16, 100));
    assertArrayEquals(expectedResult, debiasPipe.convertClassesToArray(binMap, 16, 100));
  }
}
