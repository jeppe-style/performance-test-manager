package cloud.benchflow.performancetestorchestrator.services.internal;

import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDAlreadyExistsException;
import cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
public class PerformanceTestModelDAO {

    // TODO - make sure the class is thread safe

    public static final PerformanceTestModelDAO INSTANCE = new PerformanceTestModelDAO();
    private static final String dbName = "performance-test-orchestrator";
    private static final String PerformanceTestIDFieldName = "performanceTestID";
    private static Logger logger = LoggerFactory.getLogger(PerformanceTestModelDAO.class.getSimpleName());
    private final Datastore datastore;

    private PerformanceTestModelDAO() {

        final Morphia morphia = new Morphia();

        // tell Morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.map(PerformanceTestModel.class);

        // create the Datastore connecting to the default port on the local host
        // TODO - set-up mongo DB (http://mongodb.github.io/mongo-java-driver/2.13/getting-started/quick-tour/)
        datastore = morphia.createDatastore(new MongoClient(), dbName);
        datastore.ensureIndexes();

    }

    /**
     *
     * @param performanceTestID
     * @throws PerformanceTestIDAlreadyExistsException
     */
    public synchronized void addPerformanceTestModel(String performanceTestID) throws PerformanceTestIDAlreadyExistsException {

        logger.info("addPerformanceTestModel: " + performanceTestID);

        final Query<PerformanceTestModel> performanceTestModelQuery = datastore
                .createQuery(PerformanceTestModel.class)
                .field(PerformanceTestIDFieldName)
                .equal(performanceTestID);

        if (performanceTestModelQuery.get() != null) {
            throw new PerformanceTestIDAlreadyExistsException();
        }

        PerformanceTestModel model = new PerformanceTestModel(performanceTestID);
        datastore.save(model);

    }

    /**
     * @param performanceTestID
     */
    public synchronized void removePerformanceTestModel(String performanceTestID) {

        logger.info("removePerformanceTestModel: " + performanceTestID);

        final Query<PerformanceTestModel> performanceTestModelQuery = datastore
                .createQuery(PerformanceTestModel.class)
                .field(PerformanceTestIDFieldName)
                .equal(performanceTestID);

        datastore.delete(performanceTestModelQuery);

    }

    /**
     * @param performanceTestID
     * @return
     */
    public synchronized PerformanceTestModel getPerformanceTestModel(String performanceTestID) throws PerformanceTestIDDoesNotExistException {

        logger.info("getPerformanceTestModel: " + performanceTestID);

        final Query<PerformanceTestModel> performanceTestModelQuery = datastore
                .createQuery(PerformanceTestModel.class)
                .field(PerformanceTestIDFieldName)
                .equal(performanceTestID);

        PerformanceTestModel performanceTestModel = performanceTestModelQuery.get();

        if (performanceTestModel == null)
            throw new PerformanceTestIDDoesNotExistException();

        return performanceTestModel;

    }

    /**
     * @return
     */
    public synchronized List<String> getPerformanceTestModels() {

        logger.info("getPerformanceTestModels");

        final Query<PerformanceTestModel> performanceTestModelQuery = datastore
                .createQuery(PerformanceTestModel.class);

        return performanceTestModelQuery.asList()
                .stream()
                .map(PerformanceTestModel::getPerformanceTestID)
                .collect(Collectors.toList());
    }

    /**
     * @param performanceTestID
     * @param performanceExperimentID
     * @param trialID
     * @param status
     */
    public synchronized void addTrialStatus(String performanceTestID, String performanceExperimentID, String trialID, PerformanceExperimentModel.TrialStatus status) throws PerformanceTestIDDoesNotExistException {

        logger.info(
                "addTrialStatus: " + performanceTestID + "/" + performanceExperimentID + "/" + trialID + " : " + status.name());

        final PerformanceTestModel performanceTestModel = getPerformanceTestModel(performanceTestID);

        String compatibleExperimentID = makeKeyBSONCompatible(performanceExperimentID);
        String compatibleTrialID = makeKeyBSONCompatible(trialID);

        performanceTestModel.addTrialStatus(compatibleExperimentID, compatibleTrialID, status);

        datastore.save(performanceTestModel);

    }

    /**
     * @param performanceTestID
     * @param performanceExperimentID
     * @param trialID
     * @return
     */
    public synchronized PerformanceExperimentModel.TrialStatus getTrialStatus(String performanceTestID, String performanceExperimentID, String trialID) throws PerformanceTestIDDoesNotExistException {

        logger.info("getTrialStatus: " + performanceTestID + "/" + performanceExperimentID + "/" + trialID);

        final PerformanceTestModel performanceTestModel = getPerformanceTestModel(performanceTestID);

        String compatibleExperimentID = makeKeyBSONCompatible(performanceExperimentID);
        String compatibleTrialID = makeKeyBSONCompatible(trialID);

        return performanceTestModel.getTrialStatus(compatibleExperimentID, compatibleTrialID);

    }

    /**
     * @param performanceTestID
     * @param state
     */
    public synchronized PerformanceTestModel.PerformanceTestState setPerformanceTestState(String performanceTestID, PerformanceTestModel.PerformanceTestState state) throws PerformanceTestIDDoesNotExistException {

        logger.info("setPerformanceTestState: " + performanceTestID + " : " + state.name());

        final PerformanceTestModel performanceTestModel = getPerformanceTestModel(performanceTestID);

        performanceTestModel.setState(state);

        datastore.save(performanceTestModel);

        return getPerformanceTestModel(performanceTestID).getState();

    }

    /**
     * @param performanceTestID
     * @return
     */
    public synchronized PerformanceTestModel.PerformanceTestState getPerformanceTestState(String performanceTestID) throws PerformanceTestIDDoesNotExistException {

        logger.info("getPerformanceTestState: " + performanceTestID);

        final PerformanceTestModel performanceTestModel = getPerformanceTestModel(performanceTestID);

        return performanceTestModel.getState();

    }

    /**
     * https://docs.mongodb.com/manual/reference/limits/#naming-restrictions
     *
     * @param key
     * @return
     */
    private String makeKeyBSONCompatible(String key) {

        return key.replace(".", "/");

    }

}
