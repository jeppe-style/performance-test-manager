package cloud.benchflow.testmanager.api.response;

import cloud.benchflow.testmanager.models.BenchFlowTestModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class ChangeBenchFlowTestStateResponse {

    @NotNull
    @JsonProperty
    private BenchFlowTestModel.BenchFlowTestState state;

    public ChangeBenchFlowTestStateResponse() {
    }

    public ChangeBenchFlowTestStateResponse(BenchFlowTestModel.BenchFlowTestState state) {
        this.state = state;
    }

    public void setState(BenchFlowTestModel.BenchFlowTestState state) {
        this.state = state;
    }

    public BenchFlowTestModel.BenchFlowTestState getState() {
        return state;
    }
}
