package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.response.RunPerformanceTestResponse;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDAlreadyExistsException;
import cloud.benchflow.performancetestorchestrator.exceptions.web.PerformanceTestIDExistsException;
import cloud.benchflow.performancetestorchestrator.helpers.TestArchives;
import cloud.benchflow.performancetestorchestrator.services.external.MinioService;
import cloud.benchflow.performancetestorchestrator.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestModelDAO;
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

import static cloud.benchflow.performancetestorchestrator.helpers.TestArchives.VALID_PERFORMANCE_TEST_ID;
import static org.mockito.Mockito.*;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class RunPerformanceTestResourceTest {

    // Mocks
    private static final ExecutorService executorServiceMock = mock(ExecutorService.class);
    private MinioService minioServiceMock = Mockito.mock(MinioService.class);
    private PerformanceTestModelDAO daoMock = Mockito.mock(PerformanceTestModelDAO.class);
    private PerformanceExperimentManagerService peManagerServiceMock = Mockito.mock(PerformanceExperimentManagerService.class);

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

        resource = new RunPerformanceTestResource(executorServiceMock, minioServiceMock, daoMock, peManagerServiceMock);

    }

    @Test
    public void runPerformanceTestEmptyRequest() throws Exception {

        exception.expect(WebApplicationException.class);
        exception.expectMessage(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()));

        resource.runPerformanceTest(null);

    }

    @Test
    public void runPerformanceTestValid() throws Exception {

        InputStream expArchive = TestArchives.getValidTestArchive();

        RunPerformanceTestResponse response = resource.runPerformanceTest(expArchive);

        Assert.assertEquals(VALID_PERFORMANCE_TEST_ID, response.getPerformanceTestID());

        verify(executorServiceMock, times(1)).submit(Mockito.any(Runnable.class));

    }

    @Test
    public void runPerformanceTestAlreadyExistsException() throws Exception {

        InputStream expArchive = TestArchives.getValidTestArchive();

        doThrow(PerformanceTestIDAlreadyExistsException.class).when(daoMock).addPerformanceTestModel(VALID_PERFORMANCE_TEST_ID);

        exception.expect(PerformanceTestIDExistsException.class);

        resource.runPerformanceTest(expArchive);

    }

    @Test
    public void runPerformanceTestInValid() throws Exception {

        // TODO

//        InputStream expArchive = TestArchives.getInValidTestArchive();
//
//        exception.expect(WebApplicationException.class);
//        exception.expectMessage(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()));
//
//        resource.runPerformanceTest(expArchive);

    }

}