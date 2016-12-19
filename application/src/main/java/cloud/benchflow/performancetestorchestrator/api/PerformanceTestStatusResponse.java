package cloud.benchflow.performancetestorchestrator.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
public class PerformanceTestStatusResponse {

    @JsonProperty("status")
    private String status;

    public PerformanceTestStatusResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
