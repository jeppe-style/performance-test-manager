package cloud.benchflow.performancetestmanager.configurations;

import cloud.benchflow.performancetestmanager.configurations.factory.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class PerformanceTestManagerConfiguration extends Configuration {

    // see http://www.dropwizard.io/1.0.6/docs/manual/core.html#configuration

    // Jersey Client Configuration
    @Valid
    @NotNull
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty("jerseyClient")
    public void setJerseyClient(JerseyClientConfiguration jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    // Swagger Configuration
    @Valid
    @NotNull
    private final SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();

    @JsonProperty("swagger")
    public SwaggerBundleConfiguration getSwagger() {
        return swagger;
    }

    // BenchFlow Environment Configuration
    @Valid
    @NotNull
    private BenchFlowEnvironmentFactory benchFlowEnvironmentFactory = new BenchFlowEnvironmentFactory();

    @JsonProperty("benchFlowEnvironment")
    public BenchFlowEnvironmentFactory getBenchFlowEnvironmentFactory() {
        return benchFlowEnvironmentFactory;
    }

    @JsonProperty("benchFlowEnvironment")
    public void setBenchFlowEnvironmentFactory(BenchFlowEnvironmentFactory benchFlowEnvironmentFactory) {
        this.benchFlowEnvironmentFactory = benchFlowEnvironmentFactory;
    }

    // MongoDB Configuration
    @Valid
    @NotNull
    private MongoDBFactory mongoDBFactory = new MongoDBFactory();

    @JsonProperty("mongoDB")
    public MongoDBFactory getMongoDBFactory() {
        return mongoDBFactory;
    }

    @JsonProperty("mongoDB")
    public void setMongoDBFactory(MongoDBFactory mongoDBFactory) {
        this.mongoDBFactory = mongoDBFactory;
    }

    // Performance-Experiment-Manager Service
    @Valid
    @NotNull
    private PEManagerServiceFactory performanceExperimentManagerServiceFactory = new PEManagerServiceFactory();

    @JsonProperty("performanceExperimentManager")
    public PEManagerServiceFactory getPerformanceExperimentManagerServiceFactory() {
        return performanceExperimentManagerServiceFactory;
    }

    @JsonProperty("performanceExperimentManager")
    public void setPerformanceExperimentManagerServiceFactory(PEManagerServiceFactory performanceExperimentManagerServiceFactory) {
        this.performanceExperimentManagerServiceFactory = performanceExperimentManagerServiceFactory;
    }

    // Minio Service
    @Valid
    @NotNull
    private MinioServiceFactory minioServiceFactory = new MinioServiceFactory();

    @JsonProperty("minio")
    public MinioServiceFactory getMinioServiceFactory() {
        return minioServiceFactory;
    }

    @JsonProperty("minio")
    public void setMinioServiceFactory(MinioServiceFactory minioServiceFactory) {
        this.minioServiceFactory = minioServiceFactory;
    }

    // Task Executor
    @Valid
    @NotNull
    private TaskExecutorFactory taskExecutorFactory = new TaskExecutorFactory();

    @JsonProperty("taskExecutor")
    public TaskExecutorFactory getTaskExecutorFactory() {
        return taskExecutorFactory;
    }

    @JsonProperty("taskExecutor")
    public void setTaskExecutorFactory(TaskExecutorFactory taskExecutorFactory) {
        this.taskExecutorFactory = taskExecutorFactory;
    }
}
