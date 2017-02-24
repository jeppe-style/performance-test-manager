package cloud.benchflow.performancetestorchestrator.models;

import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.util.HashMap;
import java.util.Map;

import static cloud.benchflow.performancetestorchestrator.constants.BenchFlowConstants.MODEL_ID_DELIMITER;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
@Entity
@Indexes({@Index(options = @IndexOptions(), fields = {@Field(value = "hashedID", type = IndexType.HASHED)})})
public class PerformanceExperimentModel {

    public enum TrialStatus { RUNNING, SUCCESS, FAILURE, ERROR }

    public enum PerformanceExperimentState { READY, RUNNING, ABORTED, COMPLETED }

    public static final String ID_FIELD_NAME = "id";
    public static final String HASHED_ID_FIELD_NAME = "hashedID";

    @Id
    private String id;

    // used for potential sharding in the future
    private String hashedID;

    private String performanceTestID;
    private long number;

    private Map<Long, TrialStatus> trials = new HashMap<>();

    PerformanceExperimentModel(){
        // Empty constructor for MongoDB + Morphia
    }

    public PerformanceExperimentModel(String performanceTestID, long experimentNumber) {

        this.performanceTestID = performanceTestID;
        this.number = experimentNumber;

        this.id = performanceTestID + MODEL_ID_DELIMITER + experimentNumber;

        this.hashedID = this.id;


    }

    public String getId() {
        return id;
    }

    public void setTrialStatus(long trialNumber, TrialStatus status) {

        trials.put(trialNumber, status);

    }

    public TrialStatus getTrialStatus(long trialNumber) {

        return trials.get(trialNumber);

    }
}
