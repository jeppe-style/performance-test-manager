package cloud.benchflow.performancetestmanager.services.internal;

import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;

import javax.annotation.Nonnull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
public final class PerformanceTestExecutor {

    public static ExecutorService createPerformanceTestExecutor(Environment environment) {

        int CPUs = Runtime.getRuntime().availableProcessors();

        return environment.lifecycle().executorService("run-performance-test-%d")
                .minThreads(5 * CPUs) //TODO: make base min value configurable
                .maxThreads(15 * CPUs) //TODO: make base max value configurable
                .keepAliveTime(Duration.seconds(60))
                .workQueue(new SynchronousQueue<>())
                .threadFactory(new DaemonThreadFactory())
                .rejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .build();

    }

    /**
     * See http://dev.bizo.com/2014/06/cached-thread-pool-considered-harmlful.html
     */
    public static class DaemonThreadFactory implements ThreadFactory {

        private AtomicInteger count = new AtomicInteger();

        @Override
        public Thread newThread(@Nonnull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("performance-test-orchestrator-" + count.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }

}
