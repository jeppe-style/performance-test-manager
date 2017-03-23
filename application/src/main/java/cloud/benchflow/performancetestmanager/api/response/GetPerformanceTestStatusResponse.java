package cloud.benchflow.performancetestmanager.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
public class GetPerformanceTestStatusResponse {

    // TODO - decide what this should contain

    @NotEmpty
    @JsonProperty
    private String performanceTestID;

    public GetPerformanceTestStatusResponse() {
    }

    public GetPerformanceTestStatusResponse(String performanceTestID) {
        this.performanceTestID = performanceTestID;
    }

    public String getPerformanceTestID() {
        return performanceTestID;
    }

    public void setPerformanceTestID(String performanceTestID) {
        this.performanceTestID = performanceTestID;
    }
}