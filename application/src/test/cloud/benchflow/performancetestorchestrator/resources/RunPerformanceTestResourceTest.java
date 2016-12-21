package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.PerformanceTestOrchestratorApplication;
import cloud.benchflow.performancetestorchestrator.api.RunPerformanceTestResponse;
import cloud.benchflow.performancetestorchestrator.configurations.PerformanceTestOrchestratorConfiguration;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestExecutor;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class RunPerformanceTestResourceTest {

    @ClassRule
    public static final DropwizardAppRule<PerformanceTestOrchestratorConfiguration> RULE = new DropwizardAppRule<>(
            PerformanceTestOrchestratorApplication.class);

    String TEST_ARCHIVE_FILENAME = "src/test/resources/data/wfms.camunda.zip";
    private RunPerformanceTestResource resource;

    @Before
    public void setUp() throws Exception {

        ExecutorService performanceTestExecutor = PerformanceTestExecutor.createPerformanceTestExecutor(
                RULE.getEnvironment());

        resource = new RunPerformanceTestResource(performanceTestExecutor);

    }

    @Test
    public void runPerformanceTest() throws Exception {

        String performanceTestName = "myPerformanceTest";


        InputStream expArchive = new FileInputStream(TEST_ARCHIVE_FILENAME);

        RunPerformanceTestResponse response = resource.runPerformanceTest(performanceTestName, expArchive);

        Assert.assertEquals(performanceTestName, response.getPerformanceTestId());

        // TODO - remove after demo
        Thread.sleep(15000);

    }

}