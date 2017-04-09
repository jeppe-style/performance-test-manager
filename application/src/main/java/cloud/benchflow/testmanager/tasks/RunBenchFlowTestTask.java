package cloud.benchflow.testmanager.tasks;

import cloud.benchflow.testmanager.exceptions.BenchFlowTestIDDoesNotExistException;
import cloud.benchflow.testmanager.services.external.MinioService;
import cloud.benchflow.testmanager.services.external.BenchFlowExperimentManagerService;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowExperimentModelDAO;
import cloud.benchflow.testmanager.archive.BenchFlowTestArchiveExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class RunBenchFlowTestTask implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RunBenchFlowTestTask.class.getSimpleName());

    private final String testID;
    private final ZipInputStream testArchive;

    // services
    private final MinioService minioService;
    private final BenchFlowExperimentManagerService experimentManagerService;
    private final BenchFlowExperimentModelDAO experimentModelDAO;

    public RunBenchFlowTestTask(String testID, MinioService minioService, BenchFlowExperimentManagerService experimentManagerService, BenchFlowExperimentModelDAO experimentModelDAO, ZipInputStream testArchive) {
        this.testID = testID;
        this.minioService = minioService;
        this.experimentManagerService = experimentManagerService;
        this.experimentModelDAO = experimentModelDAO;
        this.testArchive = testArchive;
    }

    @Override
    public void run() {

        try {

            logger.info("running test task with ID " + testID);

            // extract contents
            InputStream definitionInputStream = new ByteArrayInputStream(
                    BenchFlowTestArchiveExtractor.extractBenchFlowTestDefinitionString(
                            testArchive).getBytes());

            InputStream deploymentDescriptorInputStream = BenchFlowTestArchiveExtractor.extractDeploymentDescriptorInputStream(
                    testArchive);

            // TODO - handle different SUT types

            Map<String, InputStream> bpmnModelInputStreams = BenchFlowTestArchiveExtractor.extractBPMNModelInputStreams(
                    testArchive);

            // save PT archive contents to Minio
            minioService.saveTestDefinition(testID, definitionInputStream);
            minioService.saveTestDeploymentDescriptor(testID, deploymentDescriptorInputStream);

            bpmnModelInputStreams.entrySet()
                    .forEach(entry -> minioService.saveTestBPMNModel(testID,
                                                                                entry.getKey(),
                                                                                entry.getValue()));

            // add new experiment model
            String experimentID = experimentModelDAO.addExperiment(testID);

            // TODO - generate the PE definition
            InputStream peDefinition = definitionInputStream;

            // save PE defintion to minio
            minioService.saveExperimentDefinition(experimentID,
                                                             peDefinition);

            // run PE on PEManager
            experimentManagerService.runBenchFlowExperiment(experimentID);

        } catch (IOException | BenchFlowTestIDDoesNotExistException e) {
            // TODO - handle these exceptions properly (although should not happen)
            logger.error(e.toString());
        }

    }

}
