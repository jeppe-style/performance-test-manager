package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.request.SubmitTrialStatusRequest;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestorchestrator.exceptions.web.InvalidPerformanceTestIDException;
import cloud.benchflow.performancetestorchestrator.helpers.TestArchives;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestModelDAO;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel.TrialStatus.SUCCESS;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class TrialStatusResourceTest {

    private TrialStatusResource resource;
    private SubmitTrialStatusRequest request;

    // mocks
    private PerformanceTestModelDAO daoMock = Mockito.mock(PerformanceTestModelDAO.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        resource = new TrialStatusResource(daoMock);
        request = new SubmitTrialStatusRequest();
    }

    @Test
    public void submitTrialStatus() throws Exception {

        String performanceTestID = TestArchives.VALID_PERFORMANCE_TEST_ID;
        String performanceExperimentID = TestArchives.PERFORMANCE_EXPERIMENT_ID;
        String trialID = "1";
        request.setStatus(SUCCESS);

        Mockito.doNothing().when(daoMock).addTrialStatus(performanceTestID, performanceExperimentID, trialID,
                                                         request.getStatus());

        resource.submitTrialStatus(performanceTestID, performanceExperimentID, trialID,
                                   request);

    }

    @Test
    public void submitInvalidTrialStatus() throws Exception {

        String performanceTestID = TestArchives.INVALID_PERFORMANCE_TEST_ID;
        String performanceExperimentID = TestArchives.PERFORMANCE_EXPERIMENT_ID;
        String trialID = "1";
        request.setStatus(SUCCESS);

        Mockito.doThrow(PerformanceTestIDDoesNotExistException.class).when(daoMock).addTrialStatus(performanceTestID,
                                                                                                   performanceExperimentID,
                                                                                                   trialID,
                                                                                                   request.getStatus());

        exception.expect(InvalidPerformanceTestIDException.class);

        resource.submitTrialStatus(performanceTestID, performanceExperimentID, trialID, request);

    }

}