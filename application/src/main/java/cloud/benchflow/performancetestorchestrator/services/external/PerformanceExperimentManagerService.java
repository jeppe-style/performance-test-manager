package cloud.benchflow.performancetestorchestrator.services.external;

import cloud.benchflow.performancetestorchestrator.models.PerformanceExperimentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 *
 * uses Jersey Client:
 * http://www.dropwizard.io/1.0.6/docs/manual/client.html
 * https://jersey.java.net/documentation/2.22.1/client.html
 *
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class PerformanceExperimentManagerService {

    // TODO - move this to common library?
    private static String RUN_PE_PATH = "run-performance-experiment";
    private static String STATUS_PE_PATH = "status";

    private Logger logger = LoggerFactory.getLogger(PerformanceExperimentManagerService.class.getSimpleName());

    private Client httpClient;
    private WebTarget peManagerTarget;

    public PerformanceExperimentManagerService(Client httpClient, String peManagerIP) {

        this.httpClient = httpClient;

        this.peManagerTarget = httpClient.target("http://" + peManagerIP);
    }

    public void runPerformanceExperiment(String performanceExperimentID) {

        logger.info("runPerformanceExperiment: " + performanceExperimentID);

        RunPerformanceExperimentRequest requestObject = new RunPerformanceExperimentRequest(performanceExperimentID);

        Response runPEResponse = peManagerTarget.path(RUN_PE_PATH)
                .request()
                .post(Entity.entity(requestObject, MediaType.APPLICATION_JSON));

        if (runPEResponse.getStatus() != Response.Status.OK.getStatusCode()) {

            // TODO - handle possible errors and throw exceptions accordingly

            logger.error("runPerformanceExperiment: error connecting - " + runPEResponse.getStatus());
        }

    }

    public PerformanceExperimentModel.PerformanceExperimentState abortPerformanceExperiment(String performanceTestID, long performanceExperimentID) {

        logger.info("abortPerformanceExperiment: " + performanceTestID + "/" + performanceExperimentID);

        PerformanceExperimentStateEntity stateEntity = new PerformanceExperimentStateEntity(
                PerformanceExperimentModel.PerformanceExperimentState.ABORTED);

        Response abortPEResponse = peManagerTarget.path(performanceTestID)
                .path(String.valueOf(performanceExperimentID))
                .path(STATUS_PE_PATH)
                .request().post(Entity.entity(stateEntity, MediaType.APPLICATION_JSON));

        if (abortPEResponse.getStatus() != Response.Status.OK.getStatusCode()) {

            // TODO - handle possible errors and throw exceptions accordingly

            logger.error("abortPerformanceExperiment: error connecting - " + abortPEResponse.getStatus());
        }

        PerformanceExperimentStateEntity responseStateEntity = abortPEResponse.readEntity(PerformanceExperimentStateEntity.class);

        return responseStateEntity.getState();
    }

    // TODO - move this to common library?
    private class RunPerformanceExperimentRequest {

        private String performanceExperimentID;

        public RunPerformanceExperimentRequest(String performanceExperimentID) {
            this.performanceExperimentID = performanceExperimentID;
        }

        public String getPerformanceExperimentID() {
            return performanceExperimentID;
        }

        public void setPerformanceExperimentID(String performanceExperimentID) {
            this.performanceExperimentID = performanceExperimentID;
        }

    }

    // TODO - move this to common library?
    private class PerformanceExperimentStateEntity {

        private PerformanceExperimentModel.PerformanceExperimentState state;

        public PerformanceExperimentStateEntity(PerformanceExperimentModel.PerformanceExperimentState state) {
            this.state = state;
        }

        public PerformanceExperimentModel.PerformanceExperimentState getState() {
            return state;
        }

        public void setState(PerformanceExperimentModel.PerformanceExperimentState state) {
            this.state = state;
        }
    }



}
