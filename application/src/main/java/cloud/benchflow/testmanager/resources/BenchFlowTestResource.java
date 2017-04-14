package cloud.benchflow.testmanager.resources;

import cloud.benchflow.dsl.BenchFlowDSL;
import cloud.benchflow.dsl.definition.BenchFlowTest;
import cloud.benchflow.testmanager.api.response.RunBenchFlowTestResponse;
import cloud.benchflow.testmanager.archive.BenchFlowTestArchiveExtractor;
import cloud.benchflow.testmanager.constants.BenchFlowConstants;
import cloud.benchflow.testmanager.exceptions.InvalidTestArchiveException;
import cloud.benchflow.testmanager.exceptions.UserIDAlreadyExistsException;
import cloud.benchflow.testmanager.exceptions.web.InvalidTestArchiveWebException;
import cloud.benchflow.testmanager.services.external.BenchFlowExperimentManagerService;
import cloud.benchflow.testmanager.services.external.MinioService;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowExperimentModelDAO;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowTestModelDAO;
import cloud.benchflow.testmanager.services.internal.dao.UserDAO;
import cloud.benchflow.testmanager.tasks.RunBenchFlowDemoTestTask;
import cloud.benchflow.testmanager.tasks.RunBenchFlowTestTask;
import io.swagger.annotations.Api;
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

@Path("/benchflow-test")
@Api(value = "benchflow-test")
public class BenchFlowTestResource {

    // TODO - decide where to put or encapsulate
    public static final String ROOT_PATH = "/benchflow-test";
    private final ExecutorService taskExecutorService;
    private final MinioService minioService;
    private final BenchFlowTestModelDAO testModelDAO;
    private final BenchFlowExperimentModelDAO experimentModelDAO;
    private final UserDAO userDAO;
    private final BenchFlowExperimentManagerService peManagerService;
    private Logger logger = LoggerFactory.getLogger(BenchFlowTestResource.class.getSimpleName());

