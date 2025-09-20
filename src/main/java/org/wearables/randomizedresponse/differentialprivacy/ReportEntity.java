/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Base entity class representing a persisted client report in the randomized response system.
 * Serves as a mapped superclass for entities that extend report functionality, providing common
 * fields such as identifiers and randomized bit strings.
 */
@MappedSuperclass
public class ReportEntity {
  /** Unique identifier of the report. */
  @Id
  @Column(name = "Report_Id")
  protected UUID reportId;

  /** Identifier of the device that submitted the report. */
  @Column(name = "Device_id")
  protected UUID deviceId;

  /** Identifier of the cohort associated with the report. */
  @Column(name = "Cohort")
  protected UUID cohort;

  /** Permanent randomized response bit string. */
  @Column(name = "Prr")
  protected String prr;

  /** Instantaneous randomized response bit string. */
  @Column(name = "Irr")
  protected String irr;

  /** Identifier of the parameter configuration used for encoding. */
  @Column(name = "Parameter_id")
  protected UUID parameterId;

  public UUID getReportId() {
    return reportId;
  }

  public void setReportId(UUID repotId) {
    this.reportId = repotId;
  }

  public String getIrr() {
    return irr;
  }

  public void setIrr(String irr) {
    this.irr = irr;
  }

  public String getPrr() {
    return prr;
  }

  public void setPrr(String prr) {
    this.prr = prr;
  }

  public UUID getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(UUID deviceId) {
    this.deviceId = deviceId;
  }

  public UUID getCohort() {
    return cohort;
  }

  public void setCohort(UUID cohort) {
    this.cohort = cohort;
  }
}
