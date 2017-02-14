package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.RunPerformanceTestResponse;
import cloud.benchflow.performancetestorchestrator.helpers.TestArchives;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class RunPerformanceTestResourceTest {

    private static final ExecutorService executorServiceMock = mock(ExecutorService.class);

    private RunPerformanceTestResource resource;


    @Rule
    public ExpectedException exception = ExpectedException.none();


//    @ClassRule
//    public static final DropwizardAppRule<PerformanceTestOrchestratorConfiguration> RULE = new DropwizardAppRule<>(
//            PerformanceTestOrchestratorApplication.class);

    @Before
    public void setUp() throws Exception {

//        ExecutorService performanceTestExecutor = PerformanceTestExecutor.createPerformanceTestExecutor(
//                RULE.getEnvironment());

        resource = new RunPerformanceTestResource(executorServiceMock);

    }

    @Test
    public void runPerformanceTestEmptyRequest() throws Exception {

        exception.expect(WebApplicationException.class);
        exception.expectMessage(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()));

        resource.runPerformanceTest(null);

    }

    @Test
    public void runPerformanceTestInValid() throws Exception {

        InputStream expArchive = TestArchives.getInValidTestArchive();

        exception.expect(WebApplicationException.class);
        exception.expectMessage(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()));

        resource.runPerformanceTest(expArchive);

    }

    @Test
    public void runPerformanceTestValid() throws Exception {

        String expectedPerformanceTestID = "benchflow.testNameExample.1.1.1";
        InputStream expArchive = TestArchives.getValidTestArchive();

        RunPerformanceTestResponse response = resource.runPerformanceTest(expArchive);

        Assert.assertEquals(expectedPerformanceTestID, response.getPerformanceTestID());

        verify(executorServiceMock, times(1)).submit(Mockito.any(Runnable.class));

    }

}