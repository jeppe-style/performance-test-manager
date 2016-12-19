package cloud.benchflow.performancetestorchestrator.models;

import cloud.benchflow.performancetestorchestrator.api.RunPerformanceTestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class PerformanceTestModel implements Runnable {

    private Logger logger = LoggerFactory.getLogger(PerformanceTestModel.class.getName());

    private boolean completed;

    private String id;
    private int numExperiments = 3;


    public PerformanceTestModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public RunPerformanceTestResponse getResponse() {

        return new RunPerformanceTestResponse(id);

    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void run() {

        // TODO

        while (numExperiments > 0) {

            logger.info("RUNNING NEW PERFORMANCE TEST (left=" + numExperiments + ")");

            numExperiments--;
            runNextExperiment();

        }

        completed = true;

    }

    private void runNextExperiment() {

        // generate experiment
        PerformanceExperimentModel performanceExperiment = generateNextExperiment();

        // TODO - put this in a queue
        // run experiment
        Thread experimentThread = new Thread(performanceExperiment);

        experimentThread.start();

        try {
            experimentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private PerformanceExperimentModel generateNextExperiment() {

        logger.info("generating new performance experiment");

        return new PerformanceExperimentModel();

    }
}
