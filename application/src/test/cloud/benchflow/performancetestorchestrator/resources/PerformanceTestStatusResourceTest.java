package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.response.GetPerformanceTestStatusResponse;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestorchestrator.exceptions.web.InvalidPerformanceTestIDException;
import cloud.benchflow.performancetestorchestrator.helpers.TestArchives;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestModelDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
public class PerformanceTestStatusResourceTest {

//    @ClassRule
//    public static final DropwizardAppRule<PerformanceTestOrchestratorConfiguration> RULE = new DropwizardAppRule<>(PerformanceTestOrchestratorApplication.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private PerformanceTestModelDAO daoMock = Mockito.mock(PerformanceTestModelDAO.class);

    private PerformanceTestStatusResource statusResource;

    // TODO - check for null arguments

    @Before
    public void setUp() throws Exception {

//        ExecutorService performanceTestExecutor = PerformanceTestExecutor.createPerformanceTestExecutor(RULE.getEnvironment());

        statusResource = new PerformanceTestStatusResource(daoMock);

    }

    @Test
    public void getPerformanceTestStatusInValid() throws Exception {

        String performanceTestID = "inValid";

        doThrow(PerformanceTestIDDoesNotExistException.class).when(daoMock).getPerformanceTestModel(performanceTestID);

        exception.expect(InvalidPerformanceTestIDException.class);

        statusResource.getPerformanceTestStatus(performanceTestID);

        verify(daoMock, times(1)).getPerformanceTestModel(performanceTestID);

    }

    @Test
    public void getPerformanceTestStatusValid() throws Exception {

        String performanceTestID = TestArchives.VALID_PERFORMANCE_TEST_ID;

        doReturn(new PerformanceTestModel(performanceTestID)).when(daoMock).getPerformanceTestModel(performanceTestID);

        GetPerformanceTestStatusResponse response = statusResource.getPerformanceTestStatus(performanceTestID);

        verify(daoMock, times(1)).getPerformanceTestModel(performanceTestID);

        // TODO - decide what status should contain and make assertions accordingly

        Assert.assertNotNull(response);
        Assert.assertEquals(performanceTestID, response.getPerformanceTestID());

    }



}