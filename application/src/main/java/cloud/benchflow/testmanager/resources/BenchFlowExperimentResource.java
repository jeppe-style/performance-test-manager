package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.request.SubmitExperimentStateRequest;
import cloud.benchflow.testmanager.constants.BenchFlowConstants;
import cloud.benchflow.testmanager.exceptions.BenchFlowExperimentIDDoesNotExistException;
import cloud.benchflow.testmanager.exceptions.web.InvalidTrialIDWebException;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowExperimentModelDAO;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 2017-04-16
 */
@Path("/{username}/{testName}/{testNumber}/{experimentNumber}")
@Api(value = "benchflow-experiment")
public class BenchFlowExperimentResource {

    public static String STATE_PATH = "/state";

    private Logger logger = LoggerFactory.getLogger(BenchFlowExperimentResource.class.getSimpleName());

    private BenchFlowExperimentModelDAO experimentModelDAO;

    public BenchFlowExperimentResource(BenchFlowExperimentModelDAO experimentModelDAO) {
        this.experimentModelDAO = experimentModelDAO;
    }

    @PUT
    @Path("/state")
    @Consumes(MediaType.APPLICATION_JSON)
    public void submitExperimentStatus(@PathParam("username") String username,
                                       @PathParam("testName") String testName,
                                       @PathParam("testNumber") String testNumber,
                                       @PathParam("experimentNumber") String experimentNumber,
                                       @NotNull @Valid final SubmitExperimentStateRequest stateRequest) {

        logger.info("request received: POST /" + username + "/" + testName + "/" + testNumber + "/" + experimentNumber +
                STATE_PATH + " : " + stateRequest.getState().name());

        String experimentID = BenchFlowConstants.getExperimentID(username, testName, testNumber, experimentNumber);

        try {
            experimentModelDAO.setExperimentState(experimentID, stateRequest.getState());
        } catch (BenchFlowExperimentIDDoesNotExistException e) {
            throw new InvalidTrialIDWebException();
        }

    }

}
