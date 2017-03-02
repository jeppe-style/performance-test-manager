package cloud.benchflow.performancetestmanager.configurations;

import cloud.benchflow.performancetestmanager.services.external.PerformanceExperimentManagerService;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.client.Client;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class PEManagerServiceFactory {

    @NotEmpty
    private String address;

    @JsonProperty
    public String getAddress() {
        return address;
    }

    @JsonProperty
    public void setAddress(String address) {
        this.address = address;
    }


    /**
     * @param environment
     * @return
     */
    public PerformanceExperimentManagerService build(PerformanceTestOrchestratorConfiguration config, Environment environment) {

        Client client = new JerseyClientBuilder(environment)
                .using(config.getJerseyClientConfiguration())
                .build(environment.getName());

        return new PerformanceExperimentManagerService(client, getAddress());

    }
}
