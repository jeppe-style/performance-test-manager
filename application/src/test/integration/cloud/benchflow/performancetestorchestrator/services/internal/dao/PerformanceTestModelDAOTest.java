package cloud.benchflow.performancetestorchestrator.services.internal.dao;

import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestorchestrator.helpers.TestConstants;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import cloud.benchflow.performancetestorchestrator.models.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static cloud.benchflow.performancetestorchestrator.helpers.TestConstants.VALID_PERFORMANCE_TEST_NAME;
import static cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel.PerformanceTestState.COMPLETED;
import static org.junit.Assert.assertEquals;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 14.02.17.
 */
public class PerformanceTestModelDAOTest {

    @ClassRule
    public static GenericContainer mongo =
            new GenericContainer("mongo:3.4.2")
                    .withExposedPorts(27017);
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private PerformanceTestModelDAO testModelDAO;
    private UserDAO userDAO;
    private User testUser;

    @Before
    public void setUp() throws Exception {

        MongoClient mongoClient = new MongoClient(mongo.getContainerIpAddress(), mongo.getMappedPort(27017));

        // TODO - check minio client settings
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

        PerformanceTestModel savedModel = testModelDAO.getPerformanceTestModel(performanceTestID);

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

        PerformanceTestModel model = testModelDAO.getPerformanceTestModel(performanceTestIDFirst);

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

        PerformanceTestModel.PerformanceTestState state = testModelDAO.getPerformanceTestState(performanceTestID);

        assertEquals(PerformanceTestModel.PerformanceTestState.READY, state);

        testModelDAO.setPerformanceTestState(performanceTestID, COMPLETED);

        state = testModelDAO.getPerformanceTestState(performanceTestID);

        assertEquals(COMPLETED, state);

        testModelDAO.removePerformanceTestModel(performanceTestID);

    }

    @Test
    public void changePerformanceTestStateInvalidID() throws Exception {

        exception.expect(PerformanceTestIDDoesNotExistException.class);

        testModelDAO.setPerformanceTestState("not_valid", PerformanceTestModel.PerformanceTestState.RUNNING);

    }

    @Test
    public void testHashedID() throws Exception {

        testModelDAO.addPerformanceTestModel(VALID_PERFORMANCE_TEST_NAME, testUser);

        DBCollection collection = testModelDAO.getDataStore().getCollection(PerformanceTestModel.class);

        collection.getIndexInfo().forEach(dbObject -> {

            BasicDBObject index = (BasicDBObject) dbObject;
            if (!index.getString("name").equals("_id_")) {
                assertEquals("hashed", ((DBObject) index.get("key")).get(PerformanceTestModel.HASHED_ID_FIELD_NAME));
            }

        });


    }
}