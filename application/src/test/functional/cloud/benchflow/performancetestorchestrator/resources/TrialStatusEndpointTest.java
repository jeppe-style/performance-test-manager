package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.request.SubmitTrialStatusRequest;
import cloud.benchflow.performancetestorchestrator.helpers.TestConstants;
import cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceExperimentModelDAO;
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
 *         created on 27.02.17.
 */
public class TrialStatusEndpointTest {

    private static PerformanceExperimentModelDAO experimentModelDAOMock = Mockito.mock(PerformanceExperimentModelDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TrialStatusResource(experimentModelDAOMock))
            .build();


    @Test
    public void submitTrialStatus() throws Exception {

        String trialID = TestConstants.VALID_TRIAL_ID;

        String target = TrialStatusResource.ROOT_PATH + trialID + "/status";

        SubmitTrialStatusRequest statusRequest = new SubmitTrialStatusRequest(PerformanceExperimentModel.TrialStatus.SUCCESS);

        Response response = resources.client()
                .target(target)
                .request()
                .post(Entity.entity(statusRequest, MediaType.APPLICATION_JSON));

        Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

    }

}