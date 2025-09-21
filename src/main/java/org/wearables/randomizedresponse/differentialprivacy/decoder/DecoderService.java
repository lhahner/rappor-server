/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.decoder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.*;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.wearables.randomizedresponse.differentialprivacy.decoder.substance.Substance;
import org.wearables.randomizedresponse.healthdata.HealthDataEntity;

/**
 * Service that coordinates the differential privacy decoding pipeline. The decoding pipeline has
 * three steps executed in sequence: 1. AggregationPipe: aggregates noisy client data into estimated
 * bit counts. 2. DebiasPipe: maps candidate ranges into Bloom filter indexes and removes bias. 3.
 * RegressionPipe: uses regression to estimate class counts and probabilities.
 *
 * @param <T> The entity type processed by the pipeline
 */
@Service
@Validated
public class DecoderService<T extends HealthDataEntity> {
  /** logger * */
  private final Logger logger = LoggerFactory.getLogger(DecoderService.class);

  /** Shared container object that passes through all pipeline steps. */
  private Substance<T> substance;

  /** The decoding pipeline consisting of aggregation, debiasing, and regression. */
  private final List<Pipe<T>> pipeline =
      List.of(new AggregationPipe<T>() {}, new DebiasPipe<T>(), new RegressionPipe<T>());

  /**
   * Runs the full decoding pipeline.
   *
   * <p>Each pipe processes the shared substance object in sequence, enriching it with intermediate
   * results until the final decoded outcome is produced.
   *
   * @return The final substance containing decoded results
   * @throws IllegalArgumentException if the message bit size does not match the length of the
   *     actual data
   */
  public Substance<T> runPipeline() {
    try {
      Substance<T> intermediate = new Substance<>();
      int prrLength = substance.getEntities().getFirst().getPrr().length();
      if (substance.getEntities().isEmpty() || substance.getMessageBitSize() != prrLength) {
        throw new IllegalArgumentException("The message bit size is not equal the actual data.");
      }
      for (Pipe<T> pipe : pipeline) {
        intermediate = pipe.process(substance);
      }
      return intermediate;
    } catch (ExecutionException e) {
      logger.trace("Execution of pipeline failed with Exception {e}", e);
    }
    return null;
  }

  /**
   * This is a utility method which help to visualize the probability for the given bin range.
   *
   * @param substance The substance DTO which is passed through the regression pipeline.
   * @return The coefficient TreeMap (ordered) where each key is a bin range.
   */
  public Map<String, Double> buildOutputRangeToProbabiltiesMap(Substance<T> substance) {
    Map<String, Double> coefficientMap = new TreeMap<>();
    int startRange = substance.getStartRange();
    for (double coefficient : substance.getProbabilities()) {
      int nextRange = startRange + substance.getRangeIterator();
      coefficientMap.put(startRange + "-" + nextRange, coefficient < 0 ? 0 : coefficient);
      startRange = nextRange;
    }
    return coefficientMap;
  }

  /**
   * Returns the current substance object.
   *
   * @return The current substance
   */
  public Substance<T> getSubstance() {
    return substance;
  }

  /**
   * Sets the current substance object.
   *
   * @param substance Substance to be set
   */
  public void setSubstance(Substance<T> substance) {
    this.substance = substance;
  }

  /**
   * Calculates the maximum range value for binning step counts.
   *
   * <p>The method finds the maximum step count among the entities and adjusts it upwards until it
   * is divisible by the given range iterator.
   *
   * <p>If the range iterator is greater than the maximum, the iterator value is returned.
   *
   * @param entities List of health data entities
   * @param rangeIterator Step size used for binning
   * @return Maximum range value adjusted to the binning step
   */
  public int calculateMaxRangeForStepCountBin(
      @NotNull List<T> entities, @Positive int rangeIterator) {

    int max =
        entities.stream()
            .max(Comparator.comparing(HealthDataEntity::getStepCount))
            .get()
            .getStepCount();
    if (rangeIterator > max) {
      return rangeIterator;
    }
    while (max % rangeIterator != 0) {
      max++;
    }
    return max;
  }
}
