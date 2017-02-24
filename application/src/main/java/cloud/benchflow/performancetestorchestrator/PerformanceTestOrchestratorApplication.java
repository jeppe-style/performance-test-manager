package cloud.benchflow.performancetestorchestrator;

import cloud.benchflow.performancetestorchestrator.configurations.PerformanceTestOrchestratorConfiguration;
import cloud.benchflow.performancetestorchestrator.resources.PerformanceTestStateResource;
import cloud.benchflow.performancetestorchestrator.resources.PerformanceTestStatusResource;
import cloud.benchflow.performancetestorchestrator.resources.RunPerformanceTestResource;
import cloud.benchflow.performancetestorchestrator.resources.TrialStatusResource;
import cloud.benchflow.performancetestorchestrator.services.external.MinioService;
import cloud.benchflow.performancetestorchestrator.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestExecutor;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceExperimentModelDAO;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceTestModelDAO;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.UserDAO;
import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle;
import de.thomaskrille.dropwizard_template_config.TemplateConfigBundleConfiguration;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class PerformanceTestOrchestratorApplication extends Application<PerformanceTestOrchestratorConfiguration> {

    private Logger logger = LoggerFactory.getLogger(PerformanceTestOrchestratorApplication.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        new PerformanceTestOrchestratorApplication().run(args);
    }

    @Override
    public String getName() {
        return "performance-test-orchestrator";
    }

    @Override
    public void initialize(Bootstrap<PerformanceTestOrchestratorConfiguration> bootstrap) {

        logger.info("initialize");

        // Dropwizard Template Config
        bootstrap.addBundle(new TemplateConfigBundle(new TemplateConfigBundleConfiguration().resourceIncludePath("/app")));

    }

    @Override
    public void run(PerformanceTestOrchestratorConfiguration configuration, Environment environment) throws Exception {

        logger.info("run");

        // services
        ExecutorService performanceTestExecutor = PerformanceTestExecutor.createPerformanceTestExecutor(environment);
        PerformanceTestModelDAO testModelDAO = new PerformanceTestModelDAO(configuration.getMongoDBFactory().build());
        PerformanceExperimentModelDAO experimentModelDAO = new PerformanceExperimentModelDAO(configuration.getMongoDBFactory().build(), testModelDAO);
        UserDAO userDAO = new UserDAO(configuration.getMongoDBFactory().build(), testModelDAO);
        MinioService minioService = configuration.getMinioServiceFactory().build();
        PerformanceExperimentManagerService experimentManagerService = configuration.getPerformanceExperimentManagerServiceFactory().build(
                configuration, environment);


        // resources
        final RunPerformanceTestResource runResource = new RunPerformanceTestResource(performanceTestExecutor,
                                                                                      minioService, testModelDAO, experimentModelDAO, userDAO,
                                                                                      experimentManagerService);

        final PerformanceTestStatusResource statusResource = new PerformanceTestStatusResource(testModelDAO);
        final PerformanceTestStateResource stateResource = new PerformanceTestStateResource(testModelDAO);
        final TrialStatusResource trialStatusResource = new TrialStatusResource(experimentModelDAO);

        // TODO - health checks for all services
//        final TemplateHealthCheck healthCheck =
//                new TemplateHealthCheck(configuration.getTemplate());
//        environment.healthChecks().register("template", healthCheck);


        environment.jersey().register(runResource);
        environment.jersey().register(statusResource);
        environment.jersey().register(stateResource);
        environment.jersey().register(trialStatusResource);

    }


}
