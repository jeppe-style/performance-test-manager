package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.request.ChangePerformanceTestStateRequest;
import cloud.benchflow.performancetestorchestrator.api.response.ChangePerformanceTestStateResponse;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestorchestrator.exceptions.web.InvalidPerformanceTestIDWebException;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceTestModelDAO;
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
@Path("/performance-test/")
public class PerformanceTestStateResource {

    public static String ROOT_PATH = "/performance-test/";

    private Logger logger = LoggerFactory.getLogger(PerformanceTestStateResource.class.getSimpleName());

    private PerformanceTestModelDAO testModelDAO;

    public PerformanceTestStateResource(PerformanceTestModelDAO testModelDAO) {
        this.testModelDAO = testModelDAO;
    }

    @PUT
    @Path("{performanceTestID}/state")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChangePerformanceTestStateResponse changePerformanceTestState(@PathParam("performanceTestID") final String performanceTestID,
                                                                         @NotNull @Valid final ChangePerformanceTestStateRequest stateRequest) {

        logger.info("request received: PUT " + ROOT_PATH + performanceTestID + "/state");

        // TODO - handle the actual state change (e.g. on PE Manager)

        // update the state
        PerformanceTestModel.PerformanceTestState newState = null;
        try {
            newState = testModelDAO.setPerformanceTestState(performanceTestID, stateRequest.getState());
        } catch (PerformanceTestIDDoesNotExistException e) {
            throw new InvalidPerformanceTestIDWebException();
        }

        // return the state as saved
        return new ChangePerformanceTestStateResponse(newState);

    }
}
