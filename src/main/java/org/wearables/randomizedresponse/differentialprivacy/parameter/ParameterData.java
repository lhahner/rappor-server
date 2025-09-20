/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Record representing the parameter configuration used for differential privacy. Each instance
 * defines the probabilities and values required for randomized response and decoding, identified by
 * a unique parameter profile.
 *
 * @param parameterId Unique identifier of the parameter set
 * @param profileName Human-readable name of the parameter profile
 * @param pValue Instantaneous probability of reporting a 1
 * @param qValue Instantaneous probability of reporting a 0
 * @param fValue Permanent probability factor
 * @param hValue Number of hash functions used in Bloom filter encoding
 * @param kValue Bloom filter size parameter
 */
public record ParameterData(
    @JsonProperty("parameter_id") UUID parameterId,
    @JsonProperty("profile_name") String profileName,
    @JsonProperty("p_value") double pValue,
    @JsonProperty("q_value") double qValue,
    @JsonProperty("f_value") double fValue,
    @JsonProperty("h_value") int hValue,
    @JsonProperty("k_value") int kValue) {}
