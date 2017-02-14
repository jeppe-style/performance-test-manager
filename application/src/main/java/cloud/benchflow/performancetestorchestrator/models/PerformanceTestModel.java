package cloud.benchflow.performancetestorchestrator.models;

import java.util.ArrayList;
import java.util.List;

import static cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel.PerformanceTestState.READY;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class PerformanceTestModel {

    public enum PerformanceTestState { READY, RUNNNING, COMPLETED }

    private final String performanceTestID;
    private PerformanceTestState state;

    private List<PerformanceExperimentModel> experiments;

    public PerformanceTestModel(String performanceTestID) {

        this.performanceTestID = performanceTestID;
        this.state = READY;

        this.experiments = new ArrayList<>();
    }
}
