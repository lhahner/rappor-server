/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.parameter;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service for managing parameter configurations used in the randomized response and differential
 * privacy pipeline. Provides methods to retrieve default parameters, map data transfer objects into
 * entities, look up parameter profiles, and persist parameter entities.
 */
@Service
public class ParameterService {

  private ParameterRepository parameterRepository;

  @Autowired
  public ParameterService(ParameterRepository parameterRepository) {
    this.parameterRepository = parameterRepository;
  }

  public ParameterService() {}

  /** Default message a bit size (k). */
  private static final int MESSAGE_BIT_SIZE = 32;

  /** Default number of hash functions (h). */
  private static final int NUMBER_HASH_FUNCTIONS = 2;

  /** Default permanent probability (f). */
  private static final double PERMANENT_PROBABILITY = 0.5;

  /** Default instantaneous probability for reporting zero (p). */
  private static final double INSTANTANEOUS_PROBABILITY_FOR_ZERO = 0.5;

  /** Default instantaneous probability for reporting one (q). */
  private static final double INSTANTANEOUS_PROBABILITY_FOR_ONE = 0.75;

  /**
   * Returns a default parameter entity. This method can be used when no custom parameters are
   * required. It initializes the default values sent to the client for the RAPPOR algorithm. The
   * reason for this method is to save overhead and consistency even without an entry in the
   * parameters table.
   *
   * @return Default parameter entity
   */
  @Cacheable("parameters")
  public ParameterEntity getDefaultParameterEntity() {
    return new ParameterEntity(
        MESSAGE_BIT_SIZE,
        NUMBER_HASH_FUNCTIONS,
        PERMANENT_PROBABILITY,
        INSTANTANEOUS_PROBABILITY_FOR_ZERO,
        INSTANTANEOUS_PROBABILITY_FOR_ONE);
  }

  /**
   * Finds a parameter entity by its profile name. A profile is a user defined name for the number
   * of parameters used.
   *
   * @param profile The profile name to look up
   * @return Optional containing the parameter entity if found, otherwise empty
   */
  @CachePut("parameters")
  public Optional<ParameterEntity> findParameterEntityByProfile(@NotNull String profile) {
    return parameterRepository.findByProfile(profile);
  }

  /**
   * Saves a parameter entity.
   *
   * @param parameterEntity The parameter entity to persist
   */
  public void save(@NotNull ParameterEntity parameterEntity) {
    parameterRepository.save(parameterEntity);
  }
}
