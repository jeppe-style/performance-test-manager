package cloud.benchflow.performancetestmanager.services.internal.dao;

import cloud.benchflow.performancetestmanager.exceptions.PerformanceExperimentIDDoesNotExistException;
import cloud.benchflow.performancetestmanager.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestmanager.models.BenchFlowExperimentModel;
import cloud.benchflow.performancetestmanager.models.BenchFlowTestModel;
import cloud.benchflow.performancetestmanager.constants.BenchFlowConstants;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloud.benchflow.performancetestmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 22.02.17.
 */
public class PerformanceExperimentModelDAO {

    private static final String PERFORMANCE_EXPERIMENT_ID_FIELD_NAME = "id";

    private static Logger logger = LoggerFactory.getLogger(PerformanceExperimentModelDAO.class.getSimpleName());

    private Datastore datastore;
    private PerformanceTestModelDAO testModelDAO;

    public PerformanceExperimentModelDAO(MongoClient mongoClient, PerformanceTestModelDAO performanceTestModelDAO) {

        this.testModelDAO = performanceTestModelDAO;

        final Morphia morphia = new Morphia();

        // tell Morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.map(BenchFlowExperimentModel.class);

        // create the Datastore
        // TODO - set-up mongo DB (http://mongodb.github.io/mongo-java-driver/2.13/getting-started/quick-tour/)
        // TODO - check about resilience and cache
        datastore = morphia.createDatastore(mongoClient, BenchFlowConstants.DB_NAME);
        datastore.ensureIndexes();

    }

    /**
     * @param performanceTestID
     * @return
     * @throws PerformanceTestIDDoesNotExistException
     */
    public synchronized String addPerformanceExperiment(String performanceTestID) throws PerformanceTestIDDoesNotExistException {

        logger.info("addPerformanceExperiment: " + performanceTestID);

        final BenchFlowTestModel benchFlowTestModel = testModelDAO.getPerformanceTestModel(performanceTestID);

        long experimentNumber = benchFlowTestModel.getNextPerformanceExperimentNumber();

        BenchFlowExperimentModel experimentModel = new BenchFlowExperimentModel(performanceTestID,
                                                                                    experimentNumber);

        // first save the PE model and then add it to PT Model
        datastore.save(experimentModel);

        benchFlowTestModel.addPerformanceExperimentModel(experimentModel);

        datastore.save(benchFlowTestModel);

        return experimentModel.getId();

    }

    /**
     * @param performanceExperimentID
     * @return
     * @throws PerformanceExperimentIDDoesNotExistException
     */
    private synchronized BenchFlowExperimentModel getPerformanceExperiment(String performanceExperimentID) throws PerformanceExperimentIDDoesNotExistException {

        logger.info("getPerformanceExperiment: " + performanceExperimentID);

        final Query<BenchFlowExperimentModel> performanceTestModelQuery = datastore
                .createQuery(BenchFlowExperimentModel.class)
                .field(PERFORMANCE_EXPERIMENT_ID_FIELD_NAME)
                .equal(performanceExperimentID);

        BenchFlowExperimentModel experimentModel = performanceTestModelQuery.get();

        if (experimentModel == null)
            throw new PerformanceExperimentIDDoesNotExistException();

        return experimentModel;

    }

    /**
     * @param performanceExperimentID
     * @param trialNUmber
     * @param status
     * @throws PerformanceTestIDDoesNotExistException
     */
    public synchronized void addTrialStatus(String performanceExperimentID, long trialNUmber, BenchFlowExperimentModel.TrialStatus status) throws PerformanceExperimentIDDoesNotExistException {

        logger.info(
                "addTrialStatus: " + performanceExperimentID + MODEL_ID_DELIMITER + trialNUmber + " : " + status.name());

        final BenchFlowExperimentModel experimentModel;

        experimentModel = getPerformanceExperiment(performanceExperimentID);

        experimentModel.setTrialStatus(trialNUmber, status);
        datastore.save(experimentModel);

    }

    /**
     * @param performanceExperimentID
     * @param trialNumber
     * @return
     * @throws PerformanceExperimentIDDoesNotExistException
     */
    public synchronized BenchFlowExperimentModel.TrialStatus getTrialStatus(String performanceExperimentID, long trialNumber) throws PerformanceExperimentIDDoesNotExistException {

        logger.info("getTrialStatus: " + performanceExperimentID + MODEL_ID_DELIMITER + trialNumber);

        final BenchFlowExperimentModel experimentModel = getPerformanceExperiment(performanceExperimentID);

        return experimentModel.getTrialStatus(trialNumber);

    }
}
