package cloud.benchflow.testmanager.resources;

import cloud.benchflow.testmanager.api.response.GetBenchFlowTestStatusResponse;
import cloud.benchflow.testmanager.exceptions.BenchFlowTestIDDoesNotExistException;
import cloud.benchflow.testmanager.exceptions.web.InvalidBenchFlowTestIDWebException;
import cloud.benchflow.testmanager.models.BenchFlowTestModel;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowTestModelDAO;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
@Path("/benchflow-test/")
@Api(value = "benchflow-test")
public class BenchFlowTestStatusResource {

    public static String ROOT_PATH = "/benchflow-test/";

    private Logger logger = LoggerFactory.getLogger(BenchFlowTestStatusResource.class.getSimpleName());

    private BenchFlowTestModelDAO testModelDAO;

    public BenchFlowTestStatusResource(BenchFlowTestModelDAO testModelDAO) {
        this.testModelDAO = testModelDAO;
    }

    @GET
    @Path("{benchFlowTestID}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public GetBenchFlowTestStatusResponse getBenchFlowTestStatus(@PathParam("benchFlowTestID") final String testID) {

        logger.info("request received: GET " + ROOT_PATH + testID + "/status");

        // get the BenchFlowTestModel from DAO

        BenchFlowTestModel benchFlowTestModel = null;

        try {
            benchFlowTestModel = testModelDAO.getTestModel(testID);
        } catch (BenchFlowTestIDDoesNotExistException e) {
            throw new InvalidBenchFlowTestIDWebException();
        }

        // TODO - return the full model

        return new GetBenchFlowTestStatusResponse(benchFlowTestModel.getId());

    }
}
