package cloud.benchflow.performancetestmanager.resources;

import cloud.benchflow.performancetestmanager.api.request.SubmitTrialStatusRequest;
import cloud.benchflow.performancetestmanager.exceptions.PerformanceExperimentIDDoesNotExistException;
import cloud.benchflow.performancetestmanager.exceptions.web.InvalidPerformanceTrialIDWebException;
import cloud.benchflow.performancetestmanager.helpers.TestConstants;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceExperimentModelDAO;
import cloud.benchflow.performancetestmanager.constants.BenchFlowConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static cloud.benchflow.performancetestmanager.models.PerformanceExperimentModel.TrialStatus.SUCCESS;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class TrialStatusResourceTest {

    private TrialStatusResource resource;
    private SubmitTrialStatusRequest request;

    // mocks
    private PerformanceExperimentModelDAO experimentModelDAOMock = Mockito.mock(PerformanceExperimentModelDAO.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        resource = new TrialStatusResource(experimentModelDAOMock);
        request = new SubmitTrialStatusRequest();
    }

    @Test
    public void submitTrialStatus() throws Exception {

        String performanceExperimentID = TestConstants.PERFORMANCE_EXPERIMENT_ID;
        long trialNumber = 1;
        String trialID = performanceExperimentID + BenchFlowConstants.MODEL_ID_DELIMITER + trialNumber;
        request.setStatus(SUCCESS);

        Mockito.doNothing().when(experimentModelDAOMock).addTrialStatus(performanceExperimentID, trialNumber,
                                                                  request.getStatus());

        resource.submitTrialStatus(trialID, request);

    }

    @Test
    public void submitInvalidTrialStatus() throws Exception {

        String performanceExperimentID = TestConstants.PERFORMANCE_EXPERIMENT_ID;
        long trialNumber = 1;

        String trialID = performanceExperimentID + BenchFlowConstants.MODEL_ID_DELIMITER + trialNumber;

        request.setStatus(SUCCESS);

        Mockito.doThrow(PerformanceExperimentIDDoesNotExistException.class).when(experimentModelDAOMock).addTrialStatus(performanceExperimentID,
                                                                                                                        trialNumber,
                                                                                                                        request.getStatus());

        exception.expect(InvalidPerformanceTrialIDWebException.class);

        resource.submitTrialStatus(trialID, request);

    }

}