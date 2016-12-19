package cloud.benchflow.performancetestorchestrator.services.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
public class DataStore {

    private static Logger logger = LoggerFactory.getLogger(DataStore.class.getName());

    private static Map<String, Boolean> performanceTestDataStore = new ConcurrentHashMap<>();

    public static boolean addPerformanceTest(String performanceTestID) {

        logger.info("adding new performance test");

        return performanceTestDataStore.put(performanceTestID, false);

    }

    public static boolean setPerformanceTestCompleted(String performanceTestID) {

        logger.info("setting performance test completed");

        return performanceTestDataStore.replace(performanceTestID, true);

    }

    public static boolean getPerformanceTestStatus(String performanceTestID) {

        logger.info("getting performance test status");

        return performanceTestDataStore.get(performanceTestID);

    }

}
