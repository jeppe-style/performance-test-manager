package cloud.benchflow.performancetestorchestrator.tasks;

import cloud.benchflow.performancetestorchestrator.definitions.PerformanceTestDefinition;
import cloud.benchflow.performancetestorchestrator.exceptions.PerformanceTestIDAlreadyExistsException;
import cloud.benchflow.performancetestorchestrator.services.external.MinioService;
import cloud.benchflow.performancetestorchestrator.services.external.PerformanceExperimentManagerService;
import cloud.benchflow.performancetestorchestrator.services.internal.PerformanceTestModelDAO;
import cloud.benchflow.performancetestorchestrator.util.PerformanceExperimentArchiveExtractor;

import java.io.IOException;
import java.util.zip.ZipInputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class RunPerformanceTestTask implements Runnable {

    private final String performanceTestID;
    private final ZipInputStream performanceTestArchive;
    private final PerformanceTestDefinition performanceTestDefinition;

    private final MinioService minioService;
    private final PerformanceTestModelDAO dao;
    private final PerformanceExperimentManagerService peManagerService;

    public RunPerformanceTestTask(ZipInputStream performanceTestArchive, MinioService minioService, PerformanceTestModelDAO dao, PerformanceExperimentManagerService peManagerService) throws IOException, PerformanceTestIDAlreadyExistsException {

        this.performanceTestArchive = performanceTestArchive;
        this.minioService = minioService;
        this.dao = dao;
        this.peManagerService = peManagerService;

        // get the PerformanceTestID from the archive
        String definition = PerformanceExperimentArchiveExtractor.extractPerformanceTestDefinition(performanceTestArchive);

        performanceTestDefinition = new PerformanceTestDefinition(definition);

        performanceTestID = performanceTestDefinition.getID();

        // add to DAO (make sure it doesn't already exist)
        dao.addPerformanceTestModel(performanceTestID);

    }

    public String getPerformanceTestID() {
        return performanceTestID;
    }

    @Override
    public void run() {

        // save performanceTestArchive to Minio
        minioService.savePerformanceTestArchive(performanceTestID, performanceTestArchive);

        // TODO - generate the performanceExperimentArchive and save to minio

        String performanceExperimentID = performanceTestID;

        minioService.savePerformanceExperimentArchive(performanceTestID, performanceExperimentID, performanceTestArchive);

        // run PE on PEManager
        peManagerService.runPerformanceExperiment(performanceExperimentID);

    }

}
