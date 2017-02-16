package cloud.benchflow.performancetestorchestrator.helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 14.02.17.
 */
public class TestArchives {

    private static final String VALID_TEST_ARCHIVE_FILENAME = "src/test/resources/data/wfms.camunda.valid.zip";

    private static final String INVALID_TEST_ARCHIVE_FILENAME = "src/test/resources/data/wfms.camunda.invalid.zip";

    public static final String VALID_PERFORMANCE_TEST_ID = "benchflow.testNameExample.1.1.1";
    public static final String INVALID_PERFORMANCE_TEST_ID = "benchflow.invalid.1.1.1";
    public static final String PERFORMANCE_EXPERIMENT_ID = "benchflow.experimentNameExample.1.1.1";



    public static InputStream getValidTestArchive() throws FileNotFoundException {

        return new FileInputStream(VALID_TEST_ARCHIVE_FILENAME);

    }

    public static InputStream getInValidTestArchive() throws FileNotFoundException {

        return new FileInputStream(INVALID_TEST_ARCHIVE_FILENAME);

    }

    public static ZipInputStream getValidTestArchiveZip() throws FileNotFoundException {

        return new ZipInputStream(getValidTestArchive());

    }

    public static ZipInputStream getInValidTestArchiveZip() throws FileNotFoundException {

        return new ZipInputStream(getInValidTestArchive());

    }


}
