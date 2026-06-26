import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds summary information from the current field state.
 */
public final class SimulationAnalyzer
{
    private SimulationAnalyzer()
    {
    }

    public static PopulationSummary analyze(Field field)
    {
        Map<Class<?>, Integer> countsByType = new LinkedHashMap<>();
        field.forEachLocation((location, creature) -> {
            if(creature != null) {
                Class<?> creatureClass = creature.getClass();
                Integer count = countsByType.get(creatureClass);
                countsByType.put(creatureClass, count == null ? 1 : count + 1);
            }
        });
        return new PopulationSummary(countsByType);
    }
}
