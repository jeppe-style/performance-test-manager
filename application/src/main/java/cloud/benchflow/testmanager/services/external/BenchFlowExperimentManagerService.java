package cloud.benchflow.testmanager.services.external;

import cloud.benchflow.testmanager.models.BenchFlowExperimentModel;
import org.glassfish.jersey.media.multipart.*;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;


/**
 * uses Jersey Client:
 * http://www.dropwizard.io/1.0.6/docs/manual/client.html
 * https://jersey.java.net/documentation/2.22.1/client.html
 *
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class BenchFlowExperimentManagerService {

    // TODO - move this to common library?
    private static String RUN_PATH = "/run";
    private static String STATUS_PE_PATH = "status";

    private Logger logger = LoggerFactory.getLogger(BenchFlowExperimentManagerService.class.getSimpleName());

    private WebTarget experimentManagerTarget;

    public BenchFlowExperimentManagerService(Client httpClient, String experimentManagerAddress) {

        this.experimentManagerTarget = httpClient.target("http://" + experimentManagerAddress);
    }

    public void runBenchFlowExperiment(String experimentID) {

        logger.info("runBenchFlowExperiment: " + experimentID);

        RunBenchFlowExperimentRequest requestObject = new RunBenchFlowExperimentRequest(experimentID);

        Response runPEResponse = experimentManagerTarget.path(experimentID).path(RUN_PATH)
                .request()
                .post(Entity.entity(requestObject, MediaType.APPLICATION_JSON));

        if (runPEResponse.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {

            // TODO - handle possible errors and throw exceptions accordingly

            logger.error("runBenchFlowExperiment: error connecting - " + runPEResponse.getStatus());
        }

    }

    public BenchFlowExperimentModel.BenchFlowExperimentState abortBenchFlowExperiment(String testID, long experimentID) {

        logger.info("abortBenchFlowExperiment: " + testID + "/" + experimentID);

        BenchFlowExperimentStateEntity stateEntity = new BenchFlowExperimentStateEntity(
                BenchFlowExperimentModel.BenchFlowExperimentState.ABORTED);

        Response abortPEResponse = experimentManagerTarget.path(testID)
                .path(String.valueOf(experimentID))
                .path(STATUS_PE_PATH)
                .request().post(Entity.entity(stateEntity, MediaType.APPLICATION_JSON));

        if (abortPEResponse.getStatus() != Response.Status.OK.getStatusCode()) {

            // TODO - handle possible errors and throw exceptions accordingly

            logger.error("abortBenchFlowExperiment: error connecting - " + abortPEResponse.getStatus());
        }

        BenchFlowExperimentStateEntity responseStateEntity = abortPEResponse.readEntity(BenchFlowExperimentStateEntity.class);

        return responseStateEntity.getState();
    }

    public void runBenchFlowExperimentDemo(String experimentID, File experimentBundleFile) {

        logger.info("runBenchFlowExperimentDemo: " + experimentID);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("experiment", experimentBundleFile, MediaType.APPLICATION_OCTET_STREAM_TYPE);

        MultiPart multiPart = new FormDataMultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(fileDataBodyPart);

        String fileName = experimentBundleFile.getName().split("\\.")[0];

        Response response = experimentManagerTarget
                .path("/run/" + fileName)
                .register(MultiPartFeature.class)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {

            logger.error("runBenchFlowExperiment: error connecting - " + response.getStatus());
        }

        logger.info("submitted experiment with response: " + response.readEntity(String.class));


    }

    // TODO - move this to common library?
    private class RunBenchFlowExperimentRequest {

        private String experimentID;

        public RunBenchFlowExperimentRequest(String experimentID) {
            this.experimentID = experimentID;
        }

        public String getExperimentID() {
            return experimentID;
        }

        public void setExperimentID(String experimentID) {
            this.experimentID = experimentID;
        }

    }

    // TODO - move this to common library?
    private class BenchFlowExperimentStateEntity {

        private BenchFlowExperimentModel.BenchFlowExperimentState state;

        public BenchFlowExperimentStateEntity(BenchFlowExperimentModel.BenchFlowExperimentState state) {
            this.state = state;
        }

        public BenchFlowExperimentModel.BenchFlowExperimentState getState() {
            return state;
        }

        public void setState(BenchFlowExperimentModel.BenchFlowExperimentState state) {
            this.state = state;
        }
    }


}
