package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.request.SubmitTrialStatusRequest;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestorchestrator.exceptions.web.InvalidPerformanceTestIDException;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestModelDAO;
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

    private Logger logger = LoggerFactory.getLogger(TrialStatusResource.class.getSimpleName());

    private PerformanceTestModelDAO dao;

    public TrialStatusResource(PerformanceTestModelDAO dao) {
        this.dao = dao;
    }

    @POST
    @Path("/{performanceTestID}/{performanceExperimentID}/{trialID}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public void submitTrialStatus(@PathParam("performanceTestID") final String performanceTestID,
                                  @PathParam("performanceExperimentID") final String performanceExperimentID,
                                  @PathParam("trialID") final String trialID,
                                  @NotNull @Valid final SubmitTrialStatusRequest statusRequest) {

        logger.info("request received: POST /" + performanceTestID +
                            "/" + performanceExperimentID +
                            "/" + trialID +
                            "/status : " + statusRequest.getStatus().name());

        try {

            dao.addTrialStatus(performanceTestID, performanceExperimentID, trialID, statusRequest.getStatus());

        } catch (PerformanceTestIDDoesNotExistException e) {
            throw new InvalidPerformanceTestIDException();
        }

    }

}
