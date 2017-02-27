package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.request.ChangePerformanceTestStateRequest;
import cloud.benchflow.performancetestorchestrator.api.response.ChangePerformanceTestStateResponse;
import cloud.benchflow.performancetestorchestrator.helpers.TestConstants;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceTestModelDAO;
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
public class PerformanceTestStateEndpointTest {

    private static PerformanceTestModelDAO testModelDAOMock = Mockito.mock(PerformanceTestModelDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new PerformanceTestStateResource(testModelDAOMock))
            .build();

    @Test
    public void changePerformanceTestState() throws Exception {

        PerformanceTestModel.PerformanceTestState state = PerformanceTestModel.PerformanceTestState.COMPLETED;
        String performanceTestID = TestConstants.VALID_TEST_ID;

        Mockito.doReturn(state).when(testModelDAOMock).setPerformanceTestState(performanceTestID, state);

        ChangePerformanceTestStateRequest stateRequest = new ChangePerformanceTestStateRequest(state);

        String target = PerformanceTestStateResource.ROOT_PATH + performanceTestID + "/state";

        Response response = resources.client()
                .target(target)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(stateRequest, MediaType.APPLICATION_JSON));

        ChangePerformanceTestStateResponse stateResponse = response.readEntity(ChangePerformanceTestStateResponse.class);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertNotNull(stateResponse);
        Assert.assertEquals(state, stateResponse.getState());

    }

    @Test
    public void invalidPerformanceTestState() throws Exception {

        // TODO

    }
}