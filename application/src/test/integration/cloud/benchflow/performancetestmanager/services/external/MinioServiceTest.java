package cloud.benchflow.performancetestmanager.services.external;

import cloud.benchflow.performancetestmanager.archive.TestArchives;
import cloud.benchflow.performancetestmanager.constants.BenchFlowConstants;
import cloud.benchflow.performancetestmanager.DockerComposeTest;
import io.minio.MinioClient;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static cloud.benchflow.performancetestmanager.helpers.TestConstants.VALID_EXPERIMENT_ID;
import static cloud.benchflow.performancetestmanager.helpers.TestConstants.VALID_TEST_ID;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 16.02.17.
 */
public class MinioServiceTest extends DockerComposeTest{

    private MinioService minioService;

    private InputStream ptDefinitionInputStream;

    private InputStream deploymentDescriptorInputStream;
    private Map<String, InputStream> bpmnModelsMap;

    @Before
    public void setUp() throws Exception {

        // TODO - see how to mock final class MinioClient
        // https://github.com/mockito/mockito/wiki/What%27s-new-in-Mockito-2#mock-the-unmockable-opt-in-mocking-of-final-classesmethods

        String minioEndpoint = "http://" + MINIO_CONTAINER.getIp() + ":" + MINIO_CONTAINER.getExternalPort();

        MinioClient minioClient = new MinioClient(minioEndpoint, "minio", "minio123");

        if (!minioClient.bucketExists(BenchFlowConstants.TESTS_BUCKET))
            minioClient.makeBucket(BenchFlowConstants.TESTS_BUCKET);

        minioService = new MinioService(minioClient);

        ptDefinitionInputStream = TestArchives.getValidPTDefinitionInputStream();

        deploymentDescriptorInputStream = TestArchives.getValidDeploymentDescriptorInputStream();

        bpmnModelsMap = TestArchives.getValidBPMNModels();

    }

    @Test
    public void saveGetRemovePerformanceTestDefinition() throws Exception {

        minioService.savePerformanceTestDefinition(VALID_TEST_ID, ptDefinitionInputStream);

        InputStream receivedInputStream = minioService.getPerformanceTestDefinition(VALID_TEST_ID);

        Assert.assertNotNull(receivedInputStream);

        String receivedString = IOUtils.toString(new ByteArrayInputStream(IOUtils.toByteArray(receivedInputStream)), StandardCharsets.UTF_8);

        Assert.assertEquals(TestArchives.getValidPTDefinitionString(), receivedString);

        minioService.removePerformanceTestDefinition(VALID_TEST_ID);

        receivedInputStream = minioService.getPerformanceTestDefinition(VALID_TEST_ID);

        Assert.assertNull(receivedInputStream);

    }

    @Test
    public void saveGetRemoveDeploymentDescriptor() throws Exception {

        minioService.savePerformanceTestDeploymentDescriptor(VALID_TEST_ID, deploymentDescriptorInputStream);

        InputStream receivedInputStream = minioService.getPerformanceTestDeploymentDescriptor(VALID_TEST_ID);

        Assert.assertNotNull(receivedInputStream);

        String receivedString = IOUtils.toString(new ByteArrayInputStream(IOUtils.toByteArray(receivedInputStream)), StandardCharsets.UTF_8);

        Assert.assertEquals(TestArchives.getValidDeploymentDescriptorString(), receivedString);

        minioService.removePerformanceTestDeploymentDescriptor(VALID_TEST_ID);

        receivedInputStream = minioService.getPerformanceTestDeploymentDescriptor(VALID_TEST_ID);

        Assert.assertNull(receivedInputStream);

    }


    @Test
    public void saveGetRemoveBPMNDefinitions() throws Exception {

        // TODO

        bpmnModelsMap.forEach((name, model) -> {

            minioService.savePerformanceTestBPMNModel(VALID_TEST_ID, name, model);

            InputStream receivedInputStream = minioService.getPerformanceTestBPMNModel(VALID_TEST_ID, name);

            Assert.assertNotNull(receivedInputStream);

            // TODO - assert the content is the same

//            String receivedString = IOUtils.toString(new ByteArrayInputStream(IOUtils.toByteArray(receivedInputStream)), StandardCharsets.UTF_8);
//
//            Assert.assertEquals(TestArchives.getValidDeploymentDescriptorString(), receivedString);

            minioService.removePerformanceTestBPMNModel(VALID_TEST_ID, name);

            receivedInputStream = minioService.getPerformanceTestBPMNModel(VALID_TEST_ID, name);

            Assert.assertNull(receivedInputStream);

        });

    }

    @Test
    public void saveGetRemovePerformanceExperimentDefinition() throws Exception {

        minioService.savePerformanceExperimentDefinition(VALID_EXPERIMENT_ID, ptDefinitionInputStream);

        InputStream receivedInputStream = minioService.getPerformanceExperimentDefinition(VALID_EXPERIMENT_ID);

        Assert.assertNotNull(receivedInputStream);

        String receivedString = IOUtils.toString(new ByteArrayInputStream(IOUtils.toByteArray(receivedInputStream)), StandardCharsets.UTF_8);

        Assert.assertEquals(TestArchives.getValidPTDefinitionString(), receivedString);

        minioService.removePerformanceExperimentDefinition(VALID_EXPERIMENT_ID);

        receivedInputStream = minioService.getPerformanceExperimentDefinition(VALID_EXPERIMENT_ID);

        Assert.assertNull(receivedInputStream);

    }

}