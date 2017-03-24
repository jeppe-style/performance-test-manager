package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.request.SubmitTrialStatusRequest;
import cloud.benchflow.testmanager.exceptions.BenchFlowExperimentIDDoesNotExistException;
import cloud.benchflow.testmanager.exceptions.web.InvalidTrialIDWebException;
import cloud.benchflow.testmanager.helpers.TestConstants;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowExperimentModelDAO;
import cloud.benchflow.testmanager.constants.BenchFlowConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static cloud.benchflow.testmanager.models.BenchFlowExperimentModel.TrialStatus.SUCCESS;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class TrialStatusResourceTest {

    private TrialStatusResource resource;
    private SubmitTrialStatusRequest request;

    // mocks
    private BenchFlowExperimentModelDAO experimentModelDAOMock = Mockito.mock(BenchFlowExperimentModelDAO.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        resource = new TrialStatusResource(experimentModelDAOMock);
        request = new SubmitTrialStatusRequest();
    }

    @Test
    public void submitTrialStatus() throws Exception {

        String experimentID = TestConstants.BENCHFLOW_EXPERIMENT_ID;
        long trialNumber = 1;
        String trialID = experimentID + BenchFlowConstants.MODEL_ID_DELIMITER + trialNumber;
        request.setStatus(SUCCESS);

        Mockito.doNothing().when(experimentModelDAOMock).addTrialStatus(experimentID, trialNumber,
                                                                  request.getStatus());

        resource.submitTrialStatus(trialID, request);

    }

    @Test
    public void submitInvalidTrialStatus() throws Exception {

        String experimentID = TestConstants.BENCHFLOW_EXPERIMENT_ID;
        long trialNumber = 1;

        String trialID = experimentID + BenchFlowConstants.MODEL_ID_DELIMITER + trialNumber;

        request.setStatus(SUCCESS);

        Mockito.doThrow(BenchFlowExperimentIDDoesNotExistException.class).when(experimentModelDAOMock).addTrialStatus(experimentID,
                                                                                                                        trialNumber,
                                                                                                                        request.getStatus());

        exception.expect(InvalidTrialIDWebException.class);

        resource.submitTrialStatus(trialID, request);

    }

}