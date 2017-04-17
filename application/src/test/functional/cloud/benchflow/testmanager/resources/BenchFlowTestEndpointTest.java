package cloud.benchflow.testmanager.resources;

import cloud.benchflow.faban.client.responses.RunStatus;
import cloud.benchflow.testmanager.api.request.ChangeBenchFlowTestStateRequest;
import cloud.benchflow.testmanager.api.response.ChangeBenchFlowTestStateResponse;
import cloud.benchflow.testmanager.constants.BenchFlowConstants;
import cloud.benchflow.testmanager.helpers.TestConstants;
import cloud.benchflow.testmanager.models.BenchFlowExperimentModel;
import cloud.benchflow.testmanager.models.BenchFlowTestModel;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowTestModelDAO;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 26.02.17.
 */
public class BenchFlowTestEndpointTest {

    private static BenchFlowTestModelDAO testModelDAOMock = Mockito.mock(BenchFlowTestModelDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new BenchFlowTestResource(testModelDAOMock))
            .build();

    @Test
    public void changeBenchFlowTestState() throws Exception {

        BenchFlowTestModel.BenchFlowTestState state = BenchFlowTestModel.BenchFlowTestState.COMPLETED;
        String testID = TestConstants.VALID_TEST_ID;

        Mockito.doReturn(state).when(testModelDAOMock).setTestState(testID, state);

        ChangeBenchFlowTestStateRequest stateRequest = new ChangeBenchFlowTestStateRequest(state);

        Response response = resources.client()
                .target(BenchFlowConstants.getPathFromBenchFlowID(testID))
                .path(BenchFlowTestResource.STATE_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(stateRequest, MediaType.APPLICATION_JSON));

        ChangeBenchFlowTestStateResponse stateResponse = response.readEntity(ChangeBenchFlowTestStateResponse.class);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertNotNull(stateResponse);
        Assert.assertEquals(state, stateResponse.getState());

    }

    @Test
    public void invalidBenchFlowTestState() throws Exception {

        // TODO

    }

    @Test
    public void getBenchFlowTestStatus() throws Exception {

        String testID = TestConstants.VALID_TEST_ID;

        BenchFlowTestModel testModel = new BenchFlowTestModel(TestConstants.TEST_USER, TestConstants.VALID_BENCHFLOW_TEST_NAME, TestConstants.VALID_TEST_NUMBER);
        testModel.setState(BenchFlowTestModel.BenchFlowTestState.RUNNING);
        BenchFlowExperimentModel experimentModel = new BenchFlowExperimentModel(testModel.getId(), 1);
        testModel.addExperimentModel(experimentModel);
        experimentModel.setTrialStatus(1, RunStatus.Code.COMPLETED);
        testModel.addExperimentModel(experimentModel);

        Mockito.doReturn(testModel)
                .when(testModelDAOMock)
                .getTestModel(testID);

        Response response = resources.client()
                .target(BenchFlowConstants.getPathFromBenchFlowID(testID))
                .path(BenchFlowTestResource.STATUS_PATH)
                .request(MediaType.APPLICATION_JSON)
                .get();

        BenchFlowTestModel statusResponse = response.readEntity(BenchFlowTestModel.class);


        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertNotNull(statusResponse);
        // TODO - adjust when status object is decided
        Assert.assertEquals(testID, statusResponse.getId());

    }
}