    public BenchFlowTestResource(ExecutorService taskExecutorService, MinioService minioService, BenchFlowTestModelDAO testModelDAO, BenchFlowExperimentModelDAO experimentModelDAO, UserDAO userDAO, BenchFlowExperimentManagerService peManagerService) {
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
    public RunBenchFlowTestResponse runBenchFlowTest(@FormDataParam("benchFlowTestBundle") final InputStream benchFlowTestBundle) {

        logger.info("request received: " + ROOT_PATH);

        if (benchFlowTestBundle == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        ZipInputStream archiveZipInputStream = new ZipInputStream(benchFlowTestBundle);

        // TODO - check valid user
        if (!userDAO.userExists(BenchFlowConstants.BENCHFLOW_USER)) {

            try {
                userDAO.addUser(BenchFlowConstants.BENCHFLOW_USER.getUsername());
            } catch (UserIDAlreadyExistsException e) {
                // since we already checked that the user doesn't exist it cannot happen
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        try {

            // validate archive
            // Get the contents of archive and check if valid Test ID
            String testDefinitionString = BenchFlowTestArchiveExtractor.extractBenchFlowTestDefinitionString(
                    archiveZipInputStream);

            if (testDefinitionString == null)
                throw new InvalidTestArchiveException();

            BenchFlowTest benchFlowTest = BenchFlowDSL.testFromYaml(testDefinitionString).get();

            InputStream deploymentDescriptorInputStream = BenchFlowTestArchiveExtractor.extractDeploymentDescriptorInputStream(
                    archiveZipInputStream);
            Map<String, InputStream> bpmnModelsInputStream = BenchFlowTestArchiveExtractor.extractBPMNModelInputStreams(
                    archiveZipInputStream);

            if (deploymentDescriptorInputStream == null || bpmnModelsInputStream.size() == 0) {
                throw new InvalidTestArchiveException();
            }

            // save new experiment
            String testID = testModelDAO.addTestModel(benchFlowTest.name(), BenchFlowConstants.BENCHFLOW_USER);

            // create new task and run it
            RunBenchFlowTestTask task = new RunBenchFlowTestTask(
                    testID,
                    minioService,
                    peManagerService,
                    experimentModelDAO,
                    testDefinitionString,
                    deploymentDescriptorInputStream,
                    bpmnModelsInputStream
            );


            // TODO - should go into a stateless queue (so that we can recover)
            taskExecutorService.submit(task);

            return new RunBenchFlowTestResponse(testID);

        } catch (IOException | InvalidTestArchiveException e) {
            throw new InvalidTestArchiveWebException();
        }


    }

    @Path("/demo")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public RunBenchFlowTestResponse runBenchFlowTestDemo(@FormDataParam("benchFlowTestBundle") final InputStream benchFlowTestBundle) {

        // TODO - remove after demo

        logger.info("request received: " + ROOT_PATH + "/demo");

        if (benchFlowTestBundle == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        ZipInputStream archiveZipInputStream = new ZipInputStream(benchFlowTestBundle);

        // TODO - check valid user
        if (!userDAO.userExists(BenchFlowConstants.BENCHFLOW_USER)) {

            try {
                userDAO.addUser(BenchFlowConstants.BENCHFLOW_USER.getUsername());
            } catch (UserIDAlreadyExistsException e) {
                // since we already checked that the user doesn't exist it cannot happen
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        try {

            // validate archive

            // TODO - validate benchFlowTestArchive content in detail

            // Get the contents of archive and check if valid Test ID
            String testDefinitionYamlString = BenchFlowTestArchiveExtractor.extractBenchFlowTestDefinitionString(
                    archiveZipInputStream);

            if (testDefinitionYamlString == null)
                throw new InvalidTestArchiveException();

            // TODO - get real definition
//            BenchFlowTestDefinition testDefinition = new BenchFlowTestDefinition(testDefinitionYamlString);
//            String testName = testDefinition.getName();
            BenchFlowTest benchFlowTest = BenchFlowDSL.testFromYaml(testDefinitionYamlString).get();
            String testName = benchFlowTest.name();

            InputStream deploymentDescriptorInputStream = BenchFlowTestArchiveExtractor.extractDeploymentDescriptorInputStream(
                    archiveZipInputStream);
            Map<String, InputStream> bpmnModelsInputStream = BenchFlowTestArchiveExtractor.extractBPMNModelInputStreams(
                    archiveZipInputStream);

            if (deploymentDescriptorInputStream == null || bpmnModelsInputStream.size() == 0) {
                throw new InvalidTestArchiveException();
            }


            // save new experiment
            String testID = testModelDAO.addTestModel(testName, BenchFlowConstants.BENCHFLOW_USER);

            // create new task and run it
            RunBenchFlowDemoTestTask task = new RunBenchFlowDemoTestTask(
                    testID,
                    testDefinitionYamlString,
                    deploymentDescriptorInputStream,
                    bpmnModelsInputStream,
                    minioService,
                    peManagerService,
                    experimentModelDAO
            );


            // TODO - should go into a stateless queue (so that we can recover)
            taskExecutorService.submit(task);

            return new RunBenchFlowTestResponse(testID);

        } catch (IOException | InvalidTestArchiveException e) {
            throw new InvalidTestArchiveWebException();
        }


    }

    private String validatePTArchive(ZipInputStream archiveZipInputStream) throws IOException, InvalidTestArchiveException {

        // TODO - validate benchFlowTestArchive content in detail

        // Get the contents of archive and check if valid Test ID
        String testDefinitionString = BenchFlowTestArchiveExtractor.extractBenchFlowTestDefinitionString(
                archiveZipInputStream);

        if (testDefinitionString == null)
            throw new InvalidTestArchiveException();

        BenchFlowTest benchFlowTest = BenchFlowDSL.testFromYaml(testDefinitionString).get();

        InputStream deploymentDescriptorInputStream = BenchFlowTestArchiveExtractor.extractDeploymentDescriptorInputStream(
                archiveZipInputStream);
        Map<String, InputStream> bpmnModelsInputStream = BenchFlowTestArchiveExtractor.extractBPMNModelInputStreams(
                archiveZipInputStream);

        if (deploymentDescriptorInputStream == null || bpmnModelsInputStream.size() == 0) {
            throw new InvalidTestArchiveException();
        }

        return benchFlowTest.name();
    }


}
