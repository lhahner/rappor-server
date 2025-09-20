package org.wearables.randomizeresponse.differentialprivacy.decoder;

import org.wearables.randomizedresponse.healthdata.HealthDataEntity;

/**
 * Minimal test double. If your HealthDataEntity already has suitable constructors/setters, you can
 * delete this file and instantiate that directly in the benchmark.
 */
public class StubHealthDataEntity extends HealthDataEntity {

  private String prr;
  private int stepCount;

  @Override
  public void setPrr(String prr) {
    this.prr = prr;
  }

  @Override
  public void setStepCount(int stepCount) {
    this.stepCount = stepCount;
  }

  @Override
  public String getPrr() {
    return prr;
  }

  @Override
  public int getStepCount() {
    return stepCount;
  }
}
