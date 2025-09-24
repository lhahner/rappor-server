package org.wearables.randomizedresponse.healthdata;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.wearables.randomizedresponse.differentialprivacy.Report;

/**
 * Service layer for managing HealthDataEntity objects.
 *
 * <p>Provides CRUD operations, query methods, and mapping utilities for health data records stored
 * in the database via the repository.
 */
@Service
@Validated
public class HealthDataService {

  /** Repository for persisting and retrieving health data entities. */
  private final HealthDataRepository healthDataRepository;

  @Autowired
  public HealthDataService(HealthDataRepository healthDataRepository) {
    this.healthDataRepository = healthDataRepository;
  }

  /**
   * Saves a new health data entity.
   *
   * @param healthDataEntity Entity to persist
   * @return Saved entity
   */
  public HealthDataEntity saveHealthData(@NotNull HealthDataEntity healthDataEntity) {
    return healthDataRepository.save(healthDataEntity);
  }

  /**
   * Finds a health data entity by its ID.
   *
   * @param id Identifier of the health data record
   * @return Matching health data entity
   * @throws NoSuchElementException if no entity is found
   */
  @Cacheable("healthdata")
  public HealthDataEntity findHealthDataById(@NotNull UUID id) {
    return healthDataRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("HealthData with id " + id + " not found"));
  }

  /**
   * Retrieves a chunk of health data entities with a given limit.
   *
   * @param chunkLimit Maximum number of entities to return
   * @return List of health data entities
   */
  public List<HealthDataEntity> fetchHealthDataListChunk(@Positive int chunkLimit) {
    PageRequest pageRequest = PageRequest.of(0, chunkLimit);
    return healthDataRepository.findAll(pageRequest).getContent();
  }

  /**
   * Updates an existing health data entity.
   *
   * @param healthDataEntity Entity with updated values
   * @return Updated and persisted entity
   * @throws IllegalArgumentException if the entity is null or does not exist
   */
  public HealthDataEntity updateHealthData(@NotNull HealthDataEntity healthDataEntity) {
    Optional<HealthDataEntity> healthDataEntityDb =
        healthDataRepository.findById(healthDataEntity.getDeviceId());

    if (healthDataEntityDb.isPresent()) {
      HealthDataEntity tmpHealthDataEntityDb = healthDataEntityDb.get();
      return healthDataRepository.save(tmpHealthDataEntityDb);
    } else {
      throw new IllegalArgumentException("Provided healthDataEntity does not exist.");
    }
  }

  /**
   * Deletes a health data entity.
   *
   * @param healthDataEntity Entity to delete
   * @throws NoSuchElementException if the entity does not exist
   */
  public void deleteHealthData(@NotNull HealthDataEntity healthDataEntity) {
    if (healthDataRepository.existsById(healthDataEntity.getDeviceId())) {
      healthDataRepository.delete(healthDataEntity);
      return;
    }
    throw new NoSuchElementException("The HealthData Entity is not present in Database");
  }

  /**
   * Maps a report into a list of health data entities.
   *
   * @param report Report containing health data values
   * @return List of mapped entities
   */
  public List<HealthDataEntity> mapToHealthDataEntities(@NotNull Report<HealthData> report) {
    List<HealthDataEntity> healthDataEntities = new ArrayList<>();
    for (HealthData healthData : report.values()) {
      healthDataEntities.add(
          new HealthDataEntity(
              healthData.reportId(),
              report.deviceId(),
              report.cohortId(),
              healthData.intervalStart().toLocalDateTime(),
              healthData.intervalEnd().toLocalDateTime(),
              healthData.steps(),
              healthData.prr().substring(2),
              healthData.irr().substring(2),
              report.parameterId()));
    }
    return healthDataEntities;
  }

  /**
   * Finds entities by interval start date.
   *
   * @param start Interval start date
   * @return List of entities matching the date
   */
  public List<HealthDataEntity> findByIntervalStartDates(@NotNull LocalDateTime start) {
    return healthDataRepository.findByIntervalStart(start);
  }

  /**
   * Finds entities by interval end date.
   *
   * @param end Interval end date
   * @return List of entities matching the date
   */
  public List<HealthDataEntity> findByIntervalEndDates(@NotNull LocalDate end) {
    return healthDataRepository.findByIntervalEnd(end);
  }

  /**
   * Finds entities by interval start and end dates.
   *
   * @param start Interval start date
   * @param end Interval end date
   * @return List of entities matching the range
   */
  public List<HealthDataEntity> findByIntervalStartAndIntervalEndDates(
      @NotNull LocalDateTime start, @NotNull LocalDateTime end) {
    return healthDataRepository.findByIntervalStartAndIntervalEnd(start, end);
  }

  /**
   * Saves a list of health data entities in a single transaction.
   *
   * @param healthDataEntities List of entities to save
   * @throws IllegalArgumentException if the list is null
   */
  @Transactional
  public void saveAll(@NotNull List<HealthDataEntity> healthDataEntities) {
    healthDataRepository.saveAll(healthDataEntities);
  }

  /**
   * Retrieves a page of health data entities for a given cohort.
   *
   * @param cohort Cohort identifier
   * @param pageRequestMin Minimum page index
   * @param pageRequestMax Maximum page size
   * @return List of entities from the requested page
   * @throws NoSuchElementException if no entities are found
   */
  @Cacheable("healthdataEntities")
  public List<HealthDataEntity> getPageOfCohorts(
      @NotNull UUID cohort, @PositiveOrZero int pageRequestMin, @Positive int pageRequestMax) {
    PageRequest pageRequest = PageRequest.of(pageRequestMin, pageRequestMax);
    Page<HealthDataEntity> healthData = healthDataRepository.findByCohort(cohort, pageRequest);
    if (healthData.getTotalElements() == 0) {
      throw new NoSuchElementException("No HealthDataEntity found");
    }
    return healthData.getContent();
  }

  /**
   * Retrieves a page of health data entities for a given cohort and parameter ID.
   *
   * @param cohort Cohort identifier
   * @param pageRequestMin Minimum page index
   * @param pageRequestMax Maximum page size
   * @param parameterId Identifier of the parameter configuration
   * @return List of entities from the requested page
   * @throws NoSuchElementException if no entities are found
   */
  public List<HealthDataEntity> getPageOfCohortsAndParameterId(
      @NotNull UUID cohort,
      @NotNull int pageRequestMin,
      @Positive int pageRequestMax,
      @NotNull UUID parameterId) {
    PageRequest pageRequest = PageRequest.of(pageRequestMin, pageRequestMax);
    Page<HealthDataEntity> healthData =
        healthDataRepository.findByCohortAndParameterId(cohort, pageRequest, parameterId);
    if (healthData.getTotalElements() == 0) {
      throw new NoSuchElementException("No HealthDataEntity found");
    }
    return healthData.getContent();
  }

  /**
   * Counts the number of health data reports for a cohort.
   *
   * @param cohort Cohort identifier
   * @return Number of reports in the cohort
   */
  public int countNumberReportsOfCohort(UUID cohort) {
    return (int) healthDataRepository.countByCohort(cohort);
  }
}
