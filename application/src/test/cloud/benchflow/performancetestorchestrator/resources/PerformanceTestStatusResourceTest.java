package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.PerformanceTestOrchestratorApplication;
import cloud.benchflow.performancetestorchestrator.api.PerformanceTestStatusResponse;
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
 *         created on 19.12.16.
 */
public class PerformanceTestStatusResourceTest {

    private RunPerformanceTestResource runResources;

    String TEST_ARCHIVE_FILENAME = "src/test/resources/data/wfms.camunda.zip";

    @ClassRule
    public static final DropwizardAppRule<PerformanceTestOrchestratorConfiguration> RULE = new DropwizardAppRule<>(PerformanceTestOrchestratorApplication.class);
    private PerformanceTestStatusResource statusResource;

    @Before
    public void setUp() throws Exception {

        ExecutorService performanceTestExecutor = PerformanceTestExecutor.createPerformanceTestExecutor(RULE.getEnvironment());

        runResources = new RunPerformanceTestResource(performanceTestExecutor);

        statusResource = new PerformanceTestStatusResource();

    }

    @Test
    public void getPerformanceTestStatus() throws Exception {

        String performanceTestName = "myPerformanceTest";

        InputStream expArchive = new FileInputStream(TEST_ARCHIVE_FILENAME);

        RunPerformanceTestResponse response = runResources.runPerformanceTest(performanceTestName, expArchive);

        PerformanceTestStatusResponse statusResponse = statusResource.getPerformanceTestStatus(response.getPerformanceTestId());

        while (statusResponse.getStatus().equals(PerformanceTestStatusResource.RUNNING)) {

            Thread.sleep(1000);

            statusResponse = statusResource.getPerformanceTestStatus(response.getPerformanceTestId());

        }

        Assert.assertEquals(PerformanceTestStatusResource.COMPLETED, statusResponse.getStatus());

    }



}