package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.request.SubmitTrialStatusRequest;
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

import static cloud.benchflow.testmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER;
import static cloud.benchflow.testmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER_REGEX;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
@Path("/{username}/{testName}/{testNumber}/{experimentNumber}/{trialNumber}")
@Api(value = "benchflow-trial")
public class BenchFlowTrialResource {

    public static String STATUS_PATH = "/status";

    private Logger logger = LoggerFactory.getLogger(BenchFlowTrialResource.class.getSimpleName());

    private BenchFlowExperimentModelDAO experimentModelDAO;

    public BenchFlowTrialResource(BenchFlowExperimentModelDAO experimentModelDAO) {
        this.experimentModelDAO = experimentModelDAO;
    }

    @PUT
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public void submitTrialStatus(@PathParam("username") String username,
            @PathParam("testName") String testName,
            @PathParam("testNumber") String testNumber,
            @PathParam("experimentNumber") String experimentNumber,
            @PathParam("trialNumber") String trialNumber,
            @NotNull @Valid final SubmitTrialStatusRequest statusRequest) {

        logger.info("request received: POST /" + username + "/" + testName + "/" + testNumber + "/" + experimentNumber + "/" + trialNumber +
                            STATUS_PATH + " : " + statusRequest.getStatus().name());

        String experimentID = BenchFlowConstants.getExperimentID(username, testName, testNumber, experimentNumber);

        try {
            experimentModelDAO.addTrialStatus(experimentID, Long.parseLong(trialNumber), statusRequest.getStatus());

        } catch (BenchFlowExperimentIDDoesNotExistException e) {
            throw new InvalidTrialIDWebException();
        }

    }

}
