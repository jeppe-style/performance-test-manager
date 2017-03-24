package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.request.SubmitTrialStatusRequest;
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
 *         created on 13.02.17.
 */
@Path("/benchflow-experiment-trial/")
@Api(value = "benchflow-experiment-trial")
public class TrialStatusResource {

    public static String ROOT_PATH = "/benchflow-experiment-trial/";

    private Logger logger = LoggerFactory.getLogger(TrialStatusResource.class.getSimpleName());

    private BenchFlowExperimentModelDAO experimentModelDAO;

    public TrialStatusResource(BenchFlowExperimentModelDAO experimentModelDAO) {
        this.experimentModelDAO = experimentModelDAO;
    }

    @PUT
    @Path("{trialID}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public void submitTrialStatus(@PathParam("trialID") final String trialID,
                                             @NotNull @Valid final SubmitTrialStatusRequest statusRequest) {

        logger.info("request received: POST " + ROOT_PATH + trialID +
                            "/status : " + statusRequest.getStatus().name());

        String experimentID = trialID.substring(0, trialID.lastIndexOf("."));
        long trialNumber = Long.parseLong(trialID.substring(trialID.lastIndexOf(".") + 1));

        try {

            experimentModelDAO.addTrialStatus(experimentID, trialNumber, statusRequest.getStatus());

        } catch (BenchFlowExperimentIDDoesNotExistException e) {
            throw new InvalidTrialIDWebException();
        }

    }

}
