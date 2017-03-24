package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.response.GetBenchFlowTestStatusResponse;
import cloud.benchflow.testmanager.exceptions.BenchFlowTestIDDoesNotExistException;
import cloud.benchflow.testmanager.exceptions.web.InvalidBenchFlowTestIDWebException;
import cloud.benchflow.testmanager.helpers.TestConstants;
import cloud.benchflow.testmanager.models.BenchFlowTestModel;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowTestModelDAO;
import cloud.benchflow.testmanager.constants.BenchFlowConstants;
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
public class BenchFlowTestStatusResourceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private BenchFlowTestModelDAO daoMock = Mockito.mock(BenchFlowTestModelDAO.class);

    private BenchFlowTestStatusResource statusResource;

    @Before
    public void setUp() throws Exception {

        statusResource = new BenchFlowTestStatusResource(daoMock);

    }

    @Test
    public void getBenchFlowTestStatusInValid() throws Exception {

        String testID = "inValid";

        doThrow(BenchFlowTestIDDoesNotExistException.class).when(daoMock).getTestModel(testID);

        exception.expect(InvalidBenchFlowTestIDWebException.class);

        statusResource.getBenchFlowTestStatus(testID);

        verify(daoMock, times(1)).getTestModel(testID);

    }

    @Test
    public void getBenchFlowTestStatusValid() throws Exception {

        String benchFlowTestName = TestConstants.VALID_BENCHFLOW_TEST_NAME;

        String expectedTestID = TestConstants.TEST_USER_NAME + BenchFlowConstants.MODEL_ID_DELIMITER + benchFlowTestName + BenchFlowConstants.MODEL_ID_DELIMITER + 1;

        doReturn(new BenchFlowTestModel(TestConstants.TEST_USER, benchFlowTestName, 1)).when(
                daoMock).getTestModel(benchFlowTestName);

        GetBenchFlowTestStatusResponse response = statusResource.getBenchFlowTestStatus(benchFlowTestName);

        verify(daoMock, times(1)).getTestModel(benchFlowTestName);

        // TODO - decide what status should contain and make assertions accordingly

        Assert.assertNotNull(response);
        Assert.assertEquals(expectedTestID, response.getTestID());

    }


}