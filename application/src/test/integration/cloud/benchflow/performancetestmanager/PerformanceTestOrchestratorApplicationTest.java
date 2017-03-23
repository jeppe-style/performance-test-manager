package cloud.benchflow.performancetestmanager;

import cloud.benchflow.performancetestmanager.api.request.ChangePerformanceTestStateRequest;
import cloud.benchflow.performancetestmanager.api.response.RunPerformanceTestResponse;
import cloud.benchflow.performancetestmanager.archive.TestArchives;
import cloud.benchflow.performancetestmanager.configurations.PerformanceTestManagerConfiguration;
import cloud.benchflow.performancetestmanager.constants.BenchFlowConstants;
import cloud.benchflow.performancetestmanager.helpers.TestConstants;
import cloud.benchflow.performancetestmanager.models.BenchFlowTestModel;
import cloud.benchflow.performancetestmanager.models.User;
import cloud.benchflow.performancetestmanager.resources.PerformanceTestStateResource;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
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

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.02.17.
 */
public class PerformanceTestOrchestratorApplicationTest extends DockerComposeTest {


    @Rule
    public final DropwizardAppRule<PerformanceTestManagerConfiguration> RULE = new DropwizardAppRule<>(
            PerformanceTestManagerApplication.class,
            "../configuration.yml",
            ConfigOverride.config("mongoDB.hostname", MONGO_CONTAINER.getIp()),
            ConfigOverride.config("mongoDB.port", String.valueOf(MONGO_CONTAINER.getExternalPort())),
            ConfigOverride.config("minio.address", "http://" + MINIO_CONTAINER.getIp() + ":" + MINIO_CONTAINER.getExternalPort()),
            ConfigOverride.config("minio.accessKey", MINIO_ACCESS_KEY),
            ConfigOverride.config("minio.secretKey", MINIO_SECRET_KEY),
            ConfigOverride.config("performanceExperimentManager.address", "localhost")
    );

    @Test
    public void runPerformanceTest() throws Exception {

        // needed for multipart client
        // https://github.com/dropwizard/dropwizard/issues/1013
        JerseyClientConfiguration configuration = new JerseyClientConfiguration();
        configuration.setChunkedEncodingEnabled(false);

        Client client = new JerseyClientBuilder(RULE.getEnvironment()).using(configuration).build("test client");

        String performanceTestName = "testNameExample";
        User user = BenchFlowConstants.BENCH_FLOW_USER;

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("performanceTestBundle",
                                                                 TestArchives.getValidTestArchiveFile(),
                                                                 MediaType.APPLICATION_OCTET_STREAM_TYPE);

        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(fileDataBodyPart);

        Response response = client
                .target(String.format("http://localhost:%d/performance-test", RULE.getLocalPort()))
                .register(MultiPartFeature.class)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        RunPerformanceTestResponse testResponse = response.readEntity(RunPerformanceTestResponse.class);

        Assert.assertNotNull(testResponse);
        Assert.assertTrue(testResponse.getPerformanceTestID().contains(performanceTestName));

        // TODO - check that experiment is scheduled

    }

    @Test
    public void changePerformanceTestStateInvalid() throws Exception {

        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");

        BenchFlowTestModel.PerformanceTestState state = BenchFlowTestModel.PerformanceTestState.COMPLETED;
        String performanceTestID = TestConstants.VALID_TEST_ID;

        ChangePerformanceTestStateRequest stateRequest = new ChangePerformanceTestStateRequest(state);

        String target = "http://localhost:" + RULE.getLocalPort() + PerformanceTestStateResource.ROOT_PATH + performanceTestID + "/state";

        Response response = client
                .target(target)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(stateRequest, MediaType.APPLICATION_JSON));

        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        // TODO - check how to include message
//        Assert.assertEquals(InvalidPerformanceTestIDWebException.message, response.getStatusInfo().getReasonPhrase());


    }

}