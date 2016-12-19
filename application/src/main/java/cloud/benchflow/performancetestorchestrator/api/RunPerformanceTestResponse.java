package cloud.benchflow.performancetestorchestrator.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class RunPerformanceTestResponse {

    @JsonProperty("experimentId")
    private String performanceTestId;

    public RunPerformanceTestResponse(String performanceTestId) {
        this.performanceTestId = performanceTestId;
    }

    public String getPerformanceTestId() {
        return performanceTestId;
    }

    public void setPerformanceTestId(String performanceTestId) {
        this.performanceTestId = performanceTestId;
    }
}
