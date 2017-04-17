package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.request.ChangeBenchFlowTestStateRequest;
import cloud.benchflow.testmanager.api.response.ChangeBenchFlowTestStateResponse;
import cloud.benchflow.testmanager.constants.BenchFlowConstants;
import cloud.benchflow.testmanager.exceptions.BenchFlowTestIDDoesNotExistException;
import cloud.benchflow.testmanager.exceptions.web.InvalidBenchFlowTestIDWebException;
import cloud.benchflow.testmanager.helpers.TestConstants;
import cloud.benchflow.testmanager.models.BenchFlowTestModel;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowTestModelDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static cloud.benchflow.testmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER_REGEX;
import static cloud.benchflow.testmanager.helpers.TestConstants.INVALID_BENCHFLOW_TEST_ID;
import static cloud.benchflow.testmanager.helpers.TestConstants.VALID_BENCHFLOW_TEST_ID;
import static cloud.benchflow.testmanager.helpers.TestConstants.VALID_BENCHFLOW_TEST_NAME;
import static cloud.benchflow.testmanager.models.BenchFlowTestModel.BenchFlowTestState.COMPLETED;
import static cloud.benchflow.testmanager.models.BenchFlowTestModel.BenchFlowTestState.RUNNING;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class BenchFlowTestResourceTest {

    private BenchFlowTestModelDAO daoMock = Mockito.mock(BenchFlowTestModelDAO.class);
    private BenchFlowTestResource resource;
    private ChangeBenchFlowTestStateRequest request;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Before
    public void setUp() throws Exception {

        resource = new BenchFlowTestResource(daoMock);
        request = new ChangeBenchFlowTestStateRequest();
    }

    @Test
    public void changeBenchFlowTestState() throws Exception {

        Mockito.doReturn(RUNNING).when(daoMock).setTestState(VALID_BENCHFLOW_TEST_ID, RUNNING);
        Mockito.doReturn(COMPLETED).when(daoMock).setTestState(VALID_BENCHFLOW_TEST_ID, COMPLETED);

        request.setState(RUNNING);

        String[] testIDArray = VALID_BENCHFLOW_TEST_ID.split(MODEL_ID_DELIMITER_REGEX);

        String username = testIDArray[0];
        String testName = testIDArray[1];
        String testNumber = testIDArray[2];

        ChangeBenchFlowTestStateResponse response = resource.changeBenchFlowTestState(username, testName, testNumber, request);

        Assert.assertNotNull(response);
        Assert.assertEquals(RUNNING, response.getState());


        request.setState(COMPLETED);

        response = resource.changeBenchFlowTestState(username, testName, testNumber, request);

        Assert.assertNotNull(response);
        Assert.assertEquals(COMPLETED, response.getState());

    }

    @Test
    public void changeBenchFlowTestStateInvalid() throws Exception {

        request.setState(RUNNING);

        Mockito.doThrow(BenchFlowTestIDDoesNotExistException.class).when(daoMock).setTestState(
                VALID_BENCHFLOW_TEST_ID,
                RUNNING);

        exception.expect(InvalidBenchFlowTestIDWebException.class);

        String[] testIDArray = VALID_BENCHFLOW_TEST_ID.split(MODEL_ID_DELIMITER_REGEX);

        String username = testIDArray[0];
        String testName = testIDArray[1];
        String testNumber = testIDArray[2];

        resource.changeBenchFlowTestState(username, testName, testNumber, request);

    }

    @Test
    public void getBenchFlowTestStatusInValid() throws Exception {

        String testID = INVALID_BENCHFLOW_TEST_ID;

        doThrow(BenchFlowTestIDDoesNotExistException.class).when(daoMock).getTestModel(testID);

        exception.expect(InvalidBenchFlowTestIDWebException.class);

        String[] testIDArray = INVALID_BENCHFLOW_TEST_ID.split(MODEL_ID_DELIMITER_REGEX);

        String username = testIDArray[0];
        String testName = testIDArray[1];
        String testNumber = testIDArray[2];

        resource.getBenchFlowTestStatus(username, testName, testNumber);

        verify(daoMock, times(1)).getTestModel(testID);

    }

    @Test
    public void getBenchFlowTestStatusValid() throws Exception {

        String benchFlowTestName = TestConstants.VALID_BENCHFLOW_TEST_NAME;

        String expectedTestID = TestConstants.TEST_USER_NAME + BenchFlowConstants.MODEL_ID_DELIMITER + benchFlowTestName + BenchFlowConstants.MODEL_ID_DELIMITER + 1;

        doReturn(new BenchFlowTestModel(TestConstants.TEST_USER, benchFlowTestName, 1)).when(
                daoMock).getTestModel(expectedTestID);

        String[] testIDArray = expectedTestID.split(MODEL_ID_DELIMITER_REGEX);

        String username = testIDArray[0];
        String testName = testIDArray[1];
        String testNumber = testIDArray[2];

        BenchFlowTestModel response = resource.getBenchFlowTestStatus(username, testName, testNumber);

        verify(daoMock, times(1)).getTestModel(expectedTestID);

        // TODO - decide what status should contain and make assertions accordingly

        Assert.assertNotNull(response);
        Assert.assertEquals(expectedTestID, response.getId());

    }
}