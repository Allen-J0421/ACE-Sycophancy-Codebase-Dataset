import java.util.ArrayList;
import java.util.List;

/**
 * Manages the creature population for a simulation run: initial seeding,
 * the live creature list, and newborn registration. SimulationEngine retains
 * control over the act loop and calls into this service for list access and
 * end-of-step bookkeeping.
 *
 * @version 2022/03/02
 */
public class PopulationManager
{
    private List<Creature> creatures;

    public PopulationManager()
    {
        creatures = new ArrayList<>();
    }

    /**
     * Returns the live creature list. Callers may iterate and mutate it
     * (e.g. remove dead creatures via an Iterator) during the act loop.
     */
    public List<Creature> getCreatures() { return creatures; }

    /**
     * Clear the creature list without touching the field grid.
     */
    public void clear()
    {
        creatures.clear();
    }

    /**
     * Append newborn creatures produced during the current step's act loop.
     * @param newCreatures Creatures born this step.
     */
    public void mergeNewborns(List<Creature> newCreatures)
    {
        creatures.addAll(newCreatures);
    }

    /**
     * Clear the field grid and randomly seed it with creatures according to
     * the supplied configuration probabilities.
     * @param field  The field to populate.
     * @param config Spawn probabilities and grid dimensions.
     */
    public void populate(Field field, SimulationConfig config)
    {
        CreatureFactory factory = new CreatureFactory(config);
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Creature creature = factory.trySpawn(field, new Location(row, col));
                if (creature != null) {
                    creatures.add(creature);
                }
            }
        }
    }
}
