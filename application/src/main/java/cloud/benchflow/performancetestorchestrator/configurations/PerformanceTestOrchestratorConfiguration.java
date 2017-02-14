package cloud.benchflow.performancetestorchestrator.configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class PerformanceTestOrchestratorConfiguration extends Configuration {

    // TODO - BenchFlow Environment Configuration

    // TODO - HTTP Client Configuration

    // TODO - Server Configuration

    // TODO - Data store Configuration
//    @Valid
//    @NotNull
    private DataStoreConfiguration dataStoreConfiguration = new DataStoreConfiguration();

    // Performance-Experiment-Manager Service
    @Valid
    @NotNull
    private PEManagerServiceConfiguration peManagerServiceConfiguration = new PEManagerServiceConfiguration();

    // Minio Service
    @Valid
    @NotNull
    private MinioServiceConfiguration minioServiceConfiguration = new MinioServiceConfiguration();


    @JsonProperty("performance-experiment-manager")
    public PEManagerServiceConfiguration getPeManagerServiceConfiguration() {
        return peManagerServiceConfiguration;
    }

    @JsonProperty("performance-experiment-manager")
    public void setPeManagerServiceConfiguration(PEManagerServiceConfiguration peManagerServiceConfiguration) {
        this.peManagerServiceConfiguration = peManagerServiceConfiguration;
    }

    @JsonProperty("minio")
    public MinioServiceConfiguration getMinioServiceConfiguration() {
        return minioServiceConfiguration;
    }

    @JsonProperty("minio")
    public void setMinioServiceConfiguration(MinioServiceConfiguration minioServiceConfiguration) {
        this.minioServiceConfiguration = minioServiceConfiguration;
    }
}
