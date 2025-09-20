/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.healthdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Record representing the JSON format of health data and its mapping to the data types required for
 * database insertion.
 *
 * @param reportId A unique identifier for the health data report
 * @param intervalStart Start of the time interval covered by the report
 * @param intervalEnd End of the time interval covered by the report
 * @param steps Number of steps recorded during the interval
 * @param prr Noisy bloom filter from the client after one anonymization run
 * @param irr Noisy bloom filter from the client after one anonymization run
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HealthData(
    @JsonProperty("report_id") UUID reportId,
    @JsonProperty("interval_start") OffsetDateTime intervalStart,
    @JsonProperty("interval_end") OffsetDateTime intervalEnd,
    int steps,
    String prr,
    String irr) {}
