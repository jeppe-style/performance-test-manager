package cloud.benchflow.performancetestorchestrator.tasks;

import cloud.benchflow.performancetestorchestrator.helpers.TestArchives;
import cloud.benchflow.performancetestorchestrator.services.external.MinioService;
import cloud.benchflow.performancetestorchestrator.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestModelDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class RunPerformanceTestTaskTest {


    private RunPerformanceTestTask task;
    private ZipInputStream zipArchive;

    // Mocks
    private MinioService minioServiceMock = Mockito.mock(MinioService.class);
    private PerformanceTestModelDAO daoMock = Mockito.mock(PerformanceTestModelDAO.class);
    private PerformanceExperimentManagerService peManagerServiceMock = Mockito.mock(PerformanceExperimentManagerService.class);

    @Before
    public void setUp() throws Exception {

        zipArchive = TestArchives.getValidTestArchiveZip();

        task = new RunPerformanceTestTask(zipArchive, minioServiceMock, daoMock, peManagerServiceMock);

        // save to DAO
        verify(daoMock, times(1))
                .addPerformanceTestModel(task.getPerformanceTestID());

    }

    @Test
    public void getPerformanceTestID() throws Exception {

        Assert.assertEquals(TestArchives.VALID_PERFORMANCE_TEST_ID, task.getPerformanceTestID());

    }

    @Test
    public void run() throws Exception {

        String performanceTestID = task.getPerformanceTestID();

        task.run();

        // save performanceTestArchive to Minio
        verify(minioServiceMock, times(1))
                .savePerformanceTestArchive(performanceTestID, zipArchive);

        // generate the experiment definition save to minio
        verify(minioServiceMock, times(1))
                .savePerformanceExperimentArchive(Mockito.eq(performanceTestID),
                                                  Mockito.anyString(),
                                                  Mockito.any(InputStream.class));

        // run PE on PEManager
        verify(peManagerServiceMock, times(1)).runPerformanceExperiment(performanceTestID);

    }

}