package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.response.GetPerformanceTestStatusResponse;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestorchestrator.exceptions.web.InvalidPerformanceTestIDException;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestModelDAO;
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

    private Logger logger = LoggerFactory.getLogger(PerformanceTestStatusResource.class.getSimpleName());

    private PerformanceTestModelDAO dao;

    public PerformanceTestStatusResource(PerformanceTestModelDAO dao) {
        this.dao = dao;
    }

    @GET
    @Path("{performanceTestID}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public GetPerformanceTestStatusResponse getPerformanceTestStatus(@PathParam("performanceTestID") final String performanceTestID) {

        logger.info("request received: GET /" + performanceTestID + "/status");

        // get the PerformanceTestModel from DAO

        PerformanceTestModel performanceTestModel = null;

        try {
            performanceTestModel = dao.getPerformanceTestModel(performanceTestID);
        } catch (PerformanceTestIDDoesNotExistException e) {
            throw new InvalidPerformanceTestIDException();
        }

        return new GetPerformanceTestStatusResponse(performanceTestModel.getPerformanceTestID());

    }
}
