package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.response.GetPerformanceTestStatusResponse;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestorchestrator.exceptions.web.InvalidPerformanceTestIDWebException;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceTestModelDAO;
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
public class PerformanceTestStatusResource {

    public static String ROOT_PATH = "/performance-test/";

    private Logger logger = LoggerFactory.getLogger(PerformanceTestStatusResource.class.getSimpleName());

    private PerformanceTestModelDAO testModelDAO;

    public PerformanceTestStatusResource(PerformanceTestModelDAO testModelDAO) {
        this.testModelDAO = testModelDAO;
    }

    @GET
    @Path("/performance-test/{performanceTestID}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public GetPerformanceTestStatusResponse getPerformanceTestStatus(@PathParam("performanceTestID") final String performanceTestID) {

        logger.info("request received: GET " + ROOT_PATH + performanceTestID + "/status");

        // get the PerformanceTestModel from DAO

        PerformanceTestModel performanceTestModel = null;

        try {
            performanceTestModel = testModelDAO.getPerformanceTestModel(performanceTestID);
        } catch (PerformanceTestIDDoesNotExistException e) {
            throw new InvalidPerformanceTestIDWebException();
        }

        // TODO - return the full model

        return new GetPerformanceTestStatusResponse(performanceTestModel.getId());

    }
}
