package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.request.SubmitTrialStatusRequest;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceExperimentIDDoesNotExistException;
import cloud.benchflow.performancetestorchestrator.exceptions.web.InvalidPerformanceTrialIDWebException;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceExperimentModelDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class TrialStatusResource {

    public static String ROOT_PATH = "/performance-experiment-trial/";

    private Logger logger = LoggerFactory.getLogger(TrialStatusResource.class.getSimpleName());

    private PerformanceExperimentModelDAO experimentModelDAO;

    public TrialStatusResource(PerformanceExperimentModelDAO experimentModelDAO) {
        this.experimentModelDAO = experimentModelDAO;
    }

    @POST
    @Path("/performance-experiment-trial/{trialID}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public void submitTrialStatus(@PathParam("trialID") final String trialID,
                                  @NotNull @Valid final SubmitTrialStatusRequest statusRequest) {

        logger.info("request received: POST " + ROOT_PATH + trialID +
                            "/status : " + statusRequest.getStatus().name());

        String performanceExperimentID = trialID.substring(0, trialID.lastIndexOf("."));
        long trialNumber = Long.parseLong(trialID.substring(trialID.lastIndexOf(".") + 1));

        try {

            experimentModelDAO.addTrialStatus(performanceExperimentID, trialNumber, statusRequest.getStatus());

        } catch (PerformanceExperimentIDDoesNotExistException e) {
            throw new InvalidPerformanceTrialIDWebException();
        }

    }

}
