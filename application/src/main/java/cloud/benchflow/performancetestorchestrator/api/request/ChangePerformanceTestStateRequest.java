package cloud.benchflow.performancetestorchestrator.api.request;

import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 16.02.17.
 */
public class ChangePerformanceTestStateRequest {

    @NotNull
    @JsonProperty
    PerformanceTestModel.PerformanceTestState state;

    public ChangePerformanceTestStateRequest() {
    }

    public ChangePerformanceTestStateRequest(PerformanceTestModel.PerformanceTestState state) {
        this.state = state;
    }

    public PerformanceTestModel.PerformanceTestState getState() {
        return state;
    }

    public void setState(PerformanceTestModel.PerformanceTestState state) {
        this.state = state;
    }
}
