package org.wearables.randomizedresponse;

import jakarta.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterEntity;
import org.wearables.randomizedresponse.healthdata.HealthDataEntity;

public class TestUtil {

  public Logger logger = Logger.getLogger(TestUtil.class.getName());
  public EntityManager entityManager;

  public HealthDataEntity mockHealthDataEntity() {
    UUID randomUUID = UUID.randomUUID();
    HealthDataEntity healthDataEntity = new HealthDataEntity();
    healthDataEntity.setReportId(randomUUID);
    healthDataEntity.setCohort(UUID.fromString("fd1a1b2e-8796-4144-b1fc-fa0d1c2d5b74"));
    healthDataEntity.setParameterId(UUID.fromString("4b8f6a22-b0d7-4f31-9b7a-8d2e4b9d1a33"));
    healthDataEntity.setIntervalStart(LocalDateTime.of(2023, 9, 6, 1, 1, 1));
    healthDataEntity.setIntervalEnd(LocalDateTime.of(2023, 9, 6, 1, 1, 1));
    healthDataEntity.setDeviceId(randomUUID);
    healthDataEntity.setStepCount(8448);
    healthDataEntity.setPrr("0101010000100111");
    healthDataEntity.setIrr("0001000000011111");
    return healthDataEntity;
  }

  public HealthDataEntity mockHealthDataEntity32Bit() {
    UUID randomUUID = UUID.randomUUID();
    HealthDataEntity healthDataEntity = new HealthDataEntity();
    healthDataEntity.setReportId(randomUUID);
    healthDataEntity.setCohort(UUID.fromString("fd1a1b2e-8796-4144-b1fc-fa0d1c2d5b74"));
    healthDataEntity.setParameterId(UUID.fromString("4b8f6a22-b0d7-4f31-9b7a-8d2e4b9d1a33"));
    healthDataEntity.setIntervalStart(LocalDateTime.of(2023, 9, 6, 1, 1, 1));
    healthDataEntity.setIntervalEnd(LocalDateTime.of(2023, 9, 6, 1, 1, 1));
    healthDataEntity.setDeviceId(randomUUID);
    healthDataEntity.setStepCount(8448);
    healthDataEntity.setPrr("10111111000010110011111011111111");
    healthDataEntity.setIrr("10111111000010110011111011111111");
    return healthDataEntity;
  }

  public ParameterEntity mockParameterEntity() {
    ParameterEntity parameterEntity = new ParameterEntity();
    parameterEntity.setParameterId(UUID.fromString("b844cb27-d4af-499d-8332-2061ce481819"));
    parameterEntity.setProfile("test");
    parameterEntity.setMessageBitSize(32);
    parameterEntity.setNumberOfHashFunctions(2);
    parameterEntity.setPermanentProbability(0.5);
    parameterEntity.setInstantaneousProbabilityForOne(0.75);
    parameterEntity.setInstantaneousProbabilityForZero(0.5);
    return parameterEntity;
  }

  public String readMockedJsonDatasetToString(String filename) throws IOException {
    logger.info("Reading file: " + filename);
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());
    return Files.readString(Paths.get(file.getAbsolutePath()));
  }
}
