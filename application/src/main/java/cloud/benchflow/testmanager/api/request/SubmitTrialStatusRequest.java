package cloud.benchflow.testmanager.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

import static cloud.benchflow.testmanager.models.BenchFlowExperimentModel.TrialStatus;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 16.02.17.
 */
public class SubmitTrialStatusRequest {

    @NotNull
    @JsonProperty
    private TrialStatus status;

    public SubmitTrialStatusRequest() {
    }

    public SubmitTrialStatusRequest(TrialStatus status) {
        this.status = status;
    }

    public TrialStatus getStatus() {
        return status;
    }

    public void setStatus(TrialStatus status) {
        this.status = status;
    }
}
