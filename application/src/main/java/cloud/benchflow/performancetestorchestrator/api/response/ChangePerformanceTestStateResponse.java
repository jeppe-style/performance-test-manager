package cloud.benchflow.performancetestorchestrator.api.response;

import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class ChangePerformanceTestStateResponse {

    @NotNull
    @JsonProperty
    private PerformanceTestModel.PerformanceTestState state;

    public ChangePerformanceTestStateResponse() {
    }

    public ChangePerformanceTestStateResponse(PerformanceTestModel.PerformanceTestState state) {
        this.state = state;
    }

    public void setState(PerformanceTestModel.PerformanceTestState state) {
        this.state = state;
    }

    public PerformanceTestModel.PerformanceTestState getState() {
        return state;
    }
}
