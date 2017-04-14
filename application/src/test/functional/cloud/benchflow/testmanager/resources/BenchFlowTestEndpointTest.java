package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.response.RunBenchFlowTestResponse;
import cloud.benchflow.testmanager.archive.TestArchives;
import cloud.benchflow.testmanager.constants.BenchFlowConstants;
import cloud.benchflow.testmanager.models.User;
import cloud.benchflow.testmanager.services.external.MinioService;
import cloud.benchflow.testmanager.services.external.BenchFlowExperimentManagerService;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowExperimentModelDAO;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowTestModelDAO;
import cloud.benchflow.testmanager.services.internal.dao.UserDAO;
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
public class BenchFlowTestEndpointTest {

    // Mocks
    private static ExecutorService executorServiceMock = Mockito.mock(ExecutorService.class);
    private static MinioService minioServiceMock = Mockito.mock(MinioService.class);
    private static BenchFlowTestModelDAO testModelDAOMock = Mockito.mock(BenchFlowTestModelDAO.class);
    private static BenchFlowExperimentModelDAO experimentModelDAOMock = Mockito.mock(
            BenchFlowExperimentModelDAO.class);
    private static UserDAO userDAOMock = Mockito.mock(UserDAO.class);
    private static BenchFlowExperimentManagerService peManagerServiceMock = Mockito.mock(
            BenchFlowExperimentManagerService.class);


    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addProvider(MultiPartFeature.class)
            .addResource(new BenchFlowTestResource(executorServiceMock, minioServiceMock, testModelDAOMock,
                                                     experimentModelDAOMock, userDAOMock, peManagerServiceMock))
            .build();


    @Test
    public void runValidBenchFlowTest() throws Exception {

        String benchFlowTestName = "testNameExample";
        User user = BenchFlowConstants.BENCHFLOW_USER;

        Mockito.doReturn(
                user.getUsername() + BenchFlowConstants.MODEL_ID_DELIMITER + benchFlowTestName + BenchFlowConstants.MODEL_ID_DELIMITER + 1).when(
                testModelDAOMock).addTestModel(benchFlowTestName, user);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("benchFlowTestBundle",
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

        RunBenchFlowTestResponse testResponse = response.readEntity(RunBenchFlowTestResponse.class);

        Assert.assertNotNull(testResponse);
        Assert.assertTrue(testResponse.getTestID().contains(benchFlowTestName));

    }

    @Test
    public void runInvalidArchiveBenchFlowTest() throws Exception {

        // TODO

    }
}
