package cloud.benchflow.performancetestmanager.tasks;

import cloud.benchflow.performancetestmanager.archive.TestArchives;
import cloud.benchflow.performancetestmanager.helpers.TestConstants;
import cloud.benchflow.performancetestmanager.services.external.MinioService;
import cloud.benchflow.performancetestmanager.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceExperimentModelDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;

import static cloud.benchflow.performancetestmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class RunPerformanceTestTaskTest {


    private RunPerformanceTestTask task;
    private String performanceTestID;

    // Mocks
    private MinioService minioServiceMock = Mockito.mock(MinioService.class);
    private PerformanceExperimentManagerService peManagerServiceMock = Mockito.mock(PerformanceExperimentManagerService.class);
    private PerformanceExperimentModelDAO experimentModelDAOMock = Mockito.mock(PerformanceExperimentModelDAO.class);

    @Before
    public void setUp() throws Exception {

        performanceTestID = TestConstants.TEST_USER_NAME + MODEL_ID_DELIMITER + TestConstants.VALID_PERFORMANCE_TEST_NAME + MODEL_ID_DELIMITER + 1;

        task = new RunPerformanceTestTask(performanceTestID, minioServiceMock,
                                          peManagerServiceMock, experimentModelDAOMock, TestArchives.getValidTestArchiveZip());

    }

    @Test
    public void run() throws Exception {

        String experimentID = performanceTestID + MODEL_ID_DELIMITER + "1";

        Mockito.doReturn(experimentID).when(experimentModelDAOMock).addPerformanceExperiment(performanceTestID);

        task.run();

        // check: save performanceTestArchive to Minio
        verify(minioServiceMock, times(1))
                .savePerformanceTestDefinition(Mockito.eq(performanceTestID), Mockito.any(InputStream.class));

        verify(minioServiceMock, times(1))
                .savePerformanceTestDeploymentDescriptor(Mockito.eq(performanceTestID), Mockito.any(InputStream.class));

        verify(minioServiceMock, times(TestArchives.BPMN_MODELS_COUNT))
                .savePerformanceTestBPMNModel(Mockito.eq(performanceTestID), Mockito.anyString(), Mockito.any(InputStream.class));

        // check PE was saved to DAO
        verify(experimentModelDAOMock, times(1)).addPerformanceExperiment(performanceTestID);

        // check: generate the experiment definition save to minio
        verify(minioServiceMock, times(1))
                .savePerformanceExperimentDefinition(Mockito.eq(experimentID),
                                                  Mockito.any(InputStream.class));

        // run PE on PEManager
        verify(peManagerServiceMock, times(1)).runPerformanceExperiment(experimentID);

    }

}