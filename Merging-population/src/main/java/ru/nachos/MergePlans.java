import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import ru.nachos.PopulationContainer;

public class MergePlans {

    private static final String FIRST_PLAN = "src/main/resources/kron_0/output_plans.xml";
    private static final String SECOND_PLAN = "src/main/resources/kron_0/output_plans_relax.xml";
    private static final String OUTPUT = "src/main/resources/kron_0/merged_plans_kron_0.xml";


    public static void main(String[] args) {
        run(SECOND_PLAN, FIRST_PLAN, OUTPUT);
    }

    private static void run(String firstPlan, String secondPlan, String output) {

        Config config1 = ConfigUtils.createConfig();
        Config config2 = ConfigUtils.createConfig();
        Scenario scenario_origin = ScenarioUtils.loadScenario(config1);
        Scenario scenario_merge = ScenarioUtils.loadScenario(config2);
        PopulationContainer container = new PopulationContainer(scenario_origin, scenario_merge, firstPlan, secondPlan);
        container.readPopulations();
        container.mergePopulations();
        container.writeNewPopulation(output);
    }
}
