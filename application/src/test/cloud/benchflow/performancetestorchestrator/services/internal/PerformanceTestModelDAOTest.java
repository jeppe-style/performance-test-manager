package cloud.benchflow.performancetestorchestrator.services.internal;

import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDAlreadyExistsException;
import cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel.TrialStatus;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.List;

import static cloud.benchflow.performancetestorchestrator.helpers.TestArchives.PERFORMANCE_EXPERIMENT_ID;
import static cloud.benchflow.performancetestorchestrator.helpers.TestArchives.VALID_PERFORMANCE_TEST_ID;
import static cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel.TrialStatus.RUNNING;
import static cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel.TrialStatus.SUCCESS;
import static cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel.PerformanceTestState.COMPLETED;
import static cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel.PerformanceTestState.RUNNNING;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 14.02.17.
 */
public class PerformanceTestModelDAOTest {

    private PerformanceTestModelDAO dao;

    private String performanceTestID = null;
    private String conflictingPerformanceTestID = null;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        dao = PerformanceTestModelDAO.INSTANCE;

    }

    @After
    public void tearDown() throws Exception {

        // make sure test model is removed

        if (performanceTestID != null) {

            dao.removePerformanceTestModel(performanceTestID);

            exception.expect(PerformanceTestIDDoesNotExistException.class);

            dao.getPerformanceTestModel(performanceTestID);

        }

        if (conflictingPerformanceTestID != null) {

            dao.removePerformanceTestModel(conflictingPerformanceTestID);

        }

    }

    @Test
    public void addGetRemovePerformanceTestModel() throws Exception {

        performanceTestID = VALID_PERFORMANCE_TEST_ID;

        dao.addPerformanceTestModel(performanceTestID);

        PerformanceTestModel savedModel = dao.getPerformanceTestModel(performanceTestID);

        Assert.assertNotNull(savedModel);

        Assert.assertEquals(performanceTestID, savedModel.getPerformanceTestID());

    }

    @Test
    public void getPerformanceTestModels() throws Exception {

        // Performance Test IDs
        String baseID = "performanceTest.";
        String firstID = baseID + 1;
        String secondID = baseID + 2;
        String thirdID = baseID + 3;

        int initialSize = dao.getPerformanceTestModels().size();

        dao.addPerformanceTestModel(firstID);
        dao.addPerformanceTestModel(secondID);

        List<String> modelIDs = dao.getPerformanceTestModels();

        Assert.assertNotNull(modelIDs);

        Assert.assertEquals(initialSize + 2, modelIDs.size());

        dao.addPerformanceTestModel(thirdID);

        modelIDs = dao.getPerformanceTestModels();

        Assert.assertEquals(initialSize + 3, modelIDs.size());

        dao.removePerformanceTestModel(firstID);
        dao.removePerformanceTestModel(secondID);

        modelIDs = dao.getPerformanceTestModels();

        Assert.assertEquals(initialSize + 1, modelIDs.size());

        dao.removePerformanceTestModel(thirdID);

        modelIDs = dao.getPerformanceTestModels();

        Assert.assertEquals(initialSize + 0, modelIDs.size());

    }

    @Test
    public void conflictingTestModelIDs() throws Exception {

        conflictingPerformanceTestID = VALID_PERFORMANCE_TEST_ID;


        dao.addPerformanceTestModel(conflictingPerformanceTestID);

        PerformanceTestModel model = dao.getPerformanceTestModel(conflictingPerformanceTestID);

        Assert.assertNotNull(model);

        exception.expect(PerformanceTestIDAlreadyExistsException.class);

        dao.addPerformanceTestModel(conflictingPerformanceTestID);

        // TODO - ensure performance test is deleted

    }

    @Test
    public void addTrialStatus() throws Exception {

        performanceTestID = VALID_PERFORMANCE_TEST_ID;

        String trialID = "1";

        dao.addPerformanceTestModel(performanceTestID);

        PerformanceTestModel model = dao.getPerformanceTestModel(performanceTestID);

        Assert.assertNotNull(model);
        Assert.assertEquals(performanceTestID, model.getPerformanceTestID());

        // RUNNING
        dao.addTrialStatus(performanceTestID, PERFORMANCE_EXPERIMENT_ID, trialID, RUNNING);
        TrialStatus trialStatus = dao.getTrialStatus(performanceTestID, PERFORMANCE_EXPERIMENT_ID, trialID);

        Assert.assertNotNull(trialStatus);
        Assert.assertEquals(RUNNING, trialStatus);

        // SUCCESS
        dao.addTrialStatus(performanceTestID, PERFORMANCE_EXPERIMENT_ID, trialID, SUCCESS);
        trialStatus = dao.getTrialStatus(performanceTestID, PERFORMANCE_EXPERIMENT_ID, trialID);

        Assert.assertNotNull(trialStatus);
        Assert.assertEquals(SUCCESS, trialStatus);

    }

    @Test
    public void changePerformanceTestState() throws Exception {

        performanceTestID = VALID_PERFORMANCE_TEST_ID;

        dao.addPerformanceTestModel(performanceTestID);

        PerformanceTestModel.PerformanceTestState state = dao.getPerformanceTestState(performanceTestID);

        Assert.assertEquals(PerformanceTestModel.PerformanceTestState.READY, state);

        dao.setPerformanceTestState(performanceTestID, COMPLETED);

        state = dao.getPerformanceTestState(performanceTestID);

        Assert.assertEquals(COMPLETED, state);

    }

    @Test
    public void changePerformanceTestStateInvalidID() throws Exception {

        exception.expect(PerformanceTestIDDoesNotExistException.class);

        dao.setPerformanceTestState("not_valid", RUNNNING);

    }

}