package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.response.GetBenchFlowTestStatusResponse;
import cloud.benchflow.testmanager.helpers.TestConstants;
import cloud.benchflow.testmanager.models.BenchFlowTestModel;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowTestModelDAO;
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
public class BenchFlowTestStatusEndpointTest {

    private static BenchFlowTestModelDAO testModelDAOMock = Mockito.mock(BenchFlowTestModelDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new BenchFlowTestStatusResource(testModelDAOMock))
            .build();

    @Test
    public void getBenchFlowTestStatus() throws Exception {

        String testID = TestConstants.VALID_TEST_ID;

        Mockito.doReturn(new BenchFlowTestModel(TestConstants.TEST_USER, TestConstants.VALID_BENCHFLOW_TEST_NAME, TestConstants.VALID_TEST_NUMBER))
                .when(testModelDAOMock)
                .getTestModel(testID);

        String target = BenchFlowTestStateResource.ROOT_PATH + testID + "/status";

        Response response = resources.client()
                .target(target)
                .request(MediaType.APPLICATION_JSON)
                .get();

        GetBenchFlowTestStatusResponse statusResponse = response.readEntity(GetBenchFlowTestStatusResponse.class);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertNotNull(statusResponse);
        // TODO - adjust when status object is decided
        Assert.assertEquals(testID, statusResponse.getTestID());

    }

}