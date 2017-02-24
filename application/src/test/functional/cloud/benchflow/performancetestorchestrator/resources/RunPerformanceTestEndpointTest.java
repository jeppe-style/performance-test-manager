package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.services.external.MinioService;
import cloud.benchflow.performancetestorchestrator.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceExperimentModelDAO;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceTestModelDAO;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.UserDAO;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 20.02.17.
 */
public class RunPerformanceTestEndpointTest {

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
            .addResource(new RunPerformanceTestResource(executorServiceMock, minioServiceMock, testModelDAOMock,
                                                        experimentModelDAOMock, userDAOMock, peManagerServiceMock))
            .build();


    @Test
    public void runPerformanceTest() throws Exception {

        // TODO

//        InputStream testArchive = TestArchives.getValidTestArchive();
//
//        Response response = resources.client().target(RunPerformanceTestResource.ROOT_PATH).request().post(
//                Entity.entity(testArchive, MediaType.MULTIPART_FORM_DATA));
//
//        System.out.println(response.getStatus());

    }
}
