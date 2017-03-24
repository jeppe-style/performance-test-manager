package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.request.ChangeBenchFlowTestStateRequest;
import cloud.benchflow.testmanager.api.response.ChangeBenchFlowTestStateResponse;
import cloud.benchflow.testmanager.exceptions.BenchFlowTestIDDoesNotExistException;
import cloud.benchflow.testmanager.exceptions.web.InvalidBenchFlowTestIDWebException;
import cloud.benchflow.testmanager.models.BenchFlowTestModel;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowTestModelDAO;
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
@Path("/benchflow-test/")
@Api(value = "benchflow-test")
public class BenchFlowTestStateResource {

    public static String ROOT_PATH = "/benchflow-test/";

    private Logger logger = LoggerFactory.getLogger(BenchFlowTestStateResource.class.getSimpleName());

    private BenchFlowTestModelDAO testModelDAO;

    public BenchFlowTestStateResource(BenchFlowTestModelDAO testModelDAO) {
        this.testModelDAO = testModelDAO;
    }

    @PUT
    @Path("{benchFlowTestID}/state")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChangeBenchFlowTestStateResponse changeBenchFlowTestState(@PathParam("benchFlowTestID") final String testID,
                                                                     @NotNull @Valid final ChangeBenchFlowTestStateRequest stateRequest) {

        logger.info("request received: PUT " + ROOT_PATH + testID + "/state");

        // TODO - handle the actual state change (e.g. on PE Manager)

        // update the state
        BenchFlowTestModel.BenchFlowTestState newState = null;
        try {
            newState = testModelDAO.setTestState(testID, stateRequest.getState());
        } catch (BenchFlowTestIDDoesNotExistException e) {
            throw new InvalidBenchFlowTestIDWebException();
        }

        // return the state as saved
        return new ChangeBenchFlowTestStateResponse(newState);

    }
}
