package cloud.benchflow.performancetestorchestrator.definitions;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 13.02.17.
 */
public class PerformanceTestDefinition {

    private final String definition;

    public PerformanceTestDefinition(String definition) {
        this.definition = definition;
    }

    public String getID() {

        // TODO - parse using DSL

        Map<String, Object> parsedExpConfig = (Map<String, Object>) new Yaml().load(definition);

        return (String) parsedExpConfig.get("testName");

    }
}