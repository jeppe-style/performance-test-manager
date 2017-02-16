package cloud.benchflow.performancetestorchestrator.tasks;

import cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestModelDAO;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class SubmitTrialStatusTask implements Runnable {

    private final String performanceTestID;
    private final String performanceExperimentID;
    private final String trialID;
    private final PerformanceExperimentModel.TrialStatus trialStatus;

    private PerformanceTestModelDAO dao;

    public SubmitTrialStatusTask(String performanceTestID, String performanceExperimentID, String trialID, PerformanceExperimentModel.TrialStatus trialStatus, PerformanceTestModelDAO dao) {

        this.performanceTestID = performanceTestID;
        this.performanceExperimentID = performanceExperimentID;
        this.trialID = trialID;
        this.trialStatus = trialStatus;

        this.dao = dao;
    }

    @Override
    public void run() {

        // TODO - get PerformanceExperimentModel from DAO

        // TODO - update the status

    }
}
