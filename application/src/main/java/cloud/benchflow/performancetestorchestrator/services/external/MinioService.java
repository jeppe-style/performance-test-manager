package cloud.benchflow.performancetestorchestrator.services.external;

import cloud.benchflow.performancetestorchestrator.definitions.PerformanceExperimentDefinition;
import cloud.benchflow.performancetestorchestrator.definitions.PerformanceTestDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class MinioService {

    private static Logger logger = LoggerFactory.getLogger(MinioService.class.getSimpleName());


    /**
     *
     * @param performanceTestID
     * @param performanceTestArchive
     */
    public void savePerformanceTestArchive(final String performanceTestID, final InputStream performanceTestArchive) {

        logger.info("saving performance-test-archive to minio");

        // TODO - actually save the archive to minio

    }

    /**
     *
     * @param performanceTestID
     * @return
     */
    public PerformanceTestDefinition getPerformanceTestDefinition(final String performanceTestID) {

        // TODO
        return null;
    }

    /**
     *
     *
     * @param performanceTestID
     * @param definition
     */
    public void updatePerformanceTestDefinition(final String performanceTestID, PerformanceTestDefinition definition) {

        // TODO

    }


    /**
     *
     * @param performanceTestID
     * @param performanceExperimentID
     * @param definition
     */
    public void savePerformanceExperimentArchive(final String performanceTestID, final String performanceExperimentID, final PerformanceExperimentDefinition definition) {

        // TODO

    }

}
