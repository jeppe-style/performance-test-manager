package cloud.benchflow.performancetestmanager.resources;

import cloud.benchflow.performancetestmanager.api.request.ChangePerformanceTestStateRequest;
import cloud.benchflow.performancetestmanager.api.response.ChangePerformanceTestStateResponse;
import cloud.benchflow.performancetestmanager.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestmanager.exceptions.web.InvalidPerformanceTestIDWebException;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceTestModelDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static cloud.benchflow.performancetestmanager.helpers.TestConstants.VALID_PERFORMANCE_TEST_NAME;
import static cloud.benchflow.performancetestmanager.models.BenchFlowTestModel.PerformanceTestState.COMPLETED;
import static cloud.benchflow.performancetestmanager.models.BenchFlowTestModel.PerformanceTestState.RUNNING;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class PerformanceTestStateResourceTest {

    private PerformanceTestModelDAO daoMock = Mockito.mock(PerformanceTestModelDAO.class);
    private PerformanceTestStateResource resource;
    private ChangePerformanceTestStateRequest request;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Before
    public void setUp() throws Exception {

        resource = new PerformanceTestStateResource(daoMock);
        request = new ChangePerformanceTestStateRequest();
    }

    @Test
    public void changePerformanceTestState() throws Exception {

        Mockito.doReturn(RUNNING).when(daoMock).setPerformanceTestState(VALID_PERFORMANCE_TEST_NAME, RUNNING);
        Mockito.doReturn(COMPLETED).when(daoMock).setPerformanceTestState(VALID_PERFORMANCE_TEST_NAME, COMPLETED);

        request.setState(RUNNING);

        ChangePerformanceTestStateResponse response = resource.changePerformanceTestState(VALID_PERFORMANCE_TEST_NAME,
                                                                                          request);
        Assert.assertNotNull(response);
        Assert.assertEquals(RUNNING, response.getState());


        request.setState(COMPLETED);

        response = resource.changePerformanceTestState(VALID_PERFORMANCE_TEST_NAME, request);

        Assert.assertNotNull(response);
        Assert.assertEquals(COMPLETED, response.getState());

    }

    @Test
    public void changePerformanceTestStateInvalid() throws Exception {

        request.setState(RUNNING);

        Mockito.doThrow(PerformanceTestIDDoesNotExistException.class).when(daoMock).setPerformanceTestState(
                VALID_PERFORMANCE_TEST_NAME,
                RUNNING);

        exception.expect(InvalidPerformanceTestIDWebException.class);

        resource.changePerformanceTestState(VALID_PERFORMANCE_TEST_NAME, request);

    }
}