import java.util.Collections;
import java.util.EnumMap;
import java.util.Set;

/**
 * Immutable representation of the field state for rendering and statistics.
 *
 * @version 2022.03.02
 */
public class FieldSnapshot
{
    private final int depth;
    private final int width;
    private final Species[][] occupants;
    private final EnumMap<Species, Integer> populationCounts;

    public FieldSnapshot(int depth, int width, Species[][] occupants,
                         EnumMap<Species, Integer> populationCounts)
    {
        this.depth = depth;
        this.width = width;
        this.occupants = occupants;
        this.populationCounts = new EnumMap<>(populationCounts);
    }

    public int getDepth()
    {
        return depth;
    }

    public int getWidth()
    {
        return width;
    }

    public Species getSpeciesAt(int row, int col)
    {
        return occupants[row][col];
    }

    public int getPopulationCount(Species species)
    {
        Integer count = populationCounts.get(species);
        return count == null ? 0 : count.intValue();
    }

    public Set<Species> getPresentSpecies()
    {
        return Collections.unmodifiableSet(populationCounts.keySet());
    }

    public int getActiveSpeciesCount()
    {
        return populationCounts.size();
    }
}
