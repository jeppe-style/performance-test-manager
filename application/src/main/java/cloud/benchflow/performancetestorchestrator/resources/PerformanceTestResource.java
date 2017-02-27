package cloud.benchflow.performancetestorchestrator.resources;

import cloud.benchflow.performancetestorchestrator.api.response.RunPerformanceTestResponse;
import cloud.benchflow.performancetestorchestrator.definitions.PerformanceTestDefinition;
import cloud.benchflow.performancetestorchestrator.exceptions.InvalidTestArchiveException;
import cloud.benchflow.performancetestorchestrator.exceptions.UserIDAlreadyExistsException;
import cloud.benchflow.performancetestorchestrator.exceptions.web.InvalidTestArchiveWebException;
import cloud.benchflow.performancetestorchestrator.services.external.MinioService;
import cloud.benchflow.performancetestorchestrator.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceExperimentModelDAO;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.PerformanceTestModelDAO;
import cloud.benchflow.performancetestorchestrator.services.internal.dao.UserDAO;
import cloud.benchflow.performancetestorchestrator.tasks.RunPerformanceTestTask;
import cloud.benchflow.performancetestorchestrator.constants.BenchFlowConstants;
import cloud.benchflow.performancetestorchestrator.archive.PerformanceTestArchiveExtractor;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipInputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */

@Path("/performance-test")
public class PerformanceTestResource {

    public static String ROOT_PATH = "/performance-test";

    private Logger logger = LoggerFactory.getLogger(PerformanceTestResource.class.getSimpleName());

    private final ExecutorService taskExecutorService;
    private final MinioService minioService;
    private final PerformanceTestModelDAO testModelDAO;
    private final PerformanceExperimentModelDAO experimentModelDAO;
    private final UserDAO userDAO;
    private final PerformanceExperimentManagerService peManagerService;

    public PerformanceTestResource(ExecutorService taskExecutorService, MinioService minioService, PerformanceTestModelDAO testModelDAO, PerformanceExperimentModelDAO experimentModelDAO, UserDAO userDAO, PerformanceExperimentManagerService peManagerService) {
        this.taskExecutorService = taskExecutorService;
        this.minioService = minioService;
        this.testModelDAO = testModelDAO;
        this.experimentModelDAO = experimentModelDAO;
        this.userDAO = userDAO;
        this.peManagerService = peManagerService;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public RunPerformanceTestResponse runPerformanceTest(@FormDataParam("performanceTest") final InputStream performanceTestArchive) {

        logger.info("request received: " + ROOT_PATH);

        if (performanceTestArchive == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        ZipInputStream archiveZipInputStream = new ZipInputStream(performanceTestArchive);

        // TODO - check valid user
        if (!userDAO.userExists(BenchFlowConstants.BENCH_FLOW_USER)) {

            try {
                userDAO.addUser(BenchFlowConstants.BENCH_FLOW_USER.getUsername());
            } catch (UserIDAlreadyExistsException e) {
                // since we already checked that the user doesn't exist it cannot happen
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        try {

            // validate archive
            String testName = validatePTArchive(archiveZipInputStream);

            // save new experiment
            String performanceTestID = testModelDAO.addPerformanceTestModel(testName, BenchFlowConstants.BENCH_FLOW_USER);

            // create new task and run it
            RunPerformanceTestTask task = new RunPerformanceTestTask(performanceTestID, minioService,
                                                                     peManagerService, experimentModelDAO, archiveZipInputStream);


            // TODO - should go into a stateless queue (so that we can recover)
            taskExecutorService.submit(task);

            return new RunPerformanceTestResponse(performanceTestID);

        } catch (IOException | InvalidTestArchiveException e) {
            throw new InvalidTestArchiveWebException();
        }


    }

    private String validatePTArchive(ZipInputStream archiveZipInputStream) throws IOException, InvalidTestArchiveException {

        // TODO - validate performanceTestArchive content in detail

        // Get the contents of archive and check if valid Performance Test ID
        String ptDefinitionString = PerformanceTestArchiveExtractor.extractPerformanceTestDefinitionString(
                archiveZipInputStream);

        if (ptDefinitionString == null)
            throw new InvalidTestArchiveException();

        // TODO - get real definition
        PerformanceTestDefinition ptDefinition = new PerformanceTestDefinition(ptDefinitionString);

        InputStream deploymentDescriptorInputStream = PerformanceTestArchiveExtractor.extractDeploymentDescriptorInputStream(
                archiveZipInputStream);
        Map<String, InputStream> bpmnModelsInputStream = PerformanceTestArchiveExtractor.extractBPMNModelInputStreams(
                archiveZipInputStream);

        if (deploymentDescriptorInputStream == null || bpmnModelsInputStream.size() == 0) {
            throw new InvalidTestArchiveException();
        }

        return ptDefinition.getName();
    }


}
