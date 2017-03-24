package cloud.benchflow.testmanager.models;

import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static cloud.benchflow.testmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
@Entity
@Indexes({@Index(options = @IndexOptions(), fields = {@Field(value = "hashedID", type = IndexType.HASHED)})})
public class BenchFlowExperimentModel {

    public static final String ID_FIELD_NAME = "id";
    public static final String HASHED_ID_FIELD_NAME = "hashedID";
    @Id
    private String id;
    // used for potential sharding in the future
    private String hashedID;
    private String testID;
    private long number;
    private Date start = new Date();
    private Date lastModified = new Date();
    private Map<Long, TrialStatus> trials = new HashMap<>();

    BenchFlowExperimentModel() {
        // Empty constructor for MongoDB + Morphia
    }

    public BenchFlowExperimentModel(String testID, long experimentNumber) {

        this.testID = testID;
        this.number = experimentNumber;

        this.id = testID + MODEL_ID_DELIMITER + experimentNumber;

        this.hashedID = this.id;

    }

    @PrePersist
    void prePersist() {
        lastModified = new Date();
    }

    public String getId() {
        return id;
    }

    public Date getStart() {
        return start;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setTrialStatus(long trialNumber, TrialStatus status) {

        trials.put(trialNumber, status);

    }

    public TrialStatus getTrialStatus(long trialNumber) {

        return trials.get(trialNumber);

    }

    public enum TrialStatus {RUNNING, SUCCESS, FAILURE, ERROR}

    public enum BenchFlowExperimentState {READY, RUNNING, ABORTED, COMPLETED}
}
