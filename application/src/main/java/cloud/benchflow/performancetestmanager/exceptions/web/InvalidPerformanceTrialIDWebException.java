package cloud.benchflow.performancetestmanager.exceptions.web;

import javax.ws.rs.WebApplicationException;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class InvalidPerformanceTrialIDWebException extends WebApplicationException {

    private static String message = "Invalid Performance Trial ID";

    public InvalidPerformanceTrialIDWebException() {
        super(message);
    }
}
