/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import java.util.UUID;

/**
 * Record representing a client report submitted in the randomized response system. Each report is
 * tied to a parameter configuration, a device identifier, and a cohort. It contains a list of
 * values representing the collected data in randomized format.
 *
 * @param <T> Type of the values contained in the report
 * @param parameterId Identifier of the parameter configuration used for encoding
 * @param deviceId Identifier of the device that submitted the report
 * @param cohortId Identifier of the cohort to which the report belongs
 * @param values List of values reported by the client
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Report<T>(
    @JsonProperty("parameter_id") UUID parameterId,
    @JsonProperty("device_id") UUID deviceId,
    @JsonProperty("cohort_id") UUID cohortId,
    List<T> values) {}
