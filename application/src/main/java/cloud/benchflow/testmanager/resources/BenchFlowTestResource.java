package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.request.ChangeBenchFlowTestStateRequest;
import cloud.benchflow.testmanager.api.response.ChangeBenchFlowTestStateResponse;
import cloud.benchflow.testmanager.constants.BenchFlowConstants;
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

import static cloud.benchflow.testmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER;
import static cloud.benchflow.testmanager.constants.BenchFlowConstants.MODEL_ID_DELIMITER_REGEX;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
@Path("/{username}/{testName}/{testNumber}")
@Api(value = "benchflow-test")
public class BenchFlowTestResource {

    public static String STATE_PATH = "/state";
    public static String STATUS_PATH = "/status";

    private Logger logger = LoggerFactory.getLogger(BenchFlowTestResource.class.getSimpleName());

    private BenchFlowTestModelDAO testModelDAO;

    public BenchFlowTestResource(BenchFlowTestModelDAO testModelDAO) {
        this.testModelDAO = testModelDAO;
    }

    @PUT
    @Path("/state")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChangeBenchFlowTestStateResponse changeBenchFlowTestState(@PathParam("username") String username,
                                                                     @PathParam("testName") String testName,
                                                                     @PathParam("testNumber") String testNumber,
                                                                     @NotNull @Valid final ChangeBenchFlowTestStateRequest stateRequest) {

        logger.info("request received: PUT /" + username + "/" + testName + "/" + testNumber + STATE_PATH);

        // TODO - handle the actual state change (e.g. on PE Manager)

        String testID = BenchFlowConstants.getTestID(username, testName, testNumber);

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

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public BenchFlowTestModel getBenchFlowTestStatus(@PathParam("username") String username,
                                                     @PathParam("testName") String testName,
                                                     @PathParam("testNumber") String testNumber) {

        logger.info("request received: GET /" + username + "/" + testName + "/" + testNumber + STATUS_PATH);

        // get the BenchFlowTestModel from DAO

        String testID = BenchFlowConstants.getTestID(username, testName, testNumber);

        BenchFlowTestModel benchFlowTestModel = null;

        try {
            benchFlowTestModel = testModelDAO.getTestModel(testID);
        } catch (BenchFlowTestIDDoesNotExistException e) {
            throw new InvalidBenchFlowTestIDWebException();
        }

        return benchFlowTestModel;

    }

}
