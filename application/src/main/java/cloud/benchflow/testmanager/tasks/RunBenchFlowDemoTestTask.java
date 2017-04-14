package cloud.benchflow.testmanager.tasks;

import cloud.benchflow.dsl.BenchFlowDSL;
import cloud.benchflow.dsl.DemoConverter;
import cloud.benchflow.dsl.definition.BenchFlowExperiment;
import cloud.benchflow.dsl.definition.BenchFlowTest;
import cloud.benchflow.testmanager.constants.BenchFlowConstants;
import cloud.benchflow.testmanager.exceptions.BenchFlowTestIDDoesNotExistException;
import cloud.benchflow.testmanager.services.external.BenchFlowExperimentManagerService;
import cloud.benchflow.testmanager.services.external.MinioService;
import cloud.benchflow.testmanager.services.internal.dao.BenchFlowExperimentModelDAO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 2017-03-28
 */
public class RunBenchFlowDemoTestTask implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RunBenchFlowDemoTestTask.class.getSimpleName());

    private final String testID;
    private final String testDefinitionYamlString;
    private InputStream deploymentDescriptorInputStream;
    private Map<String, InputStream> bpmnModelInputStreams;

    // services
    private final MinioService minioService;
    private final BenchFlowExperimentManagerService experimentManagerService;
    private final BenchFlowExperimentModelDAO experimentModelDAO;

    public RunBenchFlowDemoTestTask(String testID, String testDefinitionYamlString, InputStream deploymentDescriptorInputStream, Map<String, InputStream> bpmnModelInputStreams, MinioService minioService, BenchFlowExperimentManagerService experimentManagerService, BenchFlowExperimentModelDAO experimentModelDAO) {
        this.testID = testID;
        this.testDefinitionYamlString = testDefinitionYamlString;
        this.deploymentDescriptorInputStream = deploymentDescriptorInputStream;
        this.bpmnModelInputStreams = bpmnModelInputStreams;
        this.minioService = minioService;
        this.experimentManagerService = experimentManagerService;
        this.experimentModelDAO = experimentModelDAO;
    }

    @Override
    public void run() {


        try {

            logger.info("running demo test task with ID " + testID);

            // extract test bundle contents
            InputStream definitionInputStream = new ByteArrayInputStream(testDefinitionYamlString.getBytes());


            // TODO - handle different SUT types

            // save test bundle contents to minio
            logger.info("saving to minio for test " + testID);
            minioService.saveTestDefinition(testID, definitionInputStream);
            minioService.saveTestDeploymentDescriptor(testID, deploymentDescriptorInputStream);

            bpmnModelInputStreams.forEach((key, value) -> minioService.saveTestBPMNModel(testID,
                    key,
                    value));

            // verify test definition
            logger.info("verifying test definition for " + testID);
            // TODO - maybe this should be done in separate verification method?
            BenchFlowTest test = BenchFlowDSL.testFromYaml(testDefinitionYamlString).get();

            // generate experiment definition from test definition
            BenchFlowExperiment experiment = BenchFlowDSL.experimentFromTestYaml(testDefinitionYamlString).get();
            // TODO - convert to current version of PE definition
            String oldExperimentDefinitionYaml = DemoConverter.convertExperimentToPreviousYamlString(experiment);

            String experimentDefinitionYAML = BenchFlowDSL.experimentToYamlString(experiment);

            // add new experiment model
            String experimentID = experimentModelDAO.addExperiment(testID);

            // save experiment definition to minio
            minioService.saveExperimentDefinition(experimentID,
                    IOUtils.toInputStream(experimentDefinitionYAML, StandardCharsets.UTF_8));


            // create an experiment bundle

            logger.info("creating experiment bundle for " + testID);

            String tempPath = testID.split("\\.")[1] + ".zip";

            File experimentBundleFile = new File(tempPath);

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(experimentBundleFile))) {

                // create experiment definition
                ZipEntry expDefEntry = new ZipEntry(BenchFlowConstants.TEST_EXPERIMENT_DEFINITION_FILE_NAME);
                zipOutputStream.putNextEntry(expDefEntry);
                zipOutputStream.write(oldExperimentDefinitionYaml.getBytes(StandardCharsets.UTF_8));
                zipOutputStream.closeEntry();

                // create deployment descriptor
                deploymentDescriptorInputStream = minioService.getTestDeploymentDescriptor(testID);

                ZipEntry deployDescEntry = new ZipEntry(BenchFlowConstants.DEPLOYMENT_DESCRIPTOR_FILE_NAME);
                zipOutputStream.putNextEntry(deployDescEntry);
                zipOutputStream.write(IOUtils.toByteArray(deploymentDescriptorInputStream));
                zipOutputStream.closeEntry();

                // create folder with bpmn models
                for (Map.Entry<String, InputStream> entry : bpmnModelInputStreams.entrySet()) {

                    String fileName = entry.getKey();
                    InputStream modelInputStream = minioService.getTestBPMNModel(testID, fileName);

                    ZipEntry zipEntry = new ZipEntry(BenchFlowConstants.BPMN_MODELS_FOLDER_NAME + File.separator + fileName);
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(IOUtils.toByteArray(modelInputStream));
                    zipOutputStream.closeEntry();
                }

            }

            // send experiment bundle to the BF-Experiment-Manager
            experimentManagerService.runBenchFlowExperimentDemo(experimentID, experimentBundleFile);

//            experimentBundleFile.deleteOnExit();

        } catch (IOException | BenchFlowTestIDDoesNotExistException e) {
            // TODO - handle these exceptions properly (although should not happen)
            logger.info("ERROR");
            logger.error(e.toString());
        }

    }
}
