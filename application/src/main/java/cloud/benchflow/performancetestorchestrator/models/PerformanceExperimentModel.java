package cloud.benchflow.performancetestorchestrator.models;

import org.mongodb.morphia.annotations.Embedded;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
@Embedded
public class PerformanceExperimentModel {

    public enum TrialStatus { RUNNING, SUCCESS, FAILURE, ERROR }

    private String performanceExperimentID;

    private Map<String, TrialStatus> trials = new HashMap<>();

    PerformanceExperimentModel(){
        // Empty constructor for MongoDB + Morphia
    }

    PerformanceExperimentModel(String performanceExperimentID) {
        this.performanceExperimentID = performanceExperimentID;
    }

    void setTrialStatus(String trialID, TrialStatus status) {

        trials.put(trialID, status);

    }

    TrialStatus getTrialStatus(String trialID) {

        return trials.get(trialID);

    }
}
