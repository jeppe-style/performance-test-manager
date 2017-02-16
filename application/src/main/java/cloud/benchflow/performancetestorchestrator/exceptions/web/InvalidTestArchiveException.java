package cloud.benchflow.performancetestorchestrator.exceptions.web;

import javax.ws.rs.WebApplicationException;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class InvalidTestArchiveException extends WebApplicationException {

    private static String message = "Invalid Performance Test archive";

    public InvalidTestArchiveException() {
        super(message);
    }
}
