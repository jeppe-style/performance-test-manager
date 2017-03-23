package cloud.benchflow.performancetestmanager.resources;

import cloud.benchflow.performancetestmanager.api.response.RunPerformanceTestResponse;
import cloud.benchflow.performancetestmanager.archive.TestArchives;
import cloud.benchflow.performancetestmanager.constants.BenchFlowConstants;
import cloud.benchflow.performancetestmanager.models.User;
import cloud.benchflow.performancetestmanager.services.external.MinioService;
import cloud.benchflow.performancetestmanager.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceExperimentModelDAO;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceTestModelDAO;
import cloud.benchflow.performancetestmanager.services.internal.dao.UserDAO;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.concurrent.ExecutorService;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 20.02.17.
 */
public class PerformanceTestEndpointTest {

    // Mocks
    private static ExecutorService executorServiceMock = Mockito.mock(ExecutorService.class);
    private static MinioService minioServiceMock = Mockito.mock(MinioService.class);
    private static PerformanceTestModelDAO testModelDAOMock = Mockito.mock(PerformanceTestModelDAO.class);
    private static PerformanceExperimentModelDAO experimentModelDAOMock = Mockito.mock(
            PerformanceExperimentModelDAO.class);
    private static UserDAO userDAOMock = Mockito.mock(UserDAO.class);
    private static PerformanceExperimentManagerService peManagerServiceMock = Mockito.mock(
            PerformanceExperimentManagerService.class);


    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addProvider(MultiPartFeature.class)
            .addResource(new BenchFlowTestResource(executorServiceMock, minioServiceMock, testModelDAOMock,
                                                     experimentModelDAOMock, userDAOMock, peManagerServiceMock))
            .build();


    @Test
    public void runValidPerformanceTest() throws Exception {

        String performanceTestName = "testNameExample";
        User user = BenchFlowConstants.BENCH_FLOW_USER;

        Mockito.doReturn(
                user.getUsername() + BenchFlowConstants.MODEL_ID_DELIMITER + performanceTestName + BenchFlowConstants.MODEL_ID_DELIMITER + 1).when(
                testModelDAOMock).addPerformanceTestModel(performanceTestName, user);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("performanceTestBundle",
                                                                 TestArchives.getValidTestArchiveFile(),
                                                                 MediaType.APPLICATION_OCTET_STREAM_TYPE);

        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(fileDataBodyPart);

        Response response = resources.client()
                .target(BenchFlowTestResource.ROOT_PATH)
                .register(MultiPartFeature.class)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        RunPerformanceTestResponse testResponse = response.readEntity(RunPerformanceTestResponse.class);

        Assert.assertNotNull(testResponse);
        Assert.assertTrue(testResponse.getPerformanceTestID().contains(performanceTestName));

    }

    @Test
    public void runInvalidArchivePerformanceTest() throws Exception {

        // TODO

    }
}
