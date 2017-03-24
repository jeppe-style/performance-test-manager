package cloud.benchflow.testmanager.services.internal.dao;

import cloud.benchflow.testmanager.exceptions.BenchFlowExperimentIDDoesNotExistException;
import cloud.benchflow.testmanager.helpers.TestConstants;
import cloud.benchflow.testmanager.models.BenchFlowExperimentModel;
import cloud.benchflow.testmanager.models.BenchFlowTestModel;
import cloud.benchflow.testmanager.models.User;
import cloud.benchflow.testmanager.DockerComposeTest;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static cloud.benchflow.testmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER;
import static cloud.benchflow.testmanager.helpers.TestConstants.VALID_BENCHFLOW_TEST_NAME;
import static cloud.benchflow.testmanager.models.BenchFlowExperimentModel.TrialStatus.RUNNING;
import static cloud.benchflow.testmanager.models.BenchFlowExperimentModel.TrialStatus.SUCCESS;
import static org.junit.Assert.assertEquals;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 22.02.17.
 */
public class BenchFlowExperimentModelDAOTest extends DockerComposeTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private BenchFlowTestModelDAO testModelDAO;
    private BenchFlowExperimentModelDAO experimentModelDAO;
    private UserDAO userDAO;
    private User testUser;
    private String testID;

    @Before
    public void setUp() throws Exception {

        MongoClient mongoClient = new MongoClient(MONGO_CONTAINER.getIp(), MONGO_CONTAINER.getExternalPort());

        testModelDAO = new BenchFlowTestModelDAO(mongoClient);

        experimentModelDAO = new BenchFlowExperimentModelDAO(mongoClient, testModelDAO);

        userDAO = new UserDAO(mongoClient, testModelDAO);

        testUser = userDAO.addUser(TestConstants.TEST_USER_NAME);

        testID = testModelDAO.addTestModel(VALID_BENCHFLOW_TEST_NAME, testUser);

        BenchFlowTestModel model = testModelDAO.getTestModel(testID);

        Assert.assertNotNull(model);
        assertEquals(testID, model.getId());

    }

    @After
    public void tearDown() throws Exception {

        userDAO.removeUser(testUser.getUsername());

    }

    @Test
    public void addTrialStatus() throws Exception {

        int trialNumber = 1;

        String experimentID = experimentModelDAO.addExperiment(testID);

        // RUNNING
        experimentModelDAO.addTrialStatus(experimentID, trialNumber, RUNNING);
        BenchFlowExperimentModel.TrialStatus trialStatus = experimentModelDAO.getTrialStatus(experimentID,
                                                                                               trialNumber);

        Assert.assertNotNull(trialStatus);
        assertEquals(RUNNING, trialStatus);

        // SUCCESS
        experimentModelDAO.addTrialStatus(experimentID, trialNumber, SUCCESS);
        trialStatus = experimentModelDAO.getTrialStatus(experimentID, trialNumber);

        Assert.assertNotNull(trialStatus);
        assertEquals(SUCCESS, trialStatus);

    }

    @Test
    public void addMultipleExperiments() throws Exception {

        // make sure that the experiment counter is incremented correctly

        String firstID = experimentModelDAO.addExperiment(testID);

        assertEquals(testID + MODEL_ID_DELIMITER + 1, firstID);

        BenchFlowTestModel testModel = testModelDAO.getTestModel(testID);

        assertEquals(1, testModel.getExperimentModels().size());

        String secondID = experimentModelDAO.addExperiment(testID);

        assertEquals(testID + MODEL_ID_DELIMITER + 2, secondID);

        testModel = testModelDAO.getTestModel(testID);

        assertEquals(2, testModel.getExperimentModels().size());

    }

    @Test
    public void addTrialToMissingExperiment() throws Exception {


        exception.expect(BenchFlowExperimentIDDoesNotExistException.class);

        experimentModelDAO.addTrialStatus("not_valid", 1, SUCCESS);

    }

    @Test
    public void testHashedID() throws Exception {

        experimentModelDAO.addExperiment(testID);

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