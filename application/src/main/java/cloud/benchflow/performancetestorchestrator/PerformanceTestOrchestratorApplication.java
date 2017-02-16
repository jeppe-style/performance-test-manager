package cloud.benchflow.performancetestorchestrator;

import cloud.benchflow.performancetestorchestrator.configurations.PerformanceTestOrchestratorConfiguration;
import cloud.benchflow.performancetestorchestrator.resources.PerformanceTestStatusResource;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestExecutor;
import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.concurrent.ExecutorService;

public class PerformanceTestOrchestratorApplication extends Application<PerformanceTestOrchestratorConfiguration> {

    public static void main(String[] args) throws Exception {
        new PerformanceTestOrchestratorApplication().run(args);
    }



    @Override
    public String getName() {
        return "performance-test-orchestrator";
    }

    @Override
    public void initialize(Bootstrap<PerformanceTestOrchestratorConfiguration> bootstrap) {

        // Dropwizard Template Config
        bootstrap.addBundle(new TemplateConfigBundle());

    }

    @Override
    public void run(PerformanceTestOrchestratorConfiguration performanceTestOrchestratorConfiguration, Environment environment) throws Exception {


        // thread pools
        ExecutorService performanceTestExecutor = PerformanceTestExecutor.createPerformanceTestExecutor(environment);

        // TODO -resources
//        final RunPerformanceTestResource runResource = new RunPerformanceTestResource(performanceTestExecutor);
        // TODO - use real DAO
        final PerformanceTestStatusResource statusResource = new PerformanceTestStatusResource(null);

        // health checks
//        final TemplateHealthCheck healthCheck =
//                new TemplateHealthCheck(configuration.getTemplate());
//        environment.healthChecks().register("template", healthCheck);


//        environment.jersey().register(runResource);
        environment.jersey().register(statusResource);

    }



}
