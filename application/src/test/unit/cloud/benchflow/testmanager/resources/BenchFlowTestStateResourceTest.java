package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.request.ChangeBenchFlowTestStateRequest;
import cloud.benchflow.testmanager.api.response.ChangeBenchFlowTestStateResponse;
import cloud.benchflow.testmanager.exceptions.BenchFlowTestIDDoesNotExistException;
import cloud.benchflow.testmanager.exceptions.web.InvalidBenchFlowTestIDWebException;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowTestModelDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static cloud.benchflow.testmanager.helpers.TestConstants.VALID_BENCHFLOW_TEST_NAME;
import static cloud.benchflow.testmanager.models.BenchFlowTestModel.BenchFlowTestState.COMPLETED;
import static cloud.benchflow.testmanager.models.BenchFlowTestModel.BenchFlowTestState.RUNNING;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class BenchFlowTestStateResourceTest {

    private BenchFlowTestModelDAO daoMock = Mockito.mock(BenchFlowTestModelDAO.class);
    private BenchFlowTestStateResource resource;
    private ChangeBenchFlowTestStateRequest request;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Before
    public void setUp() throws Exception {

        resource = new BenchFlowTestStateResource(daoMock);
        request = new ChangeBenchFlowTestStateRequest();
    }

    @Test
    public void changeBenchFlowTestState() throws Exception {

        Mockito.doReturn(RUNNING).when(daoMock).setTestState(VALID_BENCHFLOW_TEST_NAME, RUNNING);
        Mockito.doReturn(COMPLETED).when(daoMock).setTestState(VALID_BENCHFLOW_TEST_NAME, COMPLETED);

        request.setState(RUNNING);

        ChangeBenchFlowTestStateResponse response = resource.changeBenchFlowTestState(VALID_BENCHFLOW_TEST_NAME,
                                                                                          request);
        Assert.assertNotNull(response);
        Assert.assertEquals(RUNNING, response.getState());


        request.setState(COMPLETED);

        response = resource.changeBenchFlowTestState(VALID_BENCHFLOW_TEST_NAME, request);

        Assert.assertNotNull(response);
        Assert.assertEquals(COMPLETED, response.getState());

    }

    @Test
    public void changeBenchFlowTestStateInvalid() throws Exception {

        request.setState(RUNNING);

        Mockito.doThrow(BenchFlowTestIDDoesNotExistException.class).when(daoMock).setTestState(
                VALID_BENCHFLOW_TEST_NAME,
                RUNNING);

        exception.expect(InvalidBenchFlowTestIDWebException.class);

        resource.changeBenchFlowTestState(VALID_BENCHFLOW_TEST_NAME, request);

    }
}