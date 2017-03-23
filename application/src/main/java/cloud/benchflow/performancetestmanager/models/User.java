package cloud.benchflow.performancetestmanager.models;

import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 21.02.17.
 */
@Entity
@Indexes({@Index(options = @IndexOptions(), fields = {@Field(value = "hashUsername", type = IndexType.HASHED)})})
public class User {

    public static String ID_FIELD_NAME = "username";
    public static String HASHED_ID_FIELD_NAME = "hashUsername";

    @Id
    private String username;

    private String hashUsername;

    @Reference
    private Set<BenchFlowTestModel> performanceTests = new HashSet<>();

    public User() {
        // Empty constructor for MongoDB + Morphia
    }

    public User(String username) {

        this.username = username;
        this.hashUsername = username;
    }

    public String getUsername() {
        return username;
    }

    public boolean addPerformanceTestModel(BenchFlowTestModel testModel) {

        return performanceTests.add(testModel);

    }

    public boolean removePerformanceTestModel(BenchFlowTestModel testModel) {

        return performanceTests.remove(testModel);

    }

    public void removeAllPerformanceTestModels() {
        performanceTests.clear();
    }

    public Set<BenchFlowTestModel> getPerformanceTests() {
        return performanceTests;
    }

}
