import java.util.Random;

/**
 * Decides which creature (if any) to spawn at a given field location based on
 * the per-species creation probabilities from SimulationConfig. Decouples
 * spawn rules from the population list management in PopulationManager.
 *
 * @version 2022/03/02
 */
public class CreatureFactory
{
    private final SimulationConfig config;

    /**
     * @param config Supplies per-species creation probabilities.
     */
    public CreatureFactory(SimulationConfig config)
    {
        this.config = config;
    }

    /**
     * Attempt to spawn a creature at the given location. Draws from the shared
     * RNG for each species in probability order, returning the first match, or
     * null if the cell should stay empty.
     *
     * @param field    The field the creature will inhabit.
     * @param location The cell location.
     * @return A newly created Creature, or null.
     */
    public Creature trySpawn(Field field, Location location)
    {
        Random rand = Randomizer.getRandom();
        if (rand.nextDouble() <= config.salmonCreationProbability) {
            return new Salmon(true, field, location);
        } else if (rand.nextDouble() <= config.codCreationProbability) {
            return new Cod(true, field, location);
        } else if (rand.nextDouble() <= config.seaweedCreationProbability) {
            return new Seaweed(true, field, location);
        } else if (rand.nextDouble() <= config.sharkCreationProbability) {
            return new Shark(true, field, location);
        } else if (rand.nextDouble() <= config.whaleCreationProbability) {
            return new Whale(true, field, location);
        }
        return null;
    }
}
