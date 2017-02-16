package cloud.benchflow.performancetestorchestrator.exceptions.web;

import javax.ws.rs.WebApplicationException;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 15.02.17.
 */
public class InvalidArgumentsException extends WebApplicationException {

    private static String message = "Invalid arguments";

    public InvalidArgumentsException() {
        super(message);
    }

}
