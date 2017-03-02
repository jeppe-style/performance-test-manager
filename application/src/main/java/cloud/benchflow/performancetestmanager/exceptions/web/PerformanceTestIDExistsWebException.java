package cloud.benchflow.performancetestmanager.exceptions.web;

import javax.ws.rs.WebApplicationException;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class PerformanceTestIDExistsWebException extends WebApplicationException {

    private static String message = "Performance Test ID already exists";

    public PerformanceTestIDExistsWebException() {
        super(message);
    }
}
