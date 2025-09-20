/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.healthdata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing health data entities.
 *
 * <p>Provides query methods for filtering health data by intervals, cohorts, and parameter
 * identifiers, as well as pagination support.
 */
@Repository
public interface HealthDataRepository extends JpaRepository<HealthDataEntity, UUID> {

  /**
   * Finds health data records by their interval start and end dates.
   *
   * @param intervalStart Start date of the interval
   * @param intervalEnd End date of the interval
   * @return List of matching health data entities
   */
  List<HealthDataEntity> findByIntervalStartAndIntervalEnd(
      LocalDateTime intervalStart, LocalDateTime intervalEnd);

  /**
   * Finds health data records by interval start date.
   *
   * @param intervalStart Start date of the interval
   * @return List of matching health data entities
   */
  List<HealthDataEntity> findByIntervalStart(LocalDateTime intervalStart);

  /**
   * Finds health data records by interval end date.
   *
   * @param intervalEnd End date of the interval
   * @return List of matching health data entities
   */
  List<HealthDataEntity> findByIntervalEnd(LocalDate intervalEnd);

  /**
   * Finds paginated health data records for a given cohort.
   *
   * @param cohort Cohort identifier
   * @param pageable Pageable request for pagination and sorting
   * @return Page of matching health data entities
   */
  Page<HealthDataEntity> findByCohort(UUID cohort, Pageable pageable);

  /**
   * Finds paginated health data records for a given cohort and parameter ID.
   *
   * @param cohort Cohort identifier
   * @param pageable Pageable request for pagination and sorting
   * @param parameterId Identifier of the parameter configuration
   * @return Page of matching health data entities
   */
  Page<HealthDataEntity> findByCohortAndParameterId(
      UUID cohort, Pageable pageable, UUID parameterId);

  /**
   * Counts the number of health data records in a cohort.
   *
   * @param cohort Cohort identifier
   * @return Number of records in the cohort
   */
  long countByCohort(UUID cohort);
}
