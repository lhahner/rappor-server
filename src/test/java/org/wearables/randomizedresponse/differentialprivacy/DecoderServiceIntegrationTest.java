package org.wearables.randomizedresponse.differentialprivacy;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.wearables.randomizedresponse.TestUtil;
import org.wearables.randomizedresponse.differentialprivacy.decoder.DecoderService;
import org.wearables.randomizedresponse.differentialprivacy.decoder.substance.Substance;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterService;
import org.wearables.randomizedresponse.healthdata.HealthData;
import org.wearables.randomizedresponse.healthdata.HealthDataEntity;
import org.wearables.randomizedresponse.healthdata.HealthDataService;
import org.wearables.randomizedresponse.utilities.MappingUtils;

@SpringBootTest
public class DecoderServiceIntegrationTest {

  @Autowired private DecoderService<HealthDataEntity> decoderService;

  @Autowired private HealthDataService healthDataService;

  @Autowired private ParameterService parameterService;

  @InjectMocks private TestUtil testUtil;

  @Autowired private MappingUtils mappingUtils;

  private static final String PATH_COHORT_ONE = "datasets/sample-dataset.json";
  private static final String LARGE_DATASET = "datasets/large-sample-dataset.json";

  double averageSteps = 0;

  private static String readClasspath(String path) throws IOException {
    Resource resource = new ClassPathResource(path); // no leading slash
    try (var is = resource.getInputStream()) {
      if (is == null) {
        throw new IOException("Resource not found on classpath: " + path);
      }
      return new String(is.readAllBytes());
    }
  }

  @BeforeEach
  void setup() throws IOException {
    String testJsonCohortOne = readClasspath(PATH_COHORT_ONE);
    Report<HealthData> reportCohortOne =
        mappingUtils.objectMapper.readValue(
            testJsonCohortOne, new TypeReference<Report<HealthData>>() {});
    List<HealthDataEntity> testCohortOne =
        healthDataService.mapToHealthDataEntities(reportCohortOne);

    averageSteps =
        testCohortOne.stream().mapToInt(HealthDataEntity::getStepCount).average().orElse(0);

    Substance<HealthDataEntity> substance = new Substance<>();
    substance.setEntities(testCohortOne);
    substance.setMessageBitSize(parameterService.getDefaultParameterEntity().getMessageBitSize());
    substance.setMaxRange(1000);
    substance.setRangeIterator(100);
    substance.setStartRange(0);
    substance.setCohort(1);
    substance.setLambdas(new double[] {0.01, 0.05, 0.1, 0.2, 0.4});
    substance.setParameterEntity(testUtil.mockParameterEntity());
    ReflectionTestUtils.setField(decoderService, "substance", substance);
  }

  @Test
  void decoderPipelineUntilLassoRegression() {

    assertDoesNotThrow(() -> decoderService.runPipeline());
    Substance<HealthDataEntity> substance = new Substance<>();
    substance = decoderService.getSubstance();
  }

  @Test
  void calculateMaxRangeForStepCountBin_validResult() {
    List<HealthDataEntity> healthDataEntities = new ArrayList<>();
    for (int i = 0; i < 1321; i++) {
      HealthDataEntity entity = new HealthDataEntity();
      entity.setStepCount(i);
      healthDataEntities.add(entity);
    }
    assertEquals(1400, decoderService.calculateMaxRangeForStepCountBin(healthDataEntities, 100));
  }

  @Test
  void runPipeline_computeLargeData() throws IOException {
    String testJsonCohortOne = readClasspath(LARGE_DATASET);
    Report<HealthData> reportLargeDataset =
        mappingUtils.objectMapper.readValue(
            testJsonCohortOne, new TypeReference<Report<HealthData>>() {});
    List<HealthDataEntity> testLargeDataset =
        healthDataService.mapToHealthDataEntities(reportLargeDataset);

    Substance<HealthDataEntity> substance = new Substance<>();
    substance.setEntities(testLargeDataset);
    substance.setMessageBitSize(parameterService.getDefaultParameterEntity().getMessageBitSize());
    int maxValueRange = decoderService.calculateMaxRangeForStepCountBin(testLargeDataset, 100);
    substance.setMaxRange(maxValueRange);
    substance.setRangeIterator(100);
    substance.setStartRange(0);
    substance.setCohort(1);
    substance.setLambdas(new double[] {0.01, 0.05, 0.1, 0.2, 0.4});
    substance.setParameterEntity(testUtil.mockParameterEntity());
    ReflectionTestUtils.setField(decoderService, "substance", substance);
    assertDoesNotThrow(() -> decoderService.runPipeline());
    substance = decoderService.getSubstance();
  }

  @Test
  void buildProbabiltiesMap_validResult() throws IOException {
    String testJsonCohortOne = readClasspath(LARGE_DATASET);
    Report<HealthData> reportLargeDataset =
        mappingUtils.objectMapper.readValue(
            testJsonCohortOne, new TypeReference<Report<HealthData>>() {});
    List<HealthDataEntity> testLargeDataset =
        healthDataService.mapToHealthDataEntities(reportLargeDataset);

    Substance<HealthDataEntity> substance = new Substance<>();
    substance.setEntities(testLargeDataset);
    substance.setMessageBitSize(parameterService.getDefaultParameterEntity().getMessageBitSize());
    int maxValueRange = decoderService.calculateMaxRangeForStepCountBin(testLargeDataset, 100);
    substance.setMaxRange(maxValueRange);
    substance.setRangeIterator(100);
    substance.setStartRange(0);
    substance.setCohort(1);
    substance.setLambdas(new double[] {0.01, 0.05, 0.1, 0.2, 0.4});
    substance.setParameterEntity(testUtil.mockParameterEntity());
    ReflectionTestUtils.setField(decoderService, "substance", substance);
    assertDoesNotThrow(() -> decoderService.runPipeline());
    substance = decoderService.getSubstance();
    Map<String, Double> map = decoderService.buildOutputRangeToProbabiltiesMap(substance);
    assertNotNull(map);
  }
}
