package cloud.benchflow.performancetestmanager.resources;

import cloud.benchflow.performancetestmanager.api.response.GetPerformanceTestStatusResponse;
import cloud.benchflow.performancetestmanager.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestmanager.exceptions.web.InvalidPerformanceTestIDWebException;
import cloud.benchflow.performancetestmanager.models.BenchFlowTestModel;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceTestModelDAO;
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
@Path("/performance-test/")
@Api(value = "performance-test")
public class PerformanceTestStatusResource {

    public static String ROOT_PATH = "/performance-test/";

    private Logger logger = LoggerFactory.getLogger(PerformanceTestStatusResource.class.getSimpleName());

    private PerformanceTestModelDAO testModelDAO;

    public PerformanceTestStatusResource(PerformanceTestModelDAO testModelDAO) {
        this.testModelDAO = testModelDAO;
    }

    @GET
    @Path("{performanceTestID}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public GetPerformanceTestStatusResponse getPerformanceTestStatus(@PathParam("performanceTestID") final String performanceTestID) {

        logger.info("request received: GET " + ROOT_PATH + performanceTestID + "/status");

        // get the BenchFlowTestModel from DAO

        BenchFlowTestModel benchFlowTestModel = null;

        try {
            benchFlowTestModel = testModelDAO.getPerformanceTestModel(performanceTestID);
        } catch (PerformanceTestIDDoesNotExistException e) {
            throw new InvalidPerformanceTestIDWebException();
        }

        // TODO - return the full model

        return new GetPerformanceTestStatusResponse(benchFlowTestModel.getId());

    }
}
