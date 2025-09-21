/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.healthdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.wearables.randomizedresponse.differentialprivacy.Report;
import org.wearables.randomizedresponse.differentialprivacy.ReportEntity;
import org.wearables.randomizedresponse.differentialprivacy.decoder.DecoderService;
import org.wearables.randomizedresponse.differentialprivacy.decoder.substance.Substance;
import org.wearables.randomizedresponse.differentialprivacy.decoder.substance.SubstanceMapper;
import org.wearables.randomizedresponse.differentialprivacy.hyperparameter.HyperParameterConfiguration;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterEntity;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterService;
import org.wearables.randomizedresponse.utilities.MappingUtils;

/**
 * REST controller for managing health data from the smartwatch client. Exposes endpoints to upload
 * raw health data reports, retrieve persisted records, and decode a cohort's randomized responses
 * into probability distributions using differential privacy parameters.
 */
@RestController
@RequestMapping("/healthdata")
public class HealthDataController {

  /** Utility for mapping and JSON (de)serialization support. */
  private final MappingUtils mappingUtils;

  /** Service for persistence and retrieval of health data entities. */
  private final HealthDataService healthDataService;

  /** Decoder service that transforms randomized responses into probability estimates. */
  private final DecoderService<HealthDataEntity> decoderService;

  /** Service for retrieving differential privacy parameter profiles. */
  private final ParameterService parameterService;

  private final SubstanceMapper substanceMapper;
  private final HyperParameterConfiguration hyperParameterConfiguration;

  public HealthDataController(
      MappingUtils mappingUtils,
      DecoderService<HealthDataEntity> decoderService,
      ParameterService parameterService,
      HealthDataService healthDataService,
      SubstanceMapper substanceMapper,
      HyperParameterConfiguration hyperParameterConfiguration) {
    this.mappingUtils = mappingUtils;
    this.decoderService = decoderService;
    this.parameterService = parameterService;
    this.healthDataService = healthDataService;
    this.substanceMapper = substanceMapper;
    this.hyperParameterConfiguration = hyperParameterConfiguration;
  }

  /**
   * Uploads health data in JSON format. The request body is deserialized into a Report containing
   * HealthData objects, which are then mapped to entities and persisted. Returns 204 No Content if
   * the parsed report yields no entities.
   *
   * @param body JSON string containing the health data report
   * @return ResponseEntity with HTTP 200 OK if saved, HTTP 204 No Content if empty, or HTTP 400 Bad
   *     Request if the payload cannot be processed
   */
  @PostMapping(path = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> postHealthData(@RequestBody String body)
      throws JsonProcessingException {
    Report<HealthData> report =
        mappingUtils.objectMapper.readValue(body, new TypeReference<Report<HealthData>>() {});
    List<HealthDataEntity> entities = healthDataService.mapToHealthDataEntities(report);
    if (entities.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    healthDataService.saveAll(entities);
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves a health data record by its unique identifier.
   *
   * @param id UUID of the health data record
   * @return HealthDataEntity if found
   * @throws ResponseStatusException with HTTP 404 Not Found if no record exists for the given id
   */
  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public HealthDataEntity getHealthData(@PathVariable("id") UUID id) {
    HealthDataEntity healthDataEntity = healthDataService.findHealthDataById(id);
    if (healthDataEntity == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    return healthDataEntity;
  }

  /**
   * Retrieves health data records that match a given interval start date.
   *
   * @param intervalStart start date of the interval to match
   * @return ResponseEntity with the error message or the content retrieved.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> getHealthDataByIdAndIntervalStartAndIntervalEnd(
      @RequestParam LocalDateTime intervalStart) {
    List<HealthDataEntity> healthDataEntities =
        healthDataService.findByIntervalStartDates(intervalStart);
    if (healthDataEntities.isEmpty())
      return new ResponseEntity<>("No data found for that interval", HttpStatus.NOT_FOUND);
    return ResponseEntity.ok().body(healthDataEntities);
  }

  /**
   * Decodes randomized response health data for a cohort using a specified parameter profile.
   *
   * <p>This endpoint performs the following steps: 1) Resolves the parameter profile. 2) Fetches a
   * page of cohort entities for the associated parameter id. 3) Builds a Substance describing the
   * decoding task and runs the decoder pipeline. 4) Returns the resulting probability distribution
   * as a pretty-printed JSON string.
   *
   * <p>Returns 400 Bad Request if the parameter profile would cause division by zero or if no
   * matching health data exists for the request.
   *
   * @param cohort UUID of the cohort whose reports should be decoded
   * @param parameterProfile name of the parameter profile to use
   * @param numberOfReports maximum number of reports to include from the cohort
   * @return ResponseEntity with the probability distribution as a JSON string on success
   */
  @GetMapping(path = "/decode", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> getDecodedHealthData(
      @RequestParam UUID cohort,
      @RequestParam String parameterProfile,
      @RequestParam int numberOfReports)
      throws JsonProcessingException {
    ParameterEntity parameterEntity =
        parameterService
            .findParameterEntityByProfile(parameterProfile)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Parameter Entity not found"));
    if (parameterEntity.getInstantaneousProbabilityForOne()
            - parameterEntity.instantaneousProbabilityForZero
        == 0) {
      return new ResponseEntity<>(
          "The given Parameter Entity will cause division by zero.", HttpStatus.BAD_REQUEST);
    }
    List<HealthDataEntity> entities =
        healthDataService.getPageOfCohortsAndParameterId(
            cohort, 0, numberOfReports, parameterEntity.getParameterId());
    if (entities.isEmpty())
      return new ResponseEntity<>(
          "The Health data for your parameters is empty.", HttpStatus.BAD_REQUEST);
    decoderService.setSubstance(getHealthDataSubstance(entities, parameterEntity));
    Substance<HealthDataEntity> res = decoderService.runPipeline();
    return ResponseEntity.ok()
        .body(getJsonStringFromObject(decoderService.buildOutputRangeToProbabiltiesMap((res))));
  }

  /**
   * Serializes an object to a pretty-printed JSON string.
   *
   * @param object object to serialize
   * @return JSON string representation
   * @throws RuntimeException if JSON serialization fails
   */
  public String getJsonStringFromObject(@NotNull Object object) throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(object);
  }

  /**
   * Builds a Substance containing inputs and configuration for decoding health data.
   *
   * <p>The substance includes the entities to decode, the parameter entity, message bit size, start
   * and maximum ranges for histogram binning, the range iterator step, and the lambda
   * regularization values.
   *
   * @param entities list of health data entities to decode
   * @param parameterEntity differential privacy parameters to apply
* @return fully initialized Substance for the decoder service
   */
  public Substance<HealthDataEntity> getHealthDataSubstance(
          @NotNull List<HealthDataEntity> entities, @NotNull ParameterEntity parameterEntity) {
    Substance<HealthDataEntity> substance = substanceMapper.convertMulti(parameterEntity, hyperParameterConfiguration);
    substance.setEntities(entities);
    substance.setMaxRange(
        decoderService.calculateMaxRangeForStepCountBin(
            entities, hyperParameterConfiguration.getRangeIterator()));
    return substance;
  }
}
