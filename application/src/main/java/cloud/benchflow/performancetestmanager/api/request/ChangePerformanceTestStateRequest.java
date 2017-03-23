package cloud.benchflow.performancetestmanager.api.request;

import cloud.benchflow.performancetestmanager.models.BenchFlowTestModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 16.02.17.
 */
public class ChangePerformanceTestStateRequest {

    @NotNull
    @JsonProperty
    BenchFlowTestModel.PerformanceTestState state;

    public ChangePerformanceTestStateRequest() {
    }

    public ChangePerformanceTestStateRequest(BenchFlowTestModel.PerformanceTestState state) {
        this.state = state;
    }

    public BenchFlowTestModel.PerformanceTestState getState() {
        return state;
    }

    public void setState(BenchFlowTestModel.PerformanceTestState state) {
        this.state = state;
    }
}
