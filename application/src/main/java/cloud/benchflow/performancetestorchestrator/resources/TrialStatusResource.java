package cloud.benchflow.performancetestorchestrator.resources;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class TrialStatusResource {

    private Logger logger = LoggerFactory.getLogger(TrialStatusResource.class.getSimpleName());

    private ExecutorService taskExecutorService;

    public TrialStatusResource(ExecutorService taskExecutorService) {
        this.taskExecutorService = taskExecutorService;
    }

    @POST
    @Path("/{performanceTestID}/{performanceExperimentID}/{trialID}/status")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void submitTrialStatus(@PathParam("performanceTestID") final String performanceTestID,
                                  @PathParam("performanceExperimentID") final String performanceExperimentID,
                                  @PathParam("trialID") final String trialID,
                                  @FormDataParam("performanceTest") final InputStream performanceTestArchive) {


        // TODO - get the PerformanceExperimentModel from the DAO

        // TODO - add the trial status

        // TODO - return if OK


    }

}
