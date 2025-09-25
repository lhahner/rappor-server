/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.decoder.substance;

import java.util.List;
import java.util.Map;
import org.wearables.randomizedresponse.differentialprivacy.ReportEntity;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterEntity;

/**
 * Container object that passes through all steps of the decoder pipeline.
 *
 * <p>The substance holds every relevant value needed for the aggregation, debiasing, and regression
 * steps, ensuring that each stage of the pipeline remains decoupled from the others.
 *
 * @param <T> Domain-specific type representing the actual health data entity
 */
public class Substance<T extends ReportEntity> {

  /** List of entities being decoded. */
  private List<T> entities;

  /** Regression coefficients estimated during the regression step. */
  private double[] coefficients;

  /** Expected true counts estimated during aggregation. */
  private double[] expectedTrueCounts;

  /** Lambda values for regularization during regression. */
  private double[] lambdas;

  /** Final estimated class counts after regression. */
  private double[] finalClassCounts;

  /** Probability distribution over classes. */
  private double[] probabilities;

  /** Maximum range of values considered for binning. */
  private int maxRange;

  /** Identifier of the cohort being processed. */
  private int cohort;

  /** Step size used to divide the range into bins. */
  private int rangeIterator;

  /** Start of the value range for binning. */
  private int startRange;

  /** Size of the randomized message bit string. */
  private int messageBitSize;

  /** Design matrix used for regression analysis. */
  private double[][] designMatrix;

  /** Counts of bits set to 1 across all entities. */
  private int[] bitCounts;

  /** Bloom filter indexes mapped for candidate ranges. */
  private int[] indexes;

  /** Mapping of value ranges to Bloom filter indexes. */
  private Map<String, int[]> binMap;

  /** Differential privacy parameter entity associated with this decoding. */
  private ParameterEntity parameterEntity;

  public Substance() {}

  public List<T> getEntities() {
    return entities;
  }

  public void setEntities(List<T> entities) {
    this.entities = entities;
  }

  public double[] getCoefficients() {
    return coefficients;
  }

  public void setCoefficients(double[] coefficients) {
    this.coefficients = coefficients;
  }

  public double[] getExpectedTrueCounts() {
    return expectedTrueCounts;
  }

  public void setExpectedTrueCounts(double[] expectedTrueCounts) {
    this.expectedTrueCounts = expectedTrueCounts;
  }

  public double[] getLambdas() {
    return lambdas;
  }

  public void setLambdas(double[] lambdas) {
    this.lambdas = lambdas;
  }

  public int getMaxRange() {
    return maxRange;
  }

  public void setMaxRange(int maxRange) {
    this.maxRange = maxRange;
  }

  public int getCohort() {
    return cohort;
  }

  public void setCohort(int cohort) {
    this.cohort = cohort;
  }

  public int getRangeIterator() {
    return rangeIterator;
  }

  public void setRangeIterator(int rangeIterator) {
    this.rangeIterator = rangeIterator;
  }

  public int getStartRange() {
    return startRange;
  }

  public void setStartRange(int startRange) {
    this.startRange = startRange;
  }

  public int getMessageBitSize() {
    return messageBitSize;
  }

  public void setMessageBitSize(int messageBitSize) {
    this.messageBitSize = messageBitSize;
  }

  public double[][] getDesignMatrix() {
    return designMatrix;
  }

  public void setDesignMatrix(double[][] designMatrix) {
    this.designMatrix = designMatrix;
  }

  public int[] getBitCounts() {
    return bitCounts;
  }

  public int[] getIndexes() {
    return indexes;
  }

  public void setIndexes(int[] indexes) {
    this.indexes = indexes;
  }

  public void setBitCounts(int[] bitCounts) {
    this.bitCounts = bitCounts;
  }

  public Map<String, int[]> getBinMap() {
    return binMap;
  }

  public void setBinMap(Map<String, int[]> binMap) {
    this.binMap = binMap;
  }

  public double[] getFinalClassCounts() {
    return finalClassCounts;
  }

  public void setFinalClassCounts(double[] finalClassCounts) {
    this.finalClassCounts = finalClassCounts;
  }

  public double[] getProbabilities() {
    return probabilities;
  }

  public void setProbabilities(double[] probabilities) {
    this.probabilities = probabilities;
  }

  public ParameterEntity getParameterEntity() {
    return parameterEntity;
  }

  public void setParameterEntity(ParameterEntity parameterEntity) {
    this.parameterEntity = parameterEntity;
  }
}
