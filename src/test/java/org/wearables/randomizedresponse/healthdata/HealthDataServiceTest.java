package org.wearables.randomizedresponse.healthdata;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.wearables.randomizedresponse.TestUtil;
import org.wearables.randomizedresponse.differentialprivacy.Report;
import org.wearables.randomizedresponse.utilities.MappingUtils;

@DataJpaTest
@Import(HealthDataService.class)
class HealthDataServiceTest {

  @Autowired private EntityManager em;

  @Autowired private HealthDataService healthDataService;

  @Autowired private MappingUtils mappingUtils;

  private TestUtil testUtil;

  private static final String DATASET = "datasets/sample-dataset.json";

  @BeforeEach
  void setUp() {
    testUtil = new TestUtil();
  }

  @Test
  void findHealthDataById_isPersistentAndShouldBeReturned() {
    HealthDataEntity healthDataEntity = testUtil.mockHealthDataEntity();
    em.persist(healthDataEntity);
    em.flush();
    HealthDataEntity resHealthDataEntity =
        healthDataService.findHealthDataById(healthDataEntity.getDeviceId());
    assertEquals(healthDataEntity, resHealthDataEntity);
  }

  @Test
  void fetchHealthDataListChunk_isPersistentAndShouldBeReturned() {
    for (int i = 0; i < 50; i++) {
      HealthDataEntity healthDataEntity = testUtil.mockHealthDataEntity();
      em.persist(healthDataEntity);
      em.flush();
    }
    List<HealthDataEntity> res = healthDataService.fetchHealthDataListChunk(50);
    assertEquals(50, res.size());
  }

  @Test
  void updateHealthData_isPersistentAndUpdated() {
    HealthDataEntity healthDataEntity = testUtil.mockHealthDataEntity();
    em.persist(healthDataEntity);
    em.flush();
    HealthDataEntity newHealthDataEntity = healthDataEntity;
    newHealthDataEntity.setIntervalEnd(LocalDateTime.now());
    HealthDataEntity resHealthDataEntity = healthDataService.updateHealthData(newHealthDataEntity);

    assertEquals(
        resHealthDataEntity, em.find(HealthDataEntity.class, healthDataEntity.getDeviceId()));
  }

  @Test
  void deleteHealthData_isPersistentAndDeleted() {
    HealthDataEntity healthDataEntity = testUtil.mockHealthDataEntity();
    em.persist(healthDataEntity);
    em.flush();
    em.clear();
    healthDataService.deleteHealthData(healthDataEntity);
    assertNull(em.find(HealthDataEntity.class, healthDataEntity.getDeviceId()));
  }

  @Test
  void mapToHealthDataEntity_jsonMappedAndObjectReceived() throws IOException {
    String testJson = readClasspath(DATASET);
    Report<HealthData> r =
        mappingUtils.objectMapper.readValue(testJson, new TypeReference<Report<HealthData>>() {});
    assertEquals(OffsetDateTime.parse("2025-07-21T02:00:00Z"), r.values().get(0).intervalStart());
  }

  @Test
  void countReportsCohort() {
    em.persist(testUtil.mockHealthDataEntity());
    int count =
        healthDataService.countNumberReportsOfCohort(
            UUID.fromString("fd1a1b2e-8796-4144-b1fc-fa0d1c2d5b74"));
    assertEquals(1, count);
  }

  @Test
  void getPageOfCohort() {
    for (int i = 0; i < 50; i++) {
      em.persist(testUtil.mockHealthDataEntity());
    }
    assertDoesNotThrow(
        () ->
            healthDataService.getPageOfCohorts(
                UUID.fromString("fd1a1b2e-8796-4144-b1fc-fa0d1c2d5b74"), 0, 5));
    List<HealthDataEntity> firstPage =
        healthDataService.getPageOfCohorts(
            UUID.fromString("fd1a1b2e-8796-4144-b1fc-fa0d1c2d5b74"), 0, 5);
    assertEquals(5, firstPage.size());
  }

  @Test
  void getPageOfCohortAndParameterI() {
    for (int i = 0; i < 50; i++) {
      em.persist(testUtil.mockHealthDataEntity());
    }
    assertDoesNotThrow(
        () ->
            healthDataService.getPageOfCohortsAndParameterId(
                UUID.fromString("fd1a1b2e-8796-4144-b1fc-fa0d1c2d5b74"),
                0,
                5,
                UUID.fromString("4b8f6a22-b0d7-4f31-9b7a-8d2e4b9d1a33")));
    List<HealthDataEntity> firstPage =
        healthDataService.getPageOfCohortsAndParameterId(
            UUID.fromString("fd1a1b2e-8796-4144-b1fc-fa0d1c2d5b74"),
            0,
            5,
            UUID.fromString("4b8f6a22-b0d7-4f31-9b7a-8d2e4b9d1a33"));
    assertEquals(5, firstPage.size());
  }

  /**
   * Reads a file from the test classpath (src/test/resources) into a String. Use a relative path
   * (e.g., "datasets/file.json") without a leading slash.
   */
  private static String readClasspath(String resourcePath) throws IOException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try (InputStream is = cl.getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new IOException("Resource not found on classpath: " + resourcePath);
      }
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
