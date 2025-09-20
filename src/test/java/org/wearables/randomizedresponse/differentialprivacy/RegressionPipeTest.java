package org.wearables.randomizedresponse.differentialprivacy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.wearables.randomizedresponse.differentialprivacy.decoder.RegressionPipe;
import org.wearables.randomizedresponse.healthdata.HealthDataEntity;
import smile.data.DataFrame;

@ExtendWith(MockitoExtension.class)
@Import(RegressionPipe.class)
class RegressionPipeTest {

  @InjectMocks RegressionPipe<HealthDataEntity> regressionPipe;

  @Test
  void predictMostLikeliestClasses() {
    double[][] designMatrix = {
      {0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0},
      {1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1},
      {0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0}
    };
    double[] expectedTrueCounts = {
      -24, 32, 32, -24, -24, 35, 35, -24, -24, 35, 35, -24, -24, -24, 35, -24
    };
    double[] lambdas = {0.01, 0.05, 0.1, 0.2, 0.4};
    assertDoesNotThrow(
        () ->
            regressionPipe.predictMostLikeliestClasses(
                designMatrix, 16, expectedTrueCounts, lambdas));
  }

  @Test
  void getCoefficientBasedMatrix() {
    double[][] designMatrix = {
      {0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0},
      {1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1},
      {0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0}
    };
    double[] coefficients = {-24, 32, 32};
    double[][] coefficientBasedMatrix =
        regressionPipe.getCoefficientBasedMatrix(designMatrix, coefficients);
    assertEquals(2, coefficientBasedMatrix.length);
  }

  @Test
  void getProbabilityForClass() {
    double[] coefficients = new double[] {42};
    int numberOfReports = 10;
    double[] res = {4.2};
    assertArrayEquals(res, regressionPipe.getProbabilityForClass(coefficients, numberOfReports));
  }

  @Test
  void makeFrame_basicUsageValidation() {
    double[][] designMatrix = {
      {0, 1, 0, 0},
      {1, 0, 1, 0},
      {0, 0, 0, 1}
    };
    double[] expectedTrueCounts = {-24, 32, 32, -24};
    DataFrame dataFrame =
        DataFrame.of(
            new double[][] {
              {0, 1, 0, -24},
              {1, 0, 0, 32},
              {0, 1, 0, 32},
              {0, 0, 1, -24}
            },
            new String[] {"b0", "b1", "b2", "y"});
    assertSame(
        dataFrame.get(0).get("b0").toString(),
        regressionPipe.makeFrame(designMatrix, expectedTrueCounts).get(0).get("b0").toString());
  }

  @Test
  void makeFrame_MoreRowsThanColumns() {
    double[][] designMatrix = {
      {0, 1, 0, 0, 1},
      {1, 0, 1, 0, 0},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
    };
    double[] expectedTrueCounts = {-24, 32, 32, -24, 32};
    assertDoesNotThrow(() -> regressionPipe.makeFrame(designMatrix, expectedTrueCounts));
  }

  @Test
  void makeFrame_MoreColumnsThanRows() {
    double[][] designMatrix = {
      {0, 1, 0, 0, 1},
      {1, 0, 1, 0, 0},
    };
    double[] expectedTrueCounts = {-24, 32, 32, -24, 32};
    assertDoesNotThrow(() -> regressionPipe.makeFrame(designMatrix, expectedTrueCounts));
  }

  @Test
  void predictFinalClassCounts_overDeterminedMatrix() {
    double[][] designMatrix = {
      {0, 1, 0, 0, 1},
      {1, 0, 1, 0, 0},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
      {0, 0, 0, 1, 1},
    };
    double[] expectedTrueCounts = {-24, 32, 32, -24, 32, -2, 3};
    double[] coefficients = {0, 1, -1, 0, 1, 2, -2};
    assertDoesNotThrow(
        () ->
            regressionPipe.predictFinalClassCounts(designMatrix, expectedTrueCounts, coefficients));
  }

  @Test
  void predictFinalClassCounts_lessRowsThanColumns() {
    double[][] designMatrix = {
      {0, 1, 0, 0, 1, 1},
      {1, 0, 1, 1, 0, 0},
    };
    double[] expectedTrueCounts = {-24, 32};
    double[] coefficients = {0, 1, -1, 0, 1, 2, -2};
    assertDoesNotThrow(
        () ->
            regressionPipe.predictFinalClassCounts(designMatrix, expectedTrueCounts, coefficients));
  }
}
