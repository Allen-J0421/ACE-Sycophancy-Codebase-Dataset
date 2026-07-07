import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                if (rand.nextDouble() <= config.salmonCreationProbability) {
                    Location location = new Location(row, col);
                    creatures.add(new Salmon(true, field, location));
                } else if (rand.nextDouble() <= config.codCreationProbability) {
                    Location location = new Location(row, col);
                    creatures.add(new Cod(true, field, location));
                } else if (rand.nextDouble() <= config.seaweedCreationProbability) {
                    Location location = new Location(row, col);
                    creatures.add(new Seaweed(true, field, location));
                } else if (rand.nextDouble() <= config.sharkCreationProbability) {
                    Location location = new Location(row, col);
                    creatures.add(new Shark(true, field, location));
                } else if (rand.nextDouble() <= config.whaleCreationProbability) {
                    Location location = new Location(row, col);
                    creatures.add(new Whale(true, field, location));
                }
            }
        }
    }
}
