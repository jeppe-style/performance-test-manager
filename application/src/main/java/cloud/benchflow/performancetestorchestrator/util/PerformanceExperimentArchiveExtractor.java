package cloud.benchflow.performancetestorchestrator.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class PerformanceExperimentArchiveExtractor {

    public static String PT_DEFINITION_FILE_NAME = "benchflow-test.yml";


    /**
     *
     * @param performanceTestArchive
     * @return
     * @throws IOException
     */
    public static String extractPerformanceTestDefinition(ZipInputStream performanceTestArchive) throws IOException {

        BiPredicate<ZipEntry, String> isExpConfigEntry = (e, s) -> e.getName().endsWith(s);
        Predicate<ZipEntry> isExpConfig = e -> isExpConfigEntry.test(e, "/" + PT_DEFINITION_FILE_NAME);

        ZipEntry zipEntry;

        while ((zipEntry = performanceTestArchive.getNextEntry()) != null) {

            if (isExpConfig.test(zipEntry)) {
                return readZipEntry(performanceTestArchive);
            }
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
    private static String readZipEntry(ZipInputStream inputStream) throws IOException {

        byte[] buffer = new byte[1024];

        int len;

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while ((len = inputStream.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        return out.toString(StandardCharsets.UTF_8.name());

    }
}
