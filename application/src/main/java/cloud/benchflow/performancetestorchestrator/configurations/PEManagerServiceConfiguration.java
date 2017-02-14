package cloud.benchflow.performancetestorchestrator.configurations;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class PEManagerServiceConfiguration {

    @NotEmpty
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
