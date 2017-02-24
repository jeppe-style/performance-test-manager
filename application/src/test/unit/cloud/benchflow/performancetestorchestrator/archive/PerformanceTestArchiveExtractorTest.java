package cloud.benchflow.performancetestorchestrator.archive;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 16.02.17.
 */
public class PerformanceTestArchiveExtractorTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void extractPerformanceTestDefinition() throws Exception {

        String ptDefinition = PerformanceTestArchiveExtractor.extractPerformanceTestDefinitionString(
                TestArchives.getInValidTestArchiveZip());

        Assert.assertNotNull(ptDefinition);

        Assert.assertTrue(ptDefinition.contains("testName:"));

    }

    @Test
    public void extractDeploymentDescriptor() throws Exception {

        InputStream deploymentDescriptorInputStream = PerformanceTestArchiveExtractor.extractDeploymentDescriptorInputStream(
                TestArchives.getValidTestArchiveZip());

        Assert.assertNotNull(deploymentDescriptorInputStream);

        String deploymentDescriptorString = org.apache.commons.io.IOUtils.toString(deploymentDescriptorInputStream,
                                                                                   StandardCharsets.UTF_8.name());

        Assert.assertTrue(deploymentDescriptorString.contains("version:"));

    }

    @Test
    public void extractBPMNModels() throws Exception {

        int numberOfModels = TestArchives.BPMN_MODELS_COUNT;

        Map<String, InputStream> bpmnModels = PerformanceTestArchiveExtractor.extractBPMNModelInputStreams(
                TestArchives.getValidTestArchiveZip());

        Assert.assertEquals(numberOfModels, bpmnModels.size());

    }

}