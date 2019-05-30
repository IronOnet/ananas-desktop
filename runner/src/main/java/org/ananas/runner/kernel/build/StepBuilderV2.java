package org.ananas.runner.kernel.build;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.ananas.runner.kernel.ConcatStepRunner;
import org.ananas.runner.kernel.JoinStepRunner;
import org.ananas.runner.kernel.StepRunner;
import org.ananas.runner.kernel.errors.AnanasException;
import org.ananas.runner.kernel.errors.ExceptionHandler.ErrorCode;
import org.ananas.runner.kernel.model.Engine;
import org.ananas.runner.kernel.model.Step;
import org.ananas.runner.kernel.pipeline.PipelineContext;
import org.ananas.runner.kernel.pipeline.PipelineOptionsFactory;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.CannotProvideCoderException;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.NullableCoder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StepBuilderV2 {

  private static final Logger LOG = LoggerFactory.getLogger(StepBuilderV2.class);

  private static Map<String, Class<? extends StepRunner>> REGISTRY = new HashMap<>();

  public static final String TYPE_TRANSFORMER = "transformer";
  public static final String TYPE_LOADER = "loader";
  public static final String TYPE_DATAVIEWER = "viewer";

  public static org.apache.beam.sdk.Pipeline createPipelineRunner(boolean isTest, Engine engine) {
    PipelineOptions options = PipelineOptionsFactory.create(isTest, engine);
    org.apache.beam.sdk.Pipeline p = org.apache.beam.sdk.Pipeline.create(options);
    Class[] classes =
        new Class[] {
          BigDecimal.class,
          Integer.class,
          String.class,
          Boolean.class,
          Long.class,
          Float.class,
          Double.class,
          Timestamp.class,
          Date.class,
          Byte.class
        };
    for (Class clazz : classes) {
      Coder coder;
      try {
        coder = p.getCoderRegistry().getCoder(clazz);
      } catch (CannotProvideCoderException e) {
        LOG.debug(e.getMessage());
        continue;
      }
      p.getCoderRegistry().registerCoderForClass(clazz, NullableCoder.of(coder));
    }
    return p;
  }

  public static StepRunner newStep(String metadataId, Object[] initargs, Class<?>... parameterTypes) {
    if (!REGISTRY.containsKey(metadataId)) {
      throw new AnanasException(
              ErrorCode.DAG, "No StepRunner is registered for meta id: " + metadataId);
    }

    Class<? extends StepRunner> clazz = REGISTRY.get(metadataId);

    try {
      Constructor<? extends StepRunner> ctor =
              clazz.getDeclaredConstructor(parameterTypes);
      ctor.setAccessible(true);
      StepRunner connector = ctor.newInstance(initargs);
      connector.build();
      return connector;
    } catch (InstantiationException
            | NoSuchMethodException
            | IllegalAccessException
            | InvocationTargetException e) {
      System.out.println(e.getMessage());
      throw new AnanasException(ErrorCode.DAG, e.getLocalizedMessage());
    }
  }

  public static StepRunner connector(
      Step step, PipelineContext context, boolean doSampling, boolean isTest) {
      return newStep(step.metadataId, new Object[] {context.getPipeline(), step, doSampling, isTest}, Pipeline.class, Step.class, Boolean.TYPE, Boolean.TYPE);
  }

  public static StepRunner transformer(Step step, StepRunner previous) {
      return newStep(step.metadataId, new Object[] {step, previous}, Step.class, StepRunner.class);
  }

  public static StepRunner loader(Step step, StepRunner previous, boolean isTest) {
    return newStep(step.metadataId, new Object[] {step, previous, isTest}, Step.class, StepRunner.class, Boolean.TYPE);
  }

  public static StepRunner dataViewer(Step step, StepRunner previous, boolean isTest) {
    return newStep(step.metadataId, new Object[] {step, previous, isTest}, Step.class, StepRunner.class, Boolean.TYPE);
  }

  public static StepRunner append(Step step, StepRunner previous, boolean isTest) {
    switch (step.type) {
      case TYPE_TRANSFORMER:
        return transformer(step, previous);
      case TYPE_LOADER:
        return loader(step, previous, isTest);
      case TYPE_DATAVIEWER:
        return dataViewer(step, previous, isTest);
      default:
        throw new IllegalStateException("Unsupported transformer/loader type '" + step.type + "'");
    }
  }

  public static StepRunner join(Step step, StepRunner one, StepRunner another) {
    StepRunner joinStepRunner = new JoinStepRunner(step, one, another);
    joinStepRunner.build();
    return joinStepRunner;
  }

  public static StepRunner concat(Step step, StepRunner one, StepRunner another) {
    StepRunner concatStepRunner = new ConcatStepRunner(step, one, another);
    concatStepRunner.build();
    return concatStepRunner;
  }

  public static void register(String metaId, Class<? extends StepRunner> clazz) {
    REGISTRY.put(metaId, clazz);
  }
}
