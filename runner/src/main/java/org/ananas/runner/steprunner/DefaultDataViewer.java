package org.ananas.runner.steprunner;

import java.util.HashMap;
import java.util.Map;
import org.ananas.runner.core.DataViewerStepRunner;
import org.ananas.runner.core.StepRunner;
import org.ananas.runner.core.job.Job;
import org.ananas.runner.core.job.JobRepositoryFactory;
import org.ananas.runner.core.model.*;
import org.ananas.runner.core.paginate.AutoDetectedSchemaPaginator;
import org.ananas.runner.core.paginate.PaginatorFactory;
import org.ananas.runner.misc.HomeManager;
import org.ananas.runner.steprunner.jdbc.JDBCDriver;
import org.ananas.runner.steprunner.jdbc.JdbcLoader;
import org.ananas.runner.steprunner.jdbc.JdbcStepConfig;
import org.ananas.runner.steprunner.sql.SQLTransformer;

public class DefaultDataViewer extends DataViewerStepRunner {

  private static final long serialVersionUID = 4331603846982823797L;

  public DefaultDataViewer(
      Step step, StepRunner previous, Engine engine, String jobId, boolean isTest) {
    super(step, previous, engine, jobId, isTest);
  }

  @Override
  public void build() {
    // DefaultDataViewer is actually a wrapper of SQLTransformer in test mode and a jdbcLoader in
    // run mode
    // don't forget to copy the delegation state to the wrapper. (output, for example)
    if (isTest) {
      StepRunner sqlQuery = new SQLTransformer(step, previous);
      sqlQuery.build();
      this.output = sqlQuery.getOutput();
    } else {
      // TODO: accept these configurations from settings
      step.config.put(JdbcStepConfig.JDBC_OVERWRITE, true);
      step.config.put(
          JdbcStepConfig.JDBC_TABLENAME, DataViewRepository.buildTableName(jobId, step.id));
      step.config.put(JdbcStepConfig.JDBC_TYPE, DataViewRepository.getJdbcType(this.engine));
      step.config.put(JdbcStepConfig.JDBC_URL, DataViewRepository.getJdbcURL(this.engine));
      step.config.put(JdbcStepConfig.JDBC_USER, DataViewRepository.getJdbcUser(this.engine));
      step.config.put(
          JdbcStepConfig.JDBC_PASSWORD, DataViewRepository.getJdbcPassword(this.engine));

      StepRunner jdbcLoader = new JdbcLoader(step, previous, isTest);
      jdbcLoader.build();
      this.output = jdbcLoader.getOutput();
    }
  }

  public static class DataViewRepository {
    private JDBCDriver driver;

    private static String URL = "derby:%s/dataview;create=true";

    public DataViewRepository() {
      this.driver = JDBCDriver.DERBY;
    }

    static String URL(boolean withPrefix) {
      return String.format((withPrefix ? "jdbc:" : "") + URL, HomeManager.getHome());
    }

    public Dataframe query(String sql, String jobId, String stepId) {

      // find the job
      Job job = JobRepositoryFactory.getJobRepostory().getJob(jobId);

      if (job == null) {
        throw new RuntimeException(
            "Can't find the last job for this step. Please make sure you have executed the step before explore");
      }

      return query(sql, jobId, stepId, job.engine);
    }

    public Dataframe query(String sql, String jobId, String stepId, Engine engine) {
      String tName = DataViewRepository.buildTableName(jobId, stepId);
      String s;
      String jdbcType = DataViewRepository.getJdbcType(engine);
      if ("derby".equals(jdbcType)) {
        s = sql.toUpperCase().replaceFirst("PCOLLECTION", tName);
      } else {
        s = sql.replaceFirst("PCOLLECTION", tName);
      }

      Map<String, Object> config = new HashMap<>();

      config.put("subtype", "jdbc");
      config.put(JdbcStepConfig.JDBC_SQL, s);
      config.put(JdbcStepConfig.JDBC_OVERWRITE, true);
      config.put(JdbcStepConfig.JDBC_TABLENAME, DataViewRepository.buildTableName(jobId, stepId));
      config.put(JdbcStepConfig.JDBC_TYPE, DataViewRepository.getJdbcType(engine));
      config.put(JdbcStepConfig.JDBC_URL, DataViewRepository.getJdbcURL(engine));
      config.put(JdbcStepConfig.JDBC_USER, DataViewRepository.getJdbcUser(engine));
      config.put(JdbcStepConfig.JDBC_PASSWORD, DataViewRepository.getJdbcPassword(engine));

      // Paginator paginator =
      //    SourcePaginator.of(tName, StepType.Connector.name(), config, new HashMap<>());

      // by default use jdbc paginator
      AutoDetectedSchemaPaginator paginator =
          PaginatorFactory.of(
                  stepId,
                  "org.ananas.destination.jdbc.mysql",
                  StepType.Connector.name(),
                  config,
                  (Dataframe) null,
                  null)
              .buildPaginator();
      Dataframe dataframe = paginator.paginate(0, Integer.MAX_VALUE);
      return dataframe;
    }

    static String buildTableName(String jobId, String stepId) {
      return "table_" + stepId + "_" + jobId.replaceAll("-", "");
    }

    static String getJdbcType(Engine engine) {
      return engine.getProperty(Engine.VIEW_DB_TYPE, "derby");
    }

    static String getJdbcURL(Engine engine) {
      return engine.getProperty(Engine.VIEW_DB_URL, URL(false));
    }

    static String getJdbcUser(Engine engine) {
      String user = engine.getProperty(Engine.VIEW_DB_USER, "");
      if ("".equals(user)) {
        return null;
      }
      return user;
    }

    static String getJdbcPassword(Engine engine) {
      String password = engine.getProperty(Engine.VIEW_DB_PASSWORD, "");
      if ("".equals(password)) {
        return null;
      }
      return password;
    }
  }
}
