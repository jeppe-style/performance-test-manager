package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.response.RunPerformanceTestResponse;
import cloud.benchflow.performancetestorchestrator.exceptions.web.PerformanceTestIDExistsException;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDAlreadyExistsException;
import cloud.benchflow.performancetestorchestrator.services.external.MinioService;
import cloud.benchflow.performancetestorchestrator.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestModelDAO;
import cloud.benchflow.performancetestorchestrator.tasks.RunPerformanceTestTask;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipInputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class RunPerformanceTestResource {

    private final ExecutorService taskExecutorService;
    private final MinioService minioService;
    private final PerformanceTestModelDAO dao;
    private final PerformanceExperimentManagerService peManagerService;
    private Logger logger = LoggerFactory.getLogger(RunPerformanceTestResource.class.getSimpleName());

    public RunPerformanceTestResource(ExecutorService taskExecutorService, MinioService minioService, PerformanceTestModelDAO dao, PerformanceExperimentManagerService peManagerService) {
        this.taskExecutorService = taskExecutorService;
        this.minioService = minioService;
        this.dao = dao;
        this.peManagerService = peManagerService;
    }

    @POST
    @Path("/run-performance-test")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public RunPerformanceTestResponse runPerformanceTest(@FormDataParam("performanceTest") final InputStream performanceTestArchive) {

        logger.info("request received: /run-performance-test/");

        if (performanceTestArchive == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        // TODO - validate performanceTestArchive content

        // TODO - return here if failure
//        throw new InvalidTestArchiveException();

        // create new RunPerformanceTestTask and schedule it
        try {

            RunPerformanceTestTask task = new RunPerformanceTestTask(new ZipInputStream(performanceTestArchive),
                                                                     minioService,
                                                                     dao,
                                                                     peManagerService);

            String performanceTestID = task.getPerformanceTestID();

            taskExecutorService.submit(task);

            return new RunPerformanceTestResponse(performanceTestID);


        } catch (IOException e) {
            // should not be thrown since we already verified the archive before the try clause
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        } catch (PerformanceTestIDAlreadyExistsException exception) {
            throw new PerformanceTestIDExistsException();
        }

    }


}
