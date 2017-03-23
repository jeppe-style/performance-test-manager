package cloud.benchflow.performancetestmanager.services.internal.dao;

import cloud.benchflow.performancetestmanager.exceptions.PerformanceExperimentIDDoesNotExistException;
import cloud.benchflow.performancetestmanager.helpers.TestConstants;
import cloud.benchflow.performancetestmanager.models.BenchFlowExperimentModel;
import cloud.benchflow.performancetestmanager.models.BenchFlowTestModel;
import cloud.benchflow.performancetestmanager.models.User;
import cloud.benchflow.performancetestmanager.DockerComposeTest;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static cloud.benchflow.performancetestmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER;
import static cloud.benchflow.performancetestmanager.helpers.TestConstants.VALID_PERFORMANCE_TEST_NAME;
import static cloud.benchflow.performancetestmanager.models.BenchFlowExperimentModel.TrialStatus.RUNNING;
import static cloud.benchflow.performancetestmanager.models.BenchFlowExperimentModel.TrialStatus.SUCCESS;
import static org.junit.Assert.assertEquals;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 22.02.17.
 */
public class BenchFlowExperimentModelDAOTest extends DockerComposeTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private PerformanceTestModelDAO testModelDAO;
    private PerformanceExperimentModelDAO experimentModelDAO;
    private UserDAO userDAO;
    private User testUser;
    private String performanceTestID;

    @Before
    public void setUp() throws Exception {

        MongoClient mongoClient = new MongoClient(MONGO_CONTAINER.getIp(), MONGO_CONTAINER.getExternalPort());

        testModelDAO = new PerformanceTestModelDAO(mongoClient);

        experimentModelDAO = new PerformanceExperimentModelDAO(mongoClient, testModelDAO);

        userDAO = new UserDAO(mongoClient, testModelDAO);

        testUser = userDAO.addUser(TestConstants.TEST_USER_NAME);

        performanceTestID = testModelDAO.addPerformanceTestModel(VALID_PERFORMANCE_TEST_NAME, testUser);

        BenchFlowTestModel model = testModelDAO.getPerformanceTestModel(performanceTestID);

        Assert.assertNotNull(model);
        assertEquals(performanceTestID, model.getId());

    }

    @After
    public void tearDown() throws Exception {

        userDAO.removeUser(testUser.getUsername());

    }

    @Test
    public void addTrialStatus() throws Exception {

        int trialNumber = 1;

        String performanceExperimentID = experimentModelDAO.addPerformanceExperiment(performanceTestID);

        // RUNNING
        experimentModelDAO.addTrialStatus(performanceExperimentID, trialNumber, RUNNING);
        BenchFlowExperimentModel.TrialStatus trialStatus = experimentModelDAO.getTrialStatus(performanceExperimentID,
                                                                                               trialNumber);

        Assert.assertNotNull(trialStatus);
        assertEquals(RUNNING, trialStatus);

        // SUCCESS
        experimentModelDAO.addTrialStatus(performanceExperimentID, trialNumber, SUCCESS);
        trialStatus = experimentModelDAO.getTrialStatus(performanceExperimentID, trialNumber);

        Assert.assertNotNull(trialStatus);
        assertEquals(SUCCESS, trialStatus);

    }

    @Test
    public void addMultipleExperiments() throws Exception {

        // make sure that the experiment counter is incremented correctly

        String firstID = experimentModelDAO.addPerformanceExperiment(performanceTestID);

        assertEquals(performanceTestID + MODEL_ID_DELIMITER + 1, firstID);

        BenchFlowTestModel testModel = testModelDAO.getPerformanceTestModel(performanceTestID);

        assertEquals(1, testModel.getPerformanceExperiments().size());

        String secondID = experimentModelDAO.addPerformanceExperiment(performanceTestID);

        assertEquals(performanceTestID + MODEL_ID_DELIMITER + 2, secondID);

        testModel = testModelDAO.getPerformanceTestModel(performanceTestID);

        assertEquals(2, testModel.getPerformanceExperiments().size());

    }

    @Test
    public void addTrialToMissingExperiment() throws Exception {


        exception.expect(PerformanceExperimentIDDoesNotExistException.class);

        experimentModelDAO.addTrialStatus("not_valid", 1, SUCCESS);

    }

    @Test
    public void testHashedID() throws Exception {

        experimentModelDAO.addPerformanceExperiment(performanceTestID);

        DBCollection collection = testModelDAO.getDataStore().getCollection(BenchFlowExperimentModel.class);

        collection.getIndexInfo().forEach(dbObject -> {

            BasicDBObject index = (BasicDBObject) dbObject;
            if (!index.getString("name").equals("_id_")) {
                assertEquals("hashed",
                             ((DBObject) index.get("key")).get(BenchFlowExperimentModel.HASHED_ID_FIELD_NAME));
            }

        });


    }
}