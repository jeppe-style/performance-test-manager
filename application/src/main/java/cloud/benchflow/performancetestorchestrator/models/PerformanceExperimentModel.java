package cloud.benchflow.performancetestorchestrator.models;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class PerformanceExperimentModel {

    public enum TrialStatus { RUNNING, SUCCESS, FAILURE, ERROR }

    private final String performanceExperimentID;
    private Map<String, TrialStatus> trials;

    public PerformanceExperimentModel(String performanceExperimentID) {
        this.performanceExperimentID = performanceExperimentID;

        this.trials = new HashMap<>();
    }
}
