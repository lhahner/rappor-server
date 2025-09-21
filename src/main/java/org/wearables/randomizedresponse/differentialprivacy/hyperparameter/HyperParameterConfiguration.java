package org.wearables.randomizedresponse.differentialprivacy.hyperparameter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "default")
public class HyperParameterConfiguration {

  @NotBlank private int rangeIterator;

  @Size(min = 5, max = 5)
  private double[] lambdas;

  @NotBlank private int startRange;

  public int getStartRange() {
    return startRange;
  }

  public void setStartRange(int startRange) {
    this.startRange = startRange;
  }

  public double[] getLambdas() {
    return lambdas;
  }

  public void setLambdas(double[] lambdas) {
    this.lambdas = lambdas;
  }

  public int getRangeIterator() {
    return rangeIterator;
  }

  public void setRangeIterator(int rangeIterator) {
    this.rangeIterator = rangeIterator;
  }
}
