package cloud.benchflow.testmanager;

import cloud.benchflow.testmanager.api.response.RunBenchFlowTestResponse;
import cloud.benchflow.testmanager.configurations.BenchFlowTestManagerConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.util.Duration;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 2017-04-07
 */
public class BenchFlowTestManagerDemoTest {

//    private static final String DEMO_TEST_ARCHIVE_FILENAME = "src/test/resources/data/ParallelMultiple11.zip";
//
//    @Rule
//    public final DropwizardAppRule<BenchFlowTestManagerConfiguration> RULE = new DropwizardAppRule<>(
//            BenchFlowTestManagerApplication.class,
//            "../configuration.yml"
//    );
//
//    @Test
//    public void runDemoTest() {
//
//        File demoFile = new File(DEMO_TEST_ARCHIVE_FILENAME);
//
//        // needed for multipart client
//        // https://github.com/dropwizard/dropwizard/issues/1013
//        JerseyClientConfiguration configuration = new JerseyClientConfiguration();
//        configuration.setChunkedEncodingEnabled(false);
//        // needed because parsing testYaml takes more than default time
//        configuration.setTimeout(Duration.milliseconds(1000));
//
//        Client client = new JerseyClientBuilder(RULE.getEnvironment()).using(configuration).build("test client");
//
////        String benchFlowTestName = "testNameExample";
////        User user = BenchFlowConstants.BENCHFLOW_USER;
//
//        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("benchFlowTestBundle",
//                demoFile,
//                MediaType.APPLICATION_OCTET_STREAM_TYPE);
//
//        MultiPart multiPart = new MultiPart();
//        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
//        multiPart.bodyPart(fileDataBodyPart);
//
//        Response response = client
//                .target(String.format("http://localhost:%d/benchflow-test/demo", RULE.getLocalPort()))
//                .register(MultiPartFeature.class)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(multiPart, multiPart.getMediaType()));
//
//        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getState());
//
//        RunBenchFlowTestResponse testResponse = response.readEntity(RunBenchFlowTestResponse.class);
//
//        try {
//            Thread.sleep(30*1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
////        Assert.assertNotNull(testResponse);
////        Assert.assertTrue(testResponse.getTestID().contains(benchFlowTestName));
//
//    }

}
