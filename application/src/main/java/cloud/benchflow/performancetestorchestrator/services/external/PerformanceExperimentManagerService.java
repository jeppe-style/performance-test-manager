package cloud.benchflow.performancetestorchestrator.services.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class PerformanceExperimentManagerService {

    private Logger logger = LoggerFactory.getLogger(PerformanceExperimentManagerService.class.getSimpleName());

    public void runPerformanceExperiment(String performanceExperimentID) {

        logger.info("runPerformanceExperiment: " + performanceExperimentID);

        // TODO

    }

    public void abortPerformanceExperiment(String performanceExperimentID) {

        logger.info("abortPerformanceExperiment: " + performanceExperimentID);

        // TODO
    }



}
