package cloud.benchflow.performancetestmanager.services.internal.dao;

import cloud.benchflow.performancetestmanager.exceptions.UserIDAlreadyExistsException;
import cloud.benchflow.performancetestmanager.models.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerMachine;
import com.palantir.docker.compose.connection.DockerPort;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static cloud.benchflow.performancetestmanager.helpers.TestConstants.TEST_USER_NAME;
import static cloud.benchflow.performancetestmanager.helpers.TestConstants.VALID_PERFORMANCE_TEST_NAME;
import static org.junit.Assert.assertEquals;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 22.02.17.
 */
public class UserDAOTest {


    private PerformanceTestModelDAO testModelDAO;
    private UserDAO userDAO;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final DockerMachine dockerMachine = DockerMachine.localMachine()
            .withAdditionalEnvironmentVariable("MONGO_TAG", "3.4.2")
            .withAdditionalEnvironmentVariable("MONGO_DATA_VOLUME", "~/DEV/mongo/datadir")
            .withAdditionalEnvironmentVariable("MINIO_TAG", "RELEASE.2017-02-16T01-47-30Z")
            .withAdditionalEnvironmentVariable("MINIO_ACCESS_KEY", "minio")
            .withAdditionalEnvironmentVariable("MINIO_SECRET_KEY", "minio123")
            .build();

    @ClassRule
    public static DockerComposeRule composeRule = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose/docker-compose.yml")
            .machine(dockerMachine)
            .build();

    @Before
    public void setUp() throws Exception {

        DockerPort port = composeRule.containers().container("mongo").port(27017);

        MongoClient mongoClient = new MongoClient(port.getIp(), port.getExternalPort());

        testModelDAO = new PerformanceTestModelDAO(mongoClient);

        userDAO = new UserDAO(mongoClient, testModelDAO);

    }

    @After
    public void tearDown() throws Exception {

        userDAO.removeUser(TEST_USER_NAME);

    }

    @Test
    public void addRemoveUser() throws Exception {

        User user = userDAO.addUser(TEST_USER_NAME);

        Assert.assertNotNull(user);

        User savedUser = userDAO.getUser(user.getUsername());

        Assert.assertEquals(TEST_USER_NAME, savedUser.getUsername());

        userDAO.removeUser(TEST_USER_NAME);

        user = userDAO.getUser(TEST_USER_NAME);

        Assert.assertNull(user);

    }

    @Test
    public void addSameUserTwice() throws Exception  {

        userDAO.addUser(TEST_USER_NAME);

        exception.expect(UserIDAlreadyExistsException.class);

        userDAO.addUser(TEST_USER_NAME);

    }

    @Test
    public void removeUserWithPerformanceTests() throws Exception {

        User user = userDAO.addUser(TEST_USER_NAME);

        String testModel1ID = testModelDAO.addPerformanceTestModel(VALID_PERFORMANCE_TEST_NAME, user);

        user = userDAO.getUser(user.getUsername());

        Assert.assertNotNull(user);

        Assert.assertEquals(1, user.getPerformanceTests().size());

        String testModel2ID = testModelDAO.addPerformanceTestModel(VALID_PERFORMANCE_TEST_NAME, user);

        Assert.assertEquals(2, user.getPerformanceTests().size());

        userDAO.removeUser(user.getUsername());

        user = userDAO.getUser(user.getUsername());

        Assert.assertNull(user);

        Assert.assertEquals(false, testModelDAO.performanceTestModelExists(testModel1ID));

        Assert.assertEquals(false, testModelDAO.performanceTestModelExists(testModel2ID));

    }

    @Test
    public void testHashedID() throws Exception {

        userDAO.addUser(TEST_USER_NAME);

        DBCollection collection = testModelDAO.getDataStore().getCollection(User.class);

        collection.getIndexInfo().forEach(dbObject -> {

            BasicDBObject index = (BasicDBObject) dbObject;
            if (!index.getString("name").equals("_id_")) {
                assertEquals("hashed", ((DBObject) index.get("key")).get(User.HASHED_ID_FIELD_NAME));
            }

        });


    }

}