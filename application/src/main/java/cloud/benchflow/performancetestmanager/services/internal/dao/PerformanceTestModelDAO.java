package cloud.benchflow.performancetestmanager.services.internal.dao;

import cloud.benchflow.performancetestmanager.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestmanager.models.PerformanceTestModel;
import cloud.benchflow.performancetestmanager.models.PerformanceTestNumber;
import cloud.benchflow.performancetestmanager.models.User;
import cloud.benchflow.performancetestmanager.constants.BenchFlowConstants;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
public class PerformanceTestModelDAO {

    private static Logger logger = LoggerFactory.getLogger(PerformanceTestModelDAO.class.getSimpleName());

    private Datastore datastore;

    public PerformanceTestModelDAO(MongoClient mongoClient) {

        final Morphia morphia = new Morphia();

        // tell Morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.map(PerformanceTestModel.class);
        morphia.map(PerformanceTestNumber.class);
        morphia.map(User.class);

        // create the Datastore
        // TODO - set-up mongo DB (http://mongodb.github.io/mongo-java-driver/2.13/getting-started/quick-tour/)
        // TODO - check about resilience and cache
        datastore = morphia.createDatastore(mongoClient, BenchFlowConstants.DB_NAME);
        datastore.ensureIndexes();

    }

    public synchronized Datastore getDataStore() {
        return datastore;
    }

    /**
     * @param performanceTestName
     */
    public synchronized String addPerformanceTestModel(String performanceTestName, User user) {

        logger.info("addPerformanceTestModel: " + performanceTestName);

        long testNumber = generateTestNumber(performanceTestName, user);

        PerformanceTestModel model = new PerformanceTestModel(user, performanceTestName,
                                                              testNumber);
        datastore.save(model);

        user.addPerformanceTestModel(model);

        datastore.save(user);

        return model.getId();
    }

    /**
     * @param performanceTestName
     * @return
     */
    private synchronized long generateTestNumber(String performanceTestName, User user) {

        String performanceTestIdentifier = PerformanceTestNumber.generatePerformanceTestIdentifier(user.getUsername(),
                                                                                                   performanceTestName);

        Query<PerformanceTestNumber> query = datastore
                .createQuery(PerformanceTestNumber.class)
                .field(PerformanceTestNumber.ID_FIELD_NAME)
                .equal(performanceTestIdentifier);

        UpdateOperations<PerformanceTestNumber> update = datastore.createUpdateOperations(
                PerformanceTestNumber.class).inc(PerformanceTestNumber.COUNTER_FIELD_NAME);

        PerformanceTestNumber counter = datastore.findAndModify(query, update);

        if (counter == null) {
            counter = new PerformanceTestNumber(user.getUsername(), performanceTestName);
            datastore.save(counter);
        }

        return counter.getCounter();

    }

    /**
     * @param performanceTestID
     */
    public synchronized void removePerformanceTestModel(String performanceTestID) {

        logger.info("removePerformanceTestModel: " + performanceTestID);

        try {

            PerformanceTestModel testModel = getPerformanceTestModel(performanceTestID);

            testModel.getPerformanceExperiments().forEach(datastore::delete);

            User user = testModel.getUser();

            user.removePerformanceTestModel(testModel);

            datastore.delete(testModel);
            datastore.save(user);

        } catch (PerformanceTestIDDoesNotExistException e) {
            logger.info("tried to remove non-existent performance test");
        }

    }

    /**
     * @param performanceTestID
     * @return
     */
    public synchronized PerformanceTestModel getPerformanceTestModel(String performanceTestID) throws PerformanceTestIDDoesNotExistException {

        logger.info("getPerformanceTestModel: " + performanceTestID);

        final Query<PerformanceTestModel> performanceTestModelQuery = datastore
                .createQuery(PerformanceTestModel.class)
                .field(PerformanceTestModel.ID_FIELD_NAME)
                .equal(performanceTestID);

        PerformanceTestModel performanceTestModel = performanceTestModelQuery.get();

        if (performanceTestModel == null)
            throw new PerformanceTestIDDoesNotExistException();

        return performanceTestModel;

    }

    public synchronized boolean performanceTestModelExists(String performanceTestID) {

        logger.info("performanceTestModelExists: " + performanceTestID);

        final Query<PerformanceTestModel> performanceTestModelQuery = datastore
                .createQuery(PerformanceTestModel.class)
                .field(PerformanceTestModel.ID_FIELD_NAME)
                .equal(performanceTestID);

        PerformanceTestModel performanceTestModel = performanceTestModelQuery.get();

        return performanceTestModel != null;
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
                .map(PerformanceTestModel::getId)
                .collect(Collectors.toList());
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

}
