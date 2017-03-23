package cloud.benchflow.performancetestmanager.models;

import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static cloud.benchflow.performancetestmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER;
import static cloud.benchflow.performancetestmanager.models.BenchFlowTestModel.PerformanceTestState.READY;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
@Entity
@Indexes({@Index(options = @IndexOptions(), fields = {@Field(value = "hashedID", type = IndexType.HASHED)})})
public class BenchFlowTestModel {

    public static final String ID_FIELD_NAME = "id";
    public static final String HASHED_ID_FIELD_NAME = "hashedID";
    @Id
    private String id;

    // Annotations for MongoDB + Morphia (http://mongodb.github.io/morphia/1.3/guides/annotations/#entity)

    //    userName.testName.testNumber.experimentNumber.trialNumber
    // used for potential sharing in the future
    private String hashedID;
    @Reference
    private User user;
    private String name;
    private long number;
    private Date start = new Date();
    private Date lastModified = new Date();
    private PerformanceTestState state;
    @Reference
    private Set<BenchFlowExperimentModel> experiments = new HashSet<>();

    public BenchFlowTestModel() {
        // Empty constructor for MongoDB + Morphia
    }

    public BenchFlowTestModel(User user, String performanceTestName, long performanceTestNumber) {

        this.user = user;
        this.name = performanceTestName;
        this.number = performanceTestNumber;

        this.id = user.getUsername() + MODEL_ID_DELIMITER + performanceTestName + MODEL_ID_DELIMITER + performanceTestNumber;
        this.hashedID = this.id;

        this.state = READY;

    }

    @PrePersist
    void prePersist() {
        lastModified = new Date();
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public long getNumber() {
        return number;
    }

    public Date getStart() {
        return start;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public PerformanceTestState getState() {
        return state;
    }

    public void setState(PerformanceTestState state) {
        this.state = state;
    }

    public void addPerformanceExperimentModel(BenchFlowExperimentModel experimentModel) {

        experiments.add(experimentModel);

    }

    public boolean containsPerformanceExperiment(String performanceExperimentID) {

        return experiments.stream().filter(model -> model.getId().equals(performanceExperimentID)).count() != 0;

    }

    public Set<BenchFlowExperimentModel> getPerformanceExperiments() {

        return experiments;

    }

    public long getNextPerformanceExperimentNumber() {

        return experiments.size() + 1;

    }

    public enum PerformanceTestState {READY, RUNNING, COMPLETED}

}
