import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable population report for the current field state.
 */
public class PopulationSummary
{
    private final Map<Class<?>, Integer> countsByType;

    public PopulationSummary(Map<Class<?>, Integer> countsByType)
    {
        this.countsByType = new LinkedHashMap<>(countsByType);
    }

    public String getPopulationDetails()
    {
        StringBuilder buffer = new StringBuilder();
        for(Map.Entry<Class<?>, Integer> entry : countsByType.entrySet()) {
            buffer.append(entry.getKey().getName());
            buffer.append(": ");
            buffer.append(entry.getValue());
            buffer.append(' ');
        }
        return buffer.toString();
    }

    public boolean isViable()
    {
        int nonZero = 0;
        for(Integer count : countsByType.values()) {
            if(count.intValue() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }
}
