package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.RunPerformanceTestResponse;
import cloud.benchflow.performancetestorchestrator.models.PerformanceTestModel;
import cloud.benchflow.performancetestorchestrator.services.external.MinioManager;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class RunPerformanceTestResource {

    private Logger logger = LoggerFactory.getLogger(RunPerformanceTestResource.class.getName());

    private MinioManager minioManager = new MinioManager();

    private ExecutorService performanceTestExecutor;

    public RunPerformanceTestResource(ExecutorService performanceTestExecutor) {
        this.performanceTestExecutor = performanceTestExecutor;
    }

    @POST
    @Path("/runPerformanceTest/{performanceTestName}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public RunPerformanceTestResponse runPerformanceTest(@PathParam("performanceTestName") final String performanceTestName,
                                                         @FormDataParam("performanceTest") final InputStream performanceTestArchive) {

        logger.info("request received: /runPerformanceTest/" + performanceTestName);


        // save description to Minio
        minioManager.savePerformanceTestArchive(performanceTestArchive);

        // TODO
        // parse description to DSL-model

        // instantiate PerformanceTestModel (with ID) (save to DB)
        PerformanceTestModel performanceTestModel = new PerformanceTestModel(performanceTestName);

        // schedule new performance test on executor service
        performanceTestExecutor.submit(performanceTestModel);

        return performanceTestModel.getResponse();

    }






}
