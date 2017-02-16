package cloud.benchflow.performancetestorchestrator.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class RunPerformanceTestResponse {

    @JsonProperty("performanceTestID")
    private String performanceTestID;

    public RunPerformanceTestResponse(String performanceTestID) {
        this.performanceTestID = performanceTestID;
    }

    public String getPerformanceTestID() {
        return performanceTestID;
    }

    public void setPerformanceTestID(String performanceTestID) {
        this.performanceTestID = performanceTestID;
    }
}
