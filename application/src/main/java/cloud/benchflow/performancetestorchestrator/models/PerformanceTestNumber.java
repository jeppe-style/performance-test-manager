package cloud.benchflow.performancetestorchestrator.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import static cloud.benchflow.performancetestorchestrator.constants.BenchFlowConstants.MODEL_ID_DELIMITER;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 21.02.17.
 */
@Entity(noClassnameStored = true)
public class PerformanceTestNumber {

    public static String COUNTER_FIELD_NAME = "counter";
    public static String ID_FIELD_NAME = "performanceTestIdentifier";

    @Id
    private String performanceTestIdentifier;

    private Long counter = 1L;

    PerformanceTestNumber() {
        // Empty constructor for MongoDB + Morphia
    }

    public PerformanceTestNumber(String userName, String performanceTestName) {
        this.performanceTestIdentifier = generatePerformanceTestIdentifier(userName, performanceTestName);
    }

    public Long getCounter() {
        return counter;
    }

    public static String generatePerformanceTestIdentifier(String userName, String performanceTestName) {

        return userName + MODEL_ID_DELIMITER + performanceTestName;

    }
}
