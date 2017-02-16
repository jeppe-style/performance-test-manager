package cloud.benchflow.performancetestorchestrator.exceptions.web;

import javax.ws.rs.WebApplicationException;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class PerformanceTestIDExistsException extends WebApplicationException {

    private static String message = "Performance Test ID already exists";

    public PerformanceTestIDExistsException() {
        super(message);
    }
}
