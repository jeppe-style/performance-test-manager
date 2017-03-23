package cloud.benchflow.performancetestmanager.exceptions.web;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class InvalidPerformanceTestIDWebException extends WebApplicationException {

    // TODO - check visability
    // TODO - check how to include message
    public static final String message = "Invalid Performance Test ID";

    public InvalidPerformanceTestIDWebException() {

        super(Response.status(Response.Status.NOT_FOUND).build());

    }
}
