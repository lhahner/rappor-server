package org.wearables.randomizedresponse.healthdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.wearables.randomizedresponse.TestUtil;
import org.wearables.randomizedresponse.differentialprivacy.Report;
import org.wearables.randomizedresponse.differentialprivacy.decoder.DecoderService;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterEntity;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterService;
import org.wearables.randomizedresponse.utilities.MappingUtils;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HealthDataControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  // Use @Autowired so the real service is wired in the Spring test context
  @Autowired private HealthDataService healthDataService;

  private TestUtil testUtil;

  @Autowired private EntityManager em;

  @Autowired private DecoderService<HealthDataEntity> decoderService;

  @Autowired private MappingUtils mappingUtils;

  @Autowired private ParameterService parameterService;

  // Classpath location: src/test/resources/datasets/sample-dataset.json
  private static final String PATH_COHORT_ONE = "datasets/sample-dataset.json";

  private List<HealthDataEntity> testCohortOne;

  @BeforeEach
  void setUp() throws IOException {
    testUtil = new TestUtil();
    String testJsonCohortOne = readClasspath(PATH_COHORT_ONE);
    Report<HealthData> reportCohortOne =
        mappingUtils.objectMapper.readValue(
            testJsonCohortOne, new TypeReference<Report<HealthData>>() {});
    testCohortOne = healthDataService.mapToHealthDataEntities(reportCohortOne);
  }

  @Test
  void getHealthData_receiveHealthDataJson() throws Exception {
    HealthDataEntity entity = testUtil.mockHealthDataEntity();
    em.persist(entity);
    em.flush();

    mockMvc
        .perform(
            get("/healthdata/" + entity.getReportId().toString())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void getHealthDataByIdAndIntervalStartAndIntervalEnd_recieveHealthDataJson() throws Exception {
    HealthDataEntity entity = testUtil.mockHealthDataEntity();
    em.persist(entity);
    em.flush();

    mockMvc
        .perform(
            get("/healthdata?intervalStart=2023-09-06T01:01:01").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void postHealthData_statusOkAndDataInserted() throws Exception {
    String testJson = readClasspath(PATH_COHORT_ONE);

    Report<HealthData> r =
        mappingUtils.objectMapper.readValue(testJson, new TypeReference<Report<HealthData>>() {});
    assertEquals(OffsetDateTime.parse("2025-07-21T02:00:00Z"), r.values().get(0).intervalStart());

    mockMvc
        .perform(
            post("/healthdata/upload")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson))
        .andExpect(status().isOk());

    HealthDataEntity healthData =
        em.find(HealthDataEntity.class, UUID.fromString("db053aad-ceb8-41e3-b717-c1ce838ff6a6"));
    assertNotNull(healthData);
  }

  @Test
  void getDecoder_ReturnsJson() throws Exception {
    em.persist(testCohortOne.getFirst());
    em.flush();

    ParameterEntity parameterEntity = testUtil.mockParameterEntity();
    em.persist(parameterEntity);
    em.flush();

    mockMvc
        .perform(
            get("/healthdata/decode?parameterProfile=test&cohort="
                    + "049ea6d6-b4db-4926-a020-2612264140fa"
                    + "&numberOfReports=1")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  /**
   * Reads a file from the test classpath (src/test/resources) into a String. Path should be
   * relative, e.g., "datasets/file.json", with no leading slash.
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
