package cloud.benchflow.performancetestorchestrator.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
public class GetPerformanceTestStatusResponse {

    // TODO - decide what this should contain

    @JsonProperty("performanceTestID")
    private String performanceTestID;

    public GetPerformanceTestStatusResponse(String performanceTestID) {
        this.performanceTestID = performanceTestID;
    }

    public String getPerformanceTestID() {
        return performanceTestID;
    }
}
