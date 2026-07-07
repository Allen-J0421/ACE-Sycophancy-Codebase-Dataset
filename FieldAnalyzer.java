import java.util.HashMap;

/**
 * Traverses a Field and tallies the count of each species present.
 * Separates the field-scanning concern from the statistics-tracking concern.
 *
 * @version 2022.03.01
 */
public class FieldAnalyzer
{
    /**
     * Scan every cell of the field and return a map of species name to the
     * number of live occupants of that type.
     *
     * @param field (Field) The field to scan.
     * @return (HashMap<String, Integer>) Species name to occupant count.
     */
    public HashMap<String, Integer> countSpecies(Field field)
    {
        HashMap<String, Integer> counts = new HashMap<>();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Object obj = field.getObjectAt(row, col);
                if (obj != null) {
                    String name = ((Species) obj).getName();
                    counts.put(name, counts.getOrDefault(name, 0) + 1);
                }
            }
        }
        return counts;
    }
}
