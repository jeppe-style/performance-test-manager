package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.response.RunBenchFlowTestResponse;
import cloud.benchflow.testmanager.exceptions.web.InvalidTestArchiveWebException;
import cloud.benchflow.testmanager.archive.TestArchives;
import cloud.benchflow.testmanager.helpers.TestConstants;
import cloud.benchflow.testmanager.services.external.MinioService;
import cloud.benchflow.testmanager.services.external.BenchFlowExperimentManagerService;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowExperimentModelDAO;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowTestModelDAO;
import cloud.benchflow.testmanager.services.internal.dao.UserDAO;
import cloud.benchflow.testmanager.constants.BenchFlowConstants;
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

import static cloud.benchflow.testmanager.helpers.TestConstants.VALID_BENCHFLOW_TEST_NAME;
import static cloud.benchflow.testmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER;
import static org.mockito.Mockito.*;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class BenchFlowTestResourceTest {

    // Mocks
    private static final ExecutorService executorServiceMock = mock(ExecutorService.class);
    private MinioService minioServiceMock = Mockito.mock(MinioService.class);
    private BenchFlowTestModelDAO testModelDAOMock = Mockito.mock(BenchFlowTestModelDAO.class);
    private BenchFlowExperimentModelDAO experimentModelDAOMock = Mockito.mock(BenchFlowExperimentModelDAO.class);
    private UserDAO userDAOMock = Mockito.mock(UserDAO.class);
    private BenchFlowExperimentManagerService peManagerServiceMock = Mockito.mock(BenchFlowExperimentManagerService.class);

    private BenchFlowTestResource resource;


    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        resource = new BenchFlowTestResource(executorServiceMock, minioServiceMock, testModelDAOMock,
                                               experimentModelDAOMock, userDAOMock, peManagerServiceMock);

    }

    @Test
    public void runBenchFlowTestEmptyRequest() throws Exception {

        exception.expect(WebApplicationException.class);
        exception.expectMessage(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()));

        resource.runBenchFlowTest(null);

    }

    @Test
    public void runBenchFlowTestValid() throws Exception {

        InputStream expArchive = TestArchives.getValidTestArchive();

        String expectedTestID = TestConstants.TEST_USER_NAME + MODEL_ID_DELIMITER + TestConstants.VALID_BENCHFLOW_TEST_NAME + MODEL_ID_DELIMITER + 1;

        Mockito.doReturn(expectedTestID).when(testModelDAOMock).addTestModel(TestConstants.VALID_BENCHFLOW_TEST_NAME, BenchFlowConstants.BENCH_FLOW_USER);

        RunBenchFlowTestResponse response = resource.runBenchFlowTest(expArchive);

        Assert.assertTrue(response.getTestID().contains(VALID_BENCHFLOW_TEST_NAME));

        verify(executorServiceMock, times(1)).submit(Mockito.any(Runnable.class));

    }

    @Test
    public void runInvalidBenchFlowTest() throws Exception {

        InputStream expArchive = TestArchives.getNoDefinitionTestArchive();

        exception.expect(InvalidTestArchiveWebException.class);

        resource.runBenchFlowTest(expArchive);

    }

}