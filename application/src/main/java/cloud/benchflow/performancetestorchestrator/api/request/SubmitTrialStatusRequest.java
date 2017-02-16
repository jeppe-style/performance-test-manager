package cloud.benchflow.performancetestorchestrator.api.request;

import cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel;

import javax.validation.constraints.NotNull;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 16.02.17.
 */
public class SubmitTrialStatusRequest {

    @NotNull
    PerformanceExperimentModel.TrialStatus status;

    public PerformanceExperimentModel.TrialStatus getStatus() {
        return status;
    }

    public void setStatus(PerformanceExperimentModel.TrialStatus status) {
        this.status = status;
    }
}
