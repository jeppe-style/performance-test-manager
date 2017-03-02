package cloud.benchflow.performancetestmanager.models;

import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.util.HashSet;
import java.util.Set;

import static cloud.benchflow.performancetestmanager.models.PerformanceTestModel.PerformanceTestState.READY;
import static cloud.benchflow.performancetestmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
@Entity
@Indexes({@Index(options = @IndexOptions(), fields = {@Field(value = "hashedID", type = IndexType.HASHED)})})
public class PerformanceTestModel {

    public static final String ID_FIELD_NAME = "id";
    public static final String HASHED_ID_FIELD_NAME = "hashedID";

    public enum PerformanceTestState { READY, RUNNING, COMPLETED }

    // Annotations for MongoDB + Morphia (http://mongodb.github.io/morphia/1.3/guides/annotations/#entity)

//    userName.testName.testNumber.experimentNumber.trialNumber

    @Id
    private String id;

    // used for potential sharing in the future
    private String hashedID;

    @Reference
    private User user;

    private String name;
    private long number;

    private PerformanceTestState state;

    @Reference
    private Set<PerformanceExperimentModel> experiments = new HashSet<>();

    public PerformanceTestModel() {
        // Empty constructor for MongoDB + Morphia
    }

    public PerformanceTestModel(User user, String performanceTestName, long performanceTestNumber) {

        this.user = user;
        this.name = performanceTestName;
        this.number = performanceTestNumber;

        this.id = user.getUsername() + MODEL_ID_DELIMITER + performanceTestName + MODEL_ID_DELIMITER + performanceTestNumber;
        this.hashedID = this.id;

        this.state = READY;

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

    public PerformanceTestState getState() {
        return state;
    }

    public void setState(PerformanceTestState state) {
        this.state = state;
    }

    public void addPerformanceExperimentModel(PerformanceExperimentModel experimentModel) {

        experiments.add(experimentModel);

    }

    public boolean containsPerformanceExperiment(String performanceExperimentID) {

        return experiments.stream().filter(model -> model.getId().equals(performanceExperimentID)).count() != 0;

    }

    public Set<PerformanceExperimentModel> getPerformanceExperiments() {

       return experiments;

    }

    public long getNextPerformanceExperimentNumber() {

        return experiments.size() + 1;

    }

}
