package cloud.benchflow.performancetestorchestrator.tasks;

import cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class SubmitTrialStatusTask implements Runnable {

    private final String performanceTestID;
    private final String performanceExperimentID;
    private final String trialID;
    private final PerformanceExperimentModel.TrialStatus trialStatus;

    public SubmitTrialStatusTask(String performanceTestID, String performanceExperimentID, String trialID, PerformanceExperimentModel.TrialStatus trialStatus) {
        this.performanceTestID = performanceTestID;
        this.performanceExperimentID = performanceExperimentID;
        this.trialID = trialID;
        this.trialStatus = trialStatus;
    }

    @Override
    public void run() {

        // TODO - get PerformanceExperimentModel from DAO

        // TODO - update the status

    }
}
