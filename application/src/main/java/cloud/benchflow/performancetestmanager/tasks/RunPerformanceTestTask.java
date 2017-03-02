package cloud.benchflow.performancetestmanager.tasks;

import cloud.benchflow.performancetestmanager.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestmanager.services.external.MinioService;
import cloud.benchflow.performancetestmanager.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceExperimentModelDAO;
import cloud.benchflow.performancetestmanager.archive.PerformanceTestArchiveExtractor;
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
public class RunPerformanceTestTask implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RunPerformanceTestTask.class.getSimpleName());

    private final String performanceTestID;
    private final ZipInputStream testArchive;

    // services
    private final MinioService minioService;
    private final PerformanceExperimentManagerService peManagerService;
    private final PerformanceExperimentModelDAO experimentModelDAO;

    public RunPerformanceTestTask(String performanceTestID, MinioService minioService, PerformanceExperimentManagerService peManagerService, PerformanceExperimentModelDAO experimentModelDAO, ZipInputStream testArchive) {
        this.performanceTestID = performanceTestID;
        this.minioService = minioService;
        this.peManagerService = peManagerService;
        this.experimentModelDAO = experimentModelDAO;
        this.testArchive = testArchive;
    }

    @Override
    public void run() {

        try {

            logger.info("running task with ID " + performanceTestID);

            // extract contents
            InputStream definitionInputStream = new ByteArrayInputStream(
                    PerformanceTestArchiveExtractor.extractPerformanceTestDefinitionString(
                            testArchive).getBytes());

            InputStream deploymentDescriptorInputStream = PerformanceTestArchiveExtractor.extractDeploymentDescriptorInputStream(
                    testArchive);

            Map<String, InputStream> bpmnModelInputStreams = PerformanceTestArchiveExtractor.extractBPMNModelInputStreams(
                    testArchive);

            // save PT archive contents to Minio
            minioService.savePerformanceTestDefinition(performanceTestID, definitionInputStream);
            minioService.savePerformanceTestDeploymentDescriptor(performanceTestID, deploymentDescriptorInputStream);

            bpmnModelInputStreams.entrySet()
                    .forEach(entry -> minioService.savePerformanceTestBPMNModel(performanceTestID,
                                                                                entry.getKey(),
                                                                                entry.getValue()));

            // add new performance experiment model
            String performanceExperimentID = experimentModelDAO.addPerformanceExperiment(performanceTestID);

            // TODO - generate the PE definition
            InputStream peDefinition = definitionInputStream;

            // save PE defintion to minio
            minioService.savePerformanceExperimentDefinition(performanceExperimentID,
                                                             peDefinition);

            // run PE on PEManager
            peManagerService.runPerformanceExperiment(performanceExperimentID);

        } catch (IOException | PerformanceTestIDDoesNotExistException e) {
            // TODO - handle these exceptions properly (although should not happen)
            logger.error(e.toString());
        }

    }

}
