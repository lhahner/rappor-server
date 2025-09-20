package org.wearables.randomizedresponse.differentialprivacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.wearables.randomizedresponse.TestUtil;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterEntity;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterService;
import org.wearables.randomizedresponse.healthdata.HealthDataService;
import org.wearables.randomizedresponse.utilities.MappingUtils;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ParameterControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean HealthDataService healthDataService;

  @Autowired private EntityManager em;

  @InjectMocks private ParameterService parameterService;

  TestUtil testUtil;

  @Autowired private MappingUtils mappingUtils;

  private static final String TEST_PARAMETER_PROFILE = "datasets/test-parameter-profile.json";

  @BeforeEach
  void setup() {
    testUtil = new TestUtil();
  }

  @Test
  void getParameters_receiveDefaultJson() throws Exception {
    this.mockMvc
        .perform(get("/parameters?mode=default").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"));
  }

  @Test
  void getParameters_receiveJsonProfile() throws Exception {
    ParameterEntity parameterEntity = testUtil.mockParameterEntity();
    em.persist(parameterEntity);
    em.flush();

    this.mockMvc
        .perform(get("/parameters?mode=profile&name=test").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"));
  }

  @Test
  void postParameters_profileIsNotPresent() throws Exception {
    String json = readClasspath(TEST_PARAMETER_PROFILE);
    mockMvc
        .perform(
            post("/parameters?profile=test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isOk());

    ParameterEntity parameterEntity =
        em.find(ParameterEntity.class, UUID.fromString("b844cb27-d4af-499d-8332-2061ce481819"));
    assertNotNull(parameterEntity);
  }

  @Test
  void postParameters_profileIsPresent() throws Exception {
    String json = readClasspath(TEST_PARAMETER_PROFILE);
    ParameterEntity parameterEntity = testUtil.mockParameterEntity();
    em.persist(parameterEntity);
    em.flush();

    mockMvc
        .perform(
            post("/parameters?profile=test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isOk());

    ParameterEntity parameterEntityActual =
        em.find(ParameterEntity.class, parameterEntity.getParameterId());
    assertEquals(parameterEntity.getParameterId(), parameterEntityActual.getParameterId());
  }

  /**
   * Reads a file from the test classpath (src/test/resources) into a String. The path must be
   * relative (e.g., "datasets/file.json") with no leading slash.
   */
  private static String readClasspath(String resourcePath) throws IOException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try (InputStream is = cl.getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new IOException("Resource not found on classpath: " + resourcePath);
      }
      byte[] bytes = is.readAllBytes();
      return new String(bytes, StandardCharsets.UTF_8);
    }
  }
}
