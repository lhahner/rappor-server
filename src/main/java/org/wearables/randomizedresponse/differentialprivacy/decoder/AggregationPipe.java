/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.decoder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.wearables.randomizedresponse.differentialprivacy.ReportEntity;
import org.wearables.randomizedresponse.differentialprivacy.decoder.substance.Substance;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterEntity;

/**
 * Pipeline step that aggregates the noisy strings received from clients and produces an estimated
 * target vector. The resulting vector is later used as the target for the regression model.
 *
 * @param <T> The entity type to be processed
 */
@Service
@Validated
public class AggregationPipe<T extends ReportEntity> implements Pipe<T> {
  /**
   * Executes the aggregation step in the pipeline. This method calculates bit counts for the
   * provided entities, estimates expected true counts using differential privacy parameters, and
   * updates the substance object with these values before returning it.
   *
   * @param substance The container holding entities and related data for processing
   * @return The updated substance containing bit counts and expected true counts
   */
  public Substance<T> process(@NotNull Substance<T> substance) throws ExecutionException {
    int[] bitCounts =
        countNumberOfIndexInCohort(substance.getEntities(), substance.getMessageBitSize());
    double[] expectedTrueCounts =
        estimateExpectedTrueCounts(
            bitCounts, substance.getEntities().size(), substance.getParameterEntity());
    substance.setBitCounts(bitCounts);
    substance.setExpectedTrueCounts(expectedTrueCounts);
    return substance;
  }

  /**
   * Counts the number of times each bit position is set to 1 across all reports.
   *
   * @param healthDataEntities The list of reports containing randomized bit strings
   * @param messageBitSize The length of the bit string representation
   * @return An integer array where each element is the count of 1s for the corresponding bit index
   */
  public int[] countNumberOfIndexInCohort(
      @NotNull List<T> healthDataEntities, @Positive int messageBitSize) {
    int[] bitCounts = new int[messageBitSize];
    for (T healthDataEntity : healthDataEntities) {
      String binary = healthDataEntity.getPrr();
      for (int i = 0; i < binary.length(); i++) {
        if (binary.charAt(i) == '1') {
          bitCounts[i]++;
        }
      }
    }
    return bitCounts;
  }

  /**
   * Estimates the expected true counts for each bit index given the observed counts and the cohort
   * size. The estimation is based on randomized response parameters retrieved from the parameter
   * service.
   *
   * @param bitCounts Observed counts of bits set to 1 across the cohort
   * @param numberReportsInCohort Total number of reports in the cohort
   * @param parameterEntity The parameter entity providing probabilities for randomized response
   * @return A double array containing the estimated true counts for each bit index
   */
  public double[] estimateExpectedTrueCounts(
      @NotNull int[] bitCounts,
      @Positive int numberReportsInCohort,
      @NotNull ParameterEntity parameterEntity) {
    double[] expectedTrueCounts = new double[bitCounts.length];
    for (int i = 0; i < expectedTrueCounts.length; i++) {
      expectedTrueCounts[i] =
          estimate(
              bitCounts[i],
              parameterEntity.getInstantaneousProbabilityForOne(),
              parameterEntity.getPermanentProbability(),
              parameterEntity.getInstantaneousProbabilityForZero(),
              numberReportsInCohort);
    }
    return expectedTrueCounts;
  }

  /**
   * Computes the estimated true count for a single bit index. The estimation uses the observed
   * count, randomized response probabilities, and the size of the cohort.
   *
   * @param c Observed count of bits set to 1 at this index
   * @param p Instantaneous probability for reporting a 1
   * @param f Permanent probability parameter
   * @param q Instantaneous probability for reporting a 0
   * @param n Number of reports in the cohort
   * @return Estimated true count for this bit index
   */
  private double estimate(int c, double p, double f, double q, int n) {
    double offset = p + 0.5 * f * (q - p);
    double denominate = (1.0 - f) * (q - p);
    return (c - offset * n) / denominate;
  }
}
