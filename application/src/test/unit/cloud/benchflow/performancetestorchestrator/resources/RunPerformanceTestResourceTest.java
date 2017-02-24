package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.response.RunPerformanceTestResponse;
import cloud.benchflow.performancetestorchestrator.exceptions.web.InvalidTestArchiveWebException;
import cloud.benchflow.performancetestorchestrator.archive.TestArchives;
import cloud.benchflow.performancetestorchestrator.helpers.TestConstants;
import cloud.benchflow.performancetestorchestrator.services.external.MinioService;
import cloud.benchflow.performancetestorchestrator.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceExperimentModelDAO;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceTestModelDAO;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.UserDAO;
import cloud.benchflow.performancetestorchestrator.constants.BenchFlowConstants;
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

import static cloud.benchflow.performancetestorchestrator.helpers.TestConstants.VALID_PERFORMANCE_TEST_NAME;
import static cloud.benchflow.performancetestorchestrator.constants.BenchFlowConstants.MODEL_ID_DELIMITER;
import static org.mockito.Mockito.*;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class RunPerformanceTestResourceTest {

    // Mocks
    private static final ExecutorService executorServiceMock = mock(ExecutorService.class);
    private MinioService minioServiceMock = Mockito.mock(MinioService.class);
    private PerformanceTestModelDAO testModelDAOMock = Mockito.mock(PerformanceTestModelDAO.class);
    private PerformanceExperimentModelDAO experimentModelDAOMock = Mockito.mock(PerformanceExperimentModelDAO.class);
    private UserDAO userDAOMock = Mockito.mock(UserDAO.class);
    private PerformanceExperimentManagerService peManagerServiceMock = Mockito.mock(PerformanceExperimentManagerService.class);

    private RunPerformanceTestResource resource;


    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        resource = new RunPerformanceTestResource(executorServiceMock, minioServiceMock, testModelDAOMock,
                                                  experimentModelDAOMock, userDAOMock, peManagerServiceMock);

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

        String expectedTestID = TestConstants.TEST_USER_NAME + MODEL_ID_DELIMITER + TestConstants.VALID_PERFORMANCE_TEST_NAME + MODEL_ID_DELIMITER + 1;

        Mockito.doReturn(expectedTestID).when(testModelDAOMock).addPerformanceTestModel(TestConstants.VALID_PERFORMANCE_TEST_NAME, BenchFlowConstants.BENCH_FLOW_USER);

        RunPerformanceTestResponse response = resource.runPerformanceTest(expArchive);

        Assert.assertTrue(response.getPerformanceTestID().contains(VALID_PERFORMANCE_TEST_NAME));

        verify(executorServiceMock, times(1)).submit(Mockito.any(Runnable.class));

    }

    @Test
    public void runInvalidPerformanceTest() throws Exception {

        InputStream expArchive = TestArchives.getNoDefinitionTestArchive();

        exception.expect(InvalidTestArchiveWebException.class);

        resource.runPerformanceTest(expArchive);

    }

}