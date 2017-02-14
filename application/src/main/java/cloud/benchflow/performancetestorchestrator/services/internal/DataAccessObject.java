package cloud.benchflow.performancetestorchestrator.services.internal;

import cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
public class DataAccessObject {

    // TODO - make sure the class is thread safe

    private static Logger logger = LoggerFactory.getLogger(DataAccessObject.class.getSimpleName());

    private static Map<String, Boolean> performanceTestDataStore = new ConcurrentHashMap<>();

    public void addPerformanceTestModel(String performanceTestID) {
        // TODO
    }

    public List<PerformanceTestModel> getPerformanceTestModels() {

        // TODO

        return null;
    }

    public void addTrialStatus(String performanceTestID, String performanceExperimentID, String trialID, PerformanceExperimentModel.TrialStatus status) {
        // TODO
    }

    public void setPerformanceTestState(String performanceTestID, PerformanceTestModel.PerformanceTestState state) {
        // TODO
    }

    public PerformanceTestModel getPerformanceTestStatus(String performanceTestID) {

        // TODO

        return null;
    }

}
