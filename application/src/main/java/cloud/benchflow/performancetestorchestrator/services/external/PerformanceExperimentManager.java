package cloud.benchflow.performancetestorchestrator.services.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Random;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class PerformanceExperimentManager {

    public enum TrialStatus { RUNNING, SUCCESS, ERROR, FAILURE }

    private Logger logger = LoggerFactory.getLogger(PerformanceExperimentManager.class.getSimpleName());

    private int trialID = 1;

    public class RunExperimentResult {
        public String experimentID;
        public int trialID;
    }

    public RunExperimentResult runExperiment(InputStream expArchive) {

        logger.info("running new experiment");

        // TODO

        int id = Math.abs((new Random()).nextInt());

        RunExperimentResult result = new RunExperimentResult();
        result.experimentID = "test-experiment-" + id;
        result.trialID = trialID++;

        return result;

    }

    public int runNewTrial(String experimentID) {

        logger.info("running new trial on " + experimentID);

        // TODO

        return trialID++;

    }

    public void reRunTrial(String experimentID, int trialID) {

        logger.info("re-running " + experimentID + " with trial " + trialID);
    }

    public TrialStatus getStatus(String experimentID, int trialID) {

        logger.info("getting status for " + experimentID + " trial " + trialID);

        // TODO
        double value = (new Random()).nextDouble();

        if (value < 0.3) {

            return TrialStatus.RUNNING;

        } else if (value < 0.9) {

            return TrialStatus.SUCCESS;

        } else if (value < 0.95) {

            return TrialStatus.ERROR;

        } else {

            return TrialStatus.FAILURE;

        }

    }

    public void abortExperiment(String experimentID) {

        logger.info("aborting experiment " + experimentID);

    }



}
