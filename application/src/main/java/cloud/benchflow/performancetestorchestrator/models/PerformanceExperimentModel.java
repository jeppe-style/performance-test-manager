package cloud.benchflow.performancetestorchestrator.models;

import cloud.benchflow.performancetestorchestrator.services.external.PerformanceExperimentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloud.benchflow.performancetestorchestrator.services.external.PerformanceExperimentManager.TrialStatus.NOT_COMPLETED;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class PerformanceExperimentModel implements Runnable {

    private Logger logger = LoggerFactory.getLogger(PerformanceExperimentModel.class.getName());

    private int numTrials = 5;

    private PerformanceExperimentManager experimentManager = new PerformanceExperimentManager();

    @Override
    public void run() {

        // run first trial of experiment
        // TODO - pass real data
        PerformanceExperimentManager.RunExperimentResult result = experimentManager.runExperiment(null);

        String experimentID = result.experimentID;
        int trialID = result.trialID;

        logger.info("running experiment " + experimentID + " with trial " + trialID);

        while (trialID < numTrials) {

            //check if experiment has completed

            PerformanceExperimentManager.TrialStatus trialStatus;

            while ((trialStatus = experimentManager.getStatus(experimentID, trialID)) == NOT_COMPLETED) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            // analyse trial status
            logger.info("analyzing result " + trialStatus.name() + " from experiment " + experimentID + " with trial " + trialID);

            switch (trialStatus) {

                case SUCCESS:
                    // if not not completed
                    if (trialID != numTrials) {
                        trialID = experimentManager.runNewTrial(experimentID);
                        logger.info("running experiment " + experimentID + " with trial " + trialID);
                    }
                    break;

                case ERROR:
                    experimentManager.reRunTrial(experimentID, trialID);
                    logger.info("repeating experiment " + experimentID + " with trial " + trialID);
                    break;

                case FAILURE:
                    experimentManager.abortExperiment(experimentID);
                    logger.info("aborting experiment " + experimentID);
                    break;

                default:
                    break;

            }
        }


    }
}
