package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.response.GetPerformanceTestStatusResponse;
import cloud.benchflow.performancetestorchestrator.helpers.TestConstants;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceTestModelDAO;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 27.02.17.
 */
public class PerformanceTestStatusEndpointTest {

    private static PerformanceTestModelDAO testModelDAOMock = Mockito.mock(PerformanceTestModelDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new PerformanceTestStatusResource(testModelDAOMock))
            .build();

    @Test
    public void getPerformanceTestStatus() throws Exception {

        String performanceTestID = TestConstants.VALID_TEST_ID;

        Mockito.doReturn(new PerformanceTestModel(TestConstants.TEST_USER, TestConstants.VALID_PERFORMANCE_TEST_NAME, TestConstants.VALID_TEST_NUMBER))
                .when(testModelDAOMock)
                .getPerformanceTestModel(performanceTestID);

        String target = PerformanceTestStateResource.ROOT_PATH + performanceTestID + "/status";

        Response response = resources.client()
                .target(target)
                .request(MediaType.APPLICATION_JSON)
                .get();

        GetPerformanceTestStatusResponse statusResponse = response.readEntity(GetPerformanceTestStatusResponse.class);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertNotNull(statusResponse);
        // TODO - adjust when status object is decided
        Assert.assertEquals(performanceTestID, statusResponse.getPerformanceTestID());

    }

}