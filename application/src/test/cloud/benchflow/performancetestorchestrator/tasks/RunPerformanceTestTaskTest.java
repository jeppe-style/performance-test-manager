package cloud.benchflow.performancetestorchestrator.tasks;

import cloud.benchflow.performancetestorchestrator.helpers.TestArchives;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.zip.ZipInputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class RunPerformanceTestTaskTest {


    private RunPerformanceTestTask task;

    @Before
    public void setUp() throws Exception {

        ZipInputStream zipArchive = TestArchives.getValidTestArchiveZip();

        task = new RunPerformanceTestTask(zipArchive);

    }

    @Test
    public void getPerformanceTestID() throws Exception {

        Assert.assertEquals(TestArchives.validPerformanceTestID, task.getPerformanceTestID());

    }

    @Test
    public void run() throws Exception {

    }

}