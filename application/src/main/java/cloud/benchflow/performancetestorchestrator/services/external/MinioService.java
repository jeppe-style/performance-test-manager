package cloud.benchflow.performancetestorchestrator.services.external;

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

        logger.info("savePerformanceTestArchive: " + performanceTestID);

        // TODO

    }

    /**
     *
     * @param performanceTestID
     * @return
     */
    public PerformanceTestDefinition getPerformanceTestDefinition(final String performanceTestID) {

        logger.info("getPerformanceTestDefinition: " + performanceTestID);

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

        logger.info("updatePerformanceTestDefinition: " + performanceTestID);

        // TODO

    }


    /**
     *
     * @param performanceTestID
     * @param performanceExperimentID
     * @param performanceExperimentArchive
     */
    public void savePerformanceExperimentArchive(final String performanceTestID, final String performanceExperimentID, final InputStream performanceExperimentArchive) {

        logger.info("savePerformanceExperimentArchive: " + performanceTestID + "/" + performanceExperimentID);

        // TODO - create the archive based on PT and save

    }

}
