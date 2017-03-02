package cloud.benchflow.performancetestmanager.resources;

import cloud.benchflow.performancetestmanager.archive.TestArchives;
import cloud.benchflow.performancetestmanager.services.external.MinioService;
import cloud.benchflow.performancetestmanager.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceExperimentModelDAO;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceTestModelDAO;
import cloud.benchflow.performancetestmanager.services.internal.dao.UserDAO;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.InputStream;
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
            .addResource(new PerformanceTestResource(executorServiceMock, minioServiceMock, testModelDAOMock,
                                                     experimentModelDAOMock, userDAOMock, peManagerServiceMock))
            .build();


    @Test
    public void runPerformanceTest() throws Exception {

        // TODO - ask Vincenzo

        InputStream testArchive = TestArchives.getValidTestArchive();

        Response response = resources.client()
                .target(PerformanceTestResource.ROOT_PATH)
                .register(MultiPartFeature.class)
                .request()
                .post(Entity.entity(testArchive, MediaType.MULTIPART_FORM_DATA));

        System.out.println(response.getStatus());

    }
}
