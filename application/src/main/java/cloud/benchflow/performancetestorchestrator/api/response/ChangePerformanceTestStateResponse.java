package cloud.benchflow.performancetestorchestrator.api.response;

import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class ChangePerformanceTestStateResponse {

    private PerformanceTestModel.PerformanceTestState state;

    public ChangePerformanceTestStateResponse(PerformanceTestModel.PerformanceTestState state) {
        this.state = state;
    }

    public PerformanceTestModel.PerformanceTestState getState() {
        return state;
    }
}
