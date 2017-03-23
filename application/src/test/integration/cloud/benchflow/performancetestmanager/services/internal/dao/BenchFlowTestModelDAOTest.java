package cloud.benchflow.performancetestmanager.services.internal.dao;

import cloud.benchflow.performancetestmanager.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestmanager.helpers.TestConstants;
import cloud.benchflow.performancetestmanager.models.BenchFlowTestModel;
import cloud.benchflow.performancetestmanager.models.User;
import cloud.benchflow.performancetestmanager.DockerComposeTest;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.List;

import static cloud.benchflow.performancetestmanager.helpers.TestConstants.VALID_PERFORMANCE_TEST_NAME;
import static cloud.benchflow.performancetestmanager.models.BenchFlowTestModel.PerformanceTestState.COMPLETED;
import static org.junit.Assert.assertEquals;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 14.02.17.
 */
public class BenchFlowTestModelDAOTest extends DockerComposeTest{

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private PerformanceTestModelDAO testModelDAO;
    private UserDAO userDAO;
    private User testUser;

    @Before
    public void setUp() throws Exception {

        MongoClient mongoClient = new MongoClient(MONGO_CONTAINER.getIp(), MONGO_CONTAINER.getExternalPort());

        testModelDAO = new PerformanceTestModelDAO(mongoClient);
        userDAO = new UserDAO(mongoClient, testModelDAO);

        testUser = userDAO.addUser(TestConstants.TEST_USER_NAME);

    }

    @After
    public void tearDown() throws Exception {

        userDAO.removeUser(testUser.getUsername());

    }

    @Test
    public void addGetRemovePerformanceTestModel() throws Exception {

        String performanceTestID = testModelDAO.addPerformanceTestModel(VALID_PERFORMANCE_TEST_NAME, testUser);

        BenchFlowTestModel savedModel = testModelDAO.getPerformanceTestModel(performanceTestID);

        Assert.assertNotNull(savedModel);

        assertEquals(performanceTestID, savedModel.getId());

        testModelDAO.removePerformanceTestModel(performanceTestID);

    }

    @Test
    public void getPerformanceTestModels() throws Exception {

        // Performance Test IDs
        String testName = "performanceTest";

        int initialSize = testModelDAO.getPerformanceTestModels().size();

        String firstID = testModelDAO.addPerformanceTestModel(testName, testUser);
        String secondID = testModelDAO.addPerformanceTestModel(testName, testUser);

        List<String> modelIDs = testModelDAO.getPerformanceTestModels();

        Assert.assertNotNull(modelIDs);

        assertEquals(initialSize + 2, modelIDs.size());

        String thirdID = testModelDAO.addPerformanceTestModel(testName, testUser);

        modelIDs = testModelDAO.getPerformanceTestModels();

        assertEquals(initialSize + 3, modelIDs.size());

        testModelDAO.removePerformanceTestModel(firstID);
        testModelDAO.removePerformanceTestModel(secondID);

        modelIDs = testModelDAO.getPerformanceTestModels();

        assertEquals(initialSize + 1, modelIDs.size());

        testModelDAO.removePerformanceTestModel(thirdID);

        modelIDs = testModelDAO.getPerformanceTestModels();

        assertEquals(initialSize + 0, modelIDs.size());

    }

    @Test
    public void conflictingTestModelNames() throws Exception {

        String performanceTestIDFirst = testModelDAO.addPerformanceTestModel(VALID_PERFORMANCE_TEST_NAME, testUser);

        BenchFlowTestModel model = testModelDAO.getPerformanceTestModel(performanceTestIDFirst);

        Assert.assertNotNull(model);

        String performanceTestIDSecond = testModelDAO.addPerformanceTestModel(VALID_PERFORMANCE_TEST_NAME, testUser);

        Assert.assertNotEquals(performanceTestIDFirst, performanceTestIDSecond);

        model = testModelDAO.getPerformanceTestModel(performanceTestIDSecond);

        Assert.assertNotNull(model);

        testModelDAO.removePerformanceTestModel(performanceTestIDFirst);
        testModelDAO.removePerformanceTestModel(performanceTestIDSecond);

    }

    @Test
    public void changePerformanceTestState() throws Exception {

        String performanceTestID = testModelDAO.addPerformanceTestModel(VALID_PERFORMANCE_TEST_NAME, testUser);

        BenchFlowTestModel.PerformanceTestState state = testModelDAO.getPerformanceTestState(performanceTestID);

        assertEquals(BenchFlowTestModel.PerformanceTestState.READY, state);

        testModelDAO.setPerformanceTestState(performanceTestID, COMPLETED);

        state = testModelDAO.getPerformanceTestState(performanceTestID);

        assertEquals(COMPLETED, state);

        testModelDAO.removePerformanceTestModel(performanceTestID);

    }

    @Test
    public void changePerformanceTestStateInvalidID() throws Exception {

        exception.expect(PerformanceTestIDDoesNotExistException.class);

        testModelDAO.setPerformanceTestState("not_valid", BenchFlowTestModel.PerformanceTestState.RUNNING);

    }

    @Test
    public void testHashedID() throws Exception {

        testModelDAO.addPerformanceTestModel(VALID_PERFORMANCE_TEST_NAME, testUser);

        DBCollection collection = testModelDAO.getDataStore().getCollection(BenchFlowTestModel.class);

        collection.getIndexInfo().forEach(dbObject -> {

            BasicDBObject index = (BasicDBObject) dbObject;
            if (!index.getString("name").equals("_id_")) {
                assertEquals("hashed", ((DBObject) index.get("key")).get(BenchFlowTestModel.HASHED_ID_FIELD_NAME));
            }

        });


    }
}