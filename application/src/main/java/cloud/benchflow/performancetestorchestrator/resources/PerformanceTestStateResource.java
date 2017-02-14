package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.ChangePerformanceTestStateResponse;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class PerformanceTestStateResource {

    private Logger logger = LoggerFactory.getLogger(PerformanceTestStateResource.class.getSimpleName());

    // TODO

    @PUT
    @Path("{performanceTestID}/state")
    @Produces(MediaType.APPLICATION_JSON)
    public ChangePerformanceTestStateResponse changePerformanceTestState(@PathParam("performanceTestID") final String performanceTestID,
                                                                         @FormDataParam("performanceTest") final PerformanceTestModel.PerformanceTestState state) {

        logger.info("request received: PUT /" + performanceTestID + "/state");

        // TODO - get the PerformanceTestModel from DAO

        // TODO - update the state

        return new ChangePerformanceTestStateResponse();

    }
}
