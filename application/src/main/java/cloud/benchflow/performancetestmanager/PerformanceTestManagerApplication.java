package cloud.benchflow.performancetestmanager;

import cloud.benchflow.performancetestmanager.configurations.PerformanceTestOrchestratorConfiguration;
import cloud.benchflow.performancetestmanager.resources.PerformanceTestStateResource;
import cloud.benchflow.performancetestmanager.resources.PerformanceTestStatusResource;
import cloud.benchflow.performancetestmanager.resources.PerformanceTestResource;
import cloud.benchflow.performancetestmanager.resources.TrialStatusResource;
import cloud.benchflow.performancetestmanager.services.external.MinioService;
import cloud.benchflow.performancetestmanager.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestmanager.services.internal.PerformanceTestExecutor;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceExperimentModelDAO;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceTestModelDAO;
import cloud.benchflow.performancetestmanager.services.internal.dao.UserDAO;
import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle;
import de.thomaskrille.dropwizard_template_config.TemplateConfigBundleConfiguration;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class PerformanceTestManagerApplication extends Application<PerformanceTestOrchestratorConfiguration> {

    private Logger logger = LoggerFactory.getLogger(PerformanceTestManagerApplication.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        new PerformanceTestManagerApplication().run(args);
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
        final PerformanceTestResource runResource = new PerformanceTestResource(performanceTestExecutor,
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

        // TODO - we need this??
//        environment.jersey().register(MultiPartFeature.class);

    }


}
