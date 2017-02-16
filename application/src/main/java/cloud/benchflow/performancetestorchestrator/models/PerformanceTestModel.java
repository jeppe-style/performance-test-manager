package cloud.benchflow.performancetestorchestrator.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import java.util.HashMap;
import java.util.Map;

import static cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel.PerformanceTestState.READY;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
@Entity
public class PerformanceTestModel {

    // TODO - how to handle concurrency? Only accessed by PerformanceTestModelDAO?

    public enum PerformanceTestState { READY, RUNNNING, COMPLETED }

    // Annotations for MongoDB + Morphia (http://mongodb.github.io/morphia/1.3/guides/annotations/#entity)

    @Id
    private ObjectId id;
    @Indexed
    private String performanceTestID;

    private PerformanceTestState state;

    private Map<String, PerformanceExperimentModel> experiments = new HashMap<>();

    public PerformanceTestModel() {
        // Empty constructor for MongoDB + Morphia
    }

    public PerformanceTestModel(String performanceTestID) {

        this.performanceTestID = performanceTestID;
        this.state = READY;

    }

    public String getPerformanceTestID() {
        return performanceTestID;
    }

    public PerformanceTestState getState() {
        return state;
    }

    public void setState(PerformanceTestState state) {
        this.state = state;
    }

    public void addTrialStatus(String performanceExperimentID, String trialID, PerformanceExperimentModel.TrialStatus status) {

        if (!experiments.containsKey(performanceExperimentID))
            experiments.put(performanceExperimentID, new PerformanceExperimentModel(performanceExperimentID));

        experiments.get(performanceExperimentID)
                .setTrialStatus(trialID, status);

    }

    public PerformanceExperimentModel.TrialStatus getTrialStatus(String performanceExperimentID, String trialID) {

        if (!experiments.containsKey(performanceExperimentID))
            return null;

        return experiments.get(performanceExperimentID).getTrialStatus(trialID);

    }

}
