import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Provides statistical data about the current state of a field.
 *
 * @version 2016.02.29
 */
public final class FieldStats
{
    private FieldStats()
    {
    }

    /**
     * Get details of what is in the field.
     * @param field The field to inspect.
     * @return A string describing what is in the field.
     */
    public static String getPopulationDetails(Field field)
    {
        StringJoiner details = new StringJoiner(" ");
        for(Map.Entry<Class<?>, Integer> entry : countLivingBeings(field).entrySet()) {
            details.add(entry.getKey().getName() + ": " + entry.getValue());
        }
        return details.toString();
    }

    /**
     * Determine whether the simulation is still viable.
     * @param field The field to inspect.
     * @return true If there is more than one species alive.
     */
    public static boolean isViable(Field field)
    {
        int nonZero = 0;
        for(int count : countLivingBeings(field).values()) {
            if(count > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    private static Map<Class<?>, Integer> countLivingBeings(Field field)
    {
        Map<Class<?>, Integer> counts = new LinkedHashMap<>();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                LivingBeing being = field.getLivingBeingAt(row, col);
                if(being != null) {
                    counts.merge(being.getClass(), 1, Integer::sum);
                }
            }
        }
        return counts;
    }
}
