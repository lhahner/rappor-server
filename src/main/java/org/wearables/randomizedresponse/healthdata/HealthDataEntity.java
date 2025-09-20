/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.healthdata;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.UUID;
import org.wearables.randomizedresponse.differentialprivacy.ReportEntity;

/**
 * Entity representing health data submitted by a client.
 *
 * <p>Extends ReportEntity by adding health-specific attributes such as the reporting interval and
 * the recorded step count.
 */
@Entity
@Table(name = "Health_data")
public class HealthDataEntity extends ReportEntity {

  /** Start timestamp of the reporting interval. */
  @Column(name = "Interval_start")
  private LocalDateTime intervalStart;

  /** End timestamp of the reporting interval. */
  @Column(name = "Interval_end")
  private LocalDateTime intervalEnd;

  /** Number of steps recorded during the reporting interval. */
  @Column(name = "Step_count")
  private int stepCount;

  public HealthDataEntity() {}

  public HealthDataEntity(
      UUID reportId,
      UUID deviceId,
      UUID cohort,
      LocalDateTime intervalStart,
      LocalDateTime intervalEnd,
      int stepCount,
      String prr,
      String irr,
      UUID parameterId) {
    super.reportId = reportId;
    super.deviceId = deviceId;
    super.cohort = cohort;
    this.intervalStart = intervalStart;
    this.intervalEnd = intervalEnd;
    this.stepCount = stepCount;
    this.prr = prr;
    this.irr = irr;
    super.parameterId = parameterId;
  }

  @Override
  public void setCohort(UUID cohort) {
    this.cohort = cohort;
  }

  public LocalDateTime getIntervalStart() {
    return intervalStart;
  }

  public void setIntervalStart(LocalDateTime intervalStart) {
    this.intervalStart = intervalStart;
  }

  public LocalDateTime getIntervalEnd() {
    return intervalEnd;
  }

  public void setIntervalEnd(LocalDateTime intervalEnd) {
    this.intervalEnd = intervalEnd;
  }

  public int getStepCount() {
    return stepCount;
  }

  public void setStepCount(int stepCount) {
    this.stepCount = stepCount;
  }

  public UUID getParameterId() {
    return parameterId;
  }

  public void setParameterId(UUID parameterId) {
    this.parameterId = parameterId;
  }
}
