package cloud.benchflow.performancetestorchestrator.services.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class MinioManager {

    private static Logger logger = LoggerFactory.getLogger(MinioManager.class.getSimpleName());

    public void savePerformanceTestArchive(InputStream performanceTestArchive) {

        // TODO - actually save the archive to minio

        logger.info("saving performance-test-archive to minio");

    }

}
