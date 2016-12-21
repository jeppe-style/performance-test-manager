package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.PerformanceTestStatusResponse;
import cloud.benchflow.performancetestorchestrator.services.internal.DataStore;
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

    public static String COMPLETED = "completed";
    public static String RUNNING = "running";

    @GET
    @Path("{performanceTestID}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public PerformanceTestStatusResponse getPerformanceTestStatus(@PathParam("performanceTestID") final String performanceTestID) {

        logger.info("request received: /" + performanceTestID + "/status");


        // get the status

        String status = DataStore.getPerformanceTestStatus(performanceTestID) ? COMPLETED : RUNNING;


        return new PerformanceTestStatusResponse(status);

    }
}
