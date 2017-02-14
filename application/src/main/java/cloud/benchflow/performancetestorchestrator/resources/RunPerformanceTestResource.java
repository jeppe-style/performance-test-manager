package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.RunPerformanceTestResponse;
import cloud.benchflow.performancetestorchestrator.tasks.RunPerformanceTestTask;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipInputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class RunPerformanceTestResource {

    private Logger logger = LoggerFactory.getLogger(RunPerformanceTestResource.class.getSimpleName());

    private ExecutorService taskExecutorService;

    public RunPerformanceTestResource(ExecutorService taskExecutorService) {
        this.taskExecutorService = taskExecutorService;
    }

    @POST
    @Path("/run-performance-test")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public RunPerformanceTestResponse runPerformanceTest(@FormDataParam("performanceTest") final InputStream performanceTestArchive) {

        logger.info("request received: /run-performance-test/");

        if (performanceTestArchive == null) {
            throw  new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        // TODO - validate performanceTestArchive content

        // TODO - return here if failure

        // create new RunPerformanceTestTask

        RunPerformanceTestTask task = new RunPerformanceTestTask(new ZipInputStream(performanceTestArchive));

        // get the ID

        String performanceTestID = task.getPerformanceTestID();

        // schedule new RunPerformanceTestTask

        taskExecutorService.submit(task);

        // return with ID

        return new RunPerformanceTestResponse(performanceTestID);

    }






}
