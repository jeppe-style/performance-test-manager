package cloud.benchflow.performancetestorchestrator.exceptions.web;

import javax.ws.rs.WebApplicationException;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class InvalidPerformanceTestIDException extends WebApplicationException {

    private static String message = "Invalid Performance Test ID";

    public InvalidPerformanceTestIDException() {
        super(message);
    }
}
