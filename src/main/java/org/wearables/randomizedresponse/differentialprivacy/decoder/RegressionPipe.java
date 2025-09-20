/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.decoder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.regression.LASSO;
import smile.regression.LinearModel;
import smile.regression.RidgeRegression;
import smile.validation.metric.MSE;

/**
 * Pipeline step that applies regression techniques to estimate class probabilities and counts from
 * aggregated noisy data. The process includes: - Using LASSO regression to identify the most likely
 * classes - Optionally fitting ordinary least squares for final class counts - Converting
 * regression coefficients into probability distributions
 *
 * @param <T> The entity type to be processed
 */
@Component
@Validated
public class RegressionPipe<T> implements Pipe<T> {
  /**
   * Runs the regression step of the pipeline.
   *
   * <p>Applies LASSO regression to estimate coefficients, then converts them into class
   * probabilities.
   *
   * @param substance The container with intermediate results from previous steps
   * @return The updated substance with regression coefficients and probabilities
   */
  @Override
  public Substance<T> process(Substance<T> substance) throws ExecutionException {
    double[] coefficients =
        predictMostLikeliestClasses(
            substance.getDesignMatrix(),
            substance.getMessageBitSize(),
            substance.getExpectedTrueCounts(),
            substance.getLambdas());
    substance.setCoefficients(coefficients);
    double[] probabilityForClasses =
        getProbabilityForClass(substance.getCoefficients(), substance.getEntities().size());
    substance.setProbabilities(probabilityForClasses);
    return substance;
  }

  /**
   * Uses LASSO regression to identify the most likely class coefficients. Evaluates multiple lambda
   * values and selects the one minimizing mean squared error.
   *
   * @param designMatrix The input matrix representing feature design
   * @param messageBitSize Size of the message bit vector used in encoding
   * @param expectedTrueCounts Expected true counts estimated from aggregation
   * @param lambdas Set of lambda values to evaluate for regularization
   * @return Coefficients of the best-performing LASSO model
   */
  public double[] predictMostLikeliestClasses(
      @NotNull double[][] designMatrix,
      @Positive int messageBitSize,
      @NotNull double[] expectedTrueCounts,
      @NotNull double[] lambdas) {
    DataFrame dataFrame = makeFrame(designMatrix, expectedTrueCounts);
    Formula formula = Formula.lhs("y");

    LinearModel best = null;

    double bestMSE = Double.POSITIVE_INFINITY;
    for (double lambda : lambdas) {
      var options = new LASSO.Options(lambda);
      LinearModel model = LASSO.fit(formula, dataFrame, options);
      double mse = MSE.of(expectedTrueCounts, model.fittedValues());
      if (mse < bestMSE) {
        bestMSE = mse;
        best = model;
      }
    }
    if (best == null) {
      throw new NullPointerException("The coefficients are null; regression failed");
    }
    return best.coefficients();
  }

  /**
   * Currently this function is not integrated yet. Fits an ordinary least squares regression model
   * and returns coefficients.
   *
   * @param designMatrix The design matrix used for regression
   * @param expectedResults Expected results used as targets
   * @param coefficients Coefficients from prior feature selection
   * @return Coefficients of the fitted OLS model
   */
  public double[] predictFinalClassCounts(
      @NotNull double[][] designMatrix,
      @NotNull double[] expectedResults,
      @NotNull double[] coefficients) {
    DataFrame finalDataFrame =
        makeFrameObservationsByRows(
            getCoefficientBasedMatrix(designMatrix, coefficients), expectedResults);
    LinearModel ordinaryLeastSquares = RidgeRegression.fit(Formula.lhs("y"), finalDataFrame, 1e-3);
    return ordinaryLeastSquares.coefficients();
  }

  /**
   * Filters the design matrix by selecting only rows with non-negative coefficients.
   *
   * @param designMatrix Original design matrix
   * @param coefficients Regression coefficients
   * @return Reduced matrix containing only selected rows
   */
  public double[][] getCoefficientBasedMatrix(
      @NotNull double[][] designMatrix, @NotNull double[] coefficients) {

    List<double[]> coefficientBasedMatrix = new ArrayList<>();
    for (int i = 0; i < designMatrix.length; i++) {
      if (coefficients[i] < 0) {
        continue;
      }
      coefficientBasedMatrix.add(designMatrix[i]);
    }
    double[][] tmpCoefficientBasedMatrix =
        new double[coefficientBasedMatrix.size()][designMatrix.length];
    return coefficientBasedMatrix.toArray(tmpCoefficientBasedMatrix);
  }

  /**
   * Converts regression coefficients into probability estimates for each class.
   *
   * @param coefficients Regression coefficients representing class counts
   * @param numberOfReports Total number of reports in the cohort
   * @return Probability distribution over classes
   */
  public double[] getProbabilityForClass(
      @NotNull double[] coefficients, @Positive int numberOfReports) {
    double[] probabilities = new double[coefficients.length];
    for (int i = 0; i < probabilities.length; i++) {
      probabilities[i] = coefficients[i] / numberOfReports;
    }
    return probabilities;
  }

  /**
   * Constructs a data frame from the design matrix and observed results.
   *
   * <p>Each row represents one observation, and the last column is the target "y".
   *
   * @param designMatrix The design matrix with rows as observations
   * @param y The observed results used as targets
   * @return Data frame suitable for regression fitting
   */
  public DataFrame makeFrameObservationsByRows(
      @NotNull double[][] designMatrix, @NotNull double[] y) {
    int nObs = designMatrix.length;
    int nFeat = designMatrix[0].length;
    double[][] table = new double[nObs][nFeat + 1];
    for (int i = 0; i < nObs; i++) {
      System.arraycopy(designMatrix[i], 0, table[i], 0, nFeat);
      table[i][nFeat] = y[i];
    }

    String[] names = new String[nFeat + 1];
    for (int j = 0; j < nFeat; j++) names[j] = "x" + j;
    names[nFeat] = "y";

    return DataFrame.of(table, names);
  }

  /**
   * Constructs a data frame from the design matrix and expected results.
   *
   * <p>The last column is assigned as the target variable labeled "y".
   *
   * @param designMatrix Feature design matrix
   * @param expectedResults Expected results as target values
   * @return Data frame containing features and the target column
   */
  public DataFrame makeFrame(@NotNull double[][] designMatrix, @NotNull double[] expectedResults) {
    double[][] x = transpose(designMatrix);
    int rows = x.length;
    int cols = x[0].length;
    if (expectedResults.length != rows) {
      throw new IllegalArgumentException(
          "expectedResults length ("
              + expectedResults.length
              + ") must equal x.length ("
              + rows
              + ")");
    }
    double[][] xy = new double[rows][cols + 1];
    for (int i = 0; i < rows; i++) {
      System.arraycopy(x[i], 0, xy[i], 0, cols);
      xy[i][cols] = expectedResults[i];
    }
    String[] names = new String[cols + 1];
    for (int j = 0; j < cols; j++) names[j] = "b" + j;
    names[cols] = "y";
    return DataFrame.of(xy, names);
  }

  /**
   * Transposes a two-dimensional matrix.
   *
   * @param m Input matrix
   * @return Transposed matrix
   */
  public static double[][] transpose(@NotNull double[][] m) {
    double[][] temp = new double[m[0].length][m.length];
    for (int i = 0; i < m.length; i++) {
      for (int j = 0; j < m[0].length; j++) {
        temp[j][i] = m[i][j];
      }
    }
    return temp;
  }
}
