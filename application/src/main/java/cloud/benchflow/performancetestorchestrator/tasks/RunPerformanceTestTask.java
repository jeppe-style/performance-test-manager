package cloud.benchflow.performancetestorchestrator.tasks;

import cloud.benchflow.performancetestorchestrator.definitions.PerformanceTestDefinition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class RunPerformanceTestTask implements Runnable {

    private final String performanceTestID;
    private final ZipInputStream performanceTestArchive;
    private final PerformanceTestDefinition performanceTestDefinition;

    public RunPerformanceTestTask(ZipInputStream performanceTestArchive) {

        this.performanceTestArchive = performanceTestArchive;

        // get the PerformanceTestID from the archive
        performanceTestDefinition = new PerformanceTestDefinition(extractPerformanceTestDefintion());

        performanceTestID = performanceTestDefinition.getID();

    }

    private String extractPerformanceTestDefintion() {

        BiPredicate<ZipEntry, String> isExpConfigEntry = (e, s) -> e.getName().endsWith(s);
        Predicate<ZipEntry> isExpConfig = e -> isExpConfigEntry.test(e, "/benchflow-test.yml");

        ZipEntry zipEntry;

        try {

            while ((zipEntry = performanceTestArchive.getNextEntry()) != null) {

                if (isExpConfig.test(zipEntry)) {

                    return readZipEntry(performanceTestArchive);

                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Reads the data from the current ZipEntry in a ZipInputStream to a String.
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String readZipEntry(ZipInputStream inputStream) throws IOException {

        byte[] buffer = new byte[1024];

        int len;

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while ((len = inputStream.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        return out.toString(StandardCharsets.UTF_8.name());

    }

    public String getPerformanceTestID() {
        return performanceTestID;
    }

    @Override
    public void run() {

        // TODO - save performanceTestArchive to Minio

        // TODO - create PT Model and save to DAO

        // TODO - generate the experiment definition and performanceExperimentArchive and save to minio

        // TODO - run PE on PEManager

    }
}
