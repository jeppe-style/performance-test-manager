package cloud.benchflow.performancetestmanager.api.response;

import cloud.benchflow.performancetestmanager.models.BenchFlowTestModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class ChangePerformanceTestStateResponse {

    @NotNull
    @JsonProperty
    private BenchFlowTestModel.PerformanceTestState state;

    public ChangePerformanceTestStateResponse() {
    }

    public ChangePerformanceTestStateResponse(BenchFlowTestModel.PerformanceTestState state) {
        this.state = state;
    }

    public void setState(BenchFlowTestModel.PerformanceTestState state) {
        this.state = state;
    }

    public BenchFlowTestModel.PerformanceTestState getState() {
        return state;
    }
}
