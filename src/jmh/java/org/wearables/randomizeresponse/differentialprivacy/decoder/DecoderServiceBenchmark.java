package org.wearables.randomizeresponse.differentialprivacy.decoder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.wearables.randomizedresponse.differentialprivacy.decoder.DecoderService;
import org.wearables.randomizedresponse.differentialprivacy.decoder.substance.Substance;
import org.wearables.randomizedresponse.differentialprivacy.parameter.ParameterEntity;
import org.wearables.randomizedresponse.healthdata.HealthDataEntity;

/**
 * JMH microbenchmarks for DecoderService without starting Spring. Adjust StubHealthDataEntity below
 * to match your actual entity API if needed.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 8)
@Fork(1)
@State(Scope.Benchmark)
public class DecoderServiceBenchmark {

  @Param({"100000"})
  int records;

  @Param({"32"})
  int messageBitSize;

  DecoderService<HealthDataEntity> service;
  Substance<HealthDataEntity> substance;
  List<HealthDataEntity> entities;
  ParameterEntity params;

  @Setup(Level.Trial)
  public void setup() {
    service = new DecoderService<>();

    entities = generateEntities(records, messageBitSize, 42L);
    params = new ParameterEntity(); // if your type needs specific ctor/fields, set them here.
    params.setMessageBitSize(messageBitSize);
    params.setInstantaneousProbabilityForOne(0.2);
    params.setPermanentProbability(0.5);
    params.setInstantaneousProbabilityForZero(0.5);
    params.setNumberOfHashFunctions(2);

    substance = new Substance<>();
    substance.setEntities(entities);
    substance.setMessageBitSize(messageBitSize);
    substance.setParameterEntity(params);
    substance.setRangeIterator(10);
    substance.setMaxRange(1000);
    substance.setLambdas(new double[] {0.1, 0.2, 0.3});
    substance.setStartRange(0);
    service.setSubstance(substance);
  }

  @Benchmark
  public void run_pipeline(Blackhole bh) {
    Substance<HealthDataEntity> result = service.runPipeline();
    // Prevent DCE; you might also consume specific fields from result if available.
    bh.consume(result);
  }

  @Benchmark
  public int calc_max_range_for_step_bins() {
    // Micro-benchmark a pure method that avoids pipeline variability
    return service.calculateMaxRangeForStepCountBin(entities, 500);
  }

  private static List<HealthDataEntity> generateEntities(int n, int bitSize, long seed) {
    Random rnd = new Random(seed);
    return IntStream.range(0, n)
        .mapToObj(
            i -> {
              String prr = randomBits(rnd, bitSize);
              int step = 1_000 + rnd.nextInt(20_000); // 1k..21k
              return stub(prr, step);
            })
        .collect(Collectors.toList());
  }

  private static String randomBits(Random rnd, int bitSize) {
    StringBuilder sb = new StringBuilder(bitSize);
    for (int i = 0; i < bitSize; i++) sb.append(rnd.nextBoolean() ? '1' : '0');
    return sb.toString();
  }

  private static HealthDataEntity stub(String prr, int stepCount) {
    // If HealthDataEntity is a concrete class with setters, construct and set directly here.
    // Otherwise, use the StubHealthDataEntity below (it extends your entity and adds setters).
    StubHealthDataEntity e = new StubHealthDataEntity();
    e.setPrr(prr);
    e.setStepCount(stepCount);
    return e;
  }
}
