import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Decides which creature (if any) to spawn at a given field location.
 * Species and their probabilities are registered in priority order at
 * construction time; adding or removing a species requires only one line
 * change in the constructor.
 *
 * @version 2022/03/02
 */
public class CreatureFactory
{
    private static class SpawnEntry
    {
        final double probability;
        final BiFunction<Field, Location, Creature> spawner;

        SpawnEntry(double probability, BiFunction<Field, Location, Creature> spawner)
        {
            this.probability = probability;
            this.spawner = spawner;
        }
    }

    private final List<SpawnEntry> registry;

    /**
     * Build the spawn registry from config probabilities. Entries are checked
     * in the order they are added; the first match wins.
     * @param config Supplies per-species creation probabilities.
     */
    public CreatureFactory(SimulationConfig config)
    {
        registry = new ArrayList<>();
        registry.add(new SpawnEntry(config.salmonCreationProbability,  (f, l) -> new Salmon(true, f, l)));
        registry.add(new SpawnEntry(config.codCreationProbability,     (f, l) -> new Cod(true, f, l)));
        registry.add(new SpawnEntry(config.seaweedCreationProbability, (f, l) -> new Seaweed(true, f, l)));
        registry.add(new SpawnEntry(config.sharkCreationProbability,   (f, l) -> new Shark(true, f, l)));
        registry.add(new SpawnEntry(config.whaleCreationProbability,   (f, l) -> new Whale(true, f, l)));
    }

    /**
     * Attempt to spawn a creature at the given location. Iterates the registry
     * in priority order, drawing one RNG value per entry until a match is found.
     * Returns null if no species matched (cell stays empty).
     *
     * @param field    The field the creature will inhabit.
     * @param location The cell location.
     * @return A newly created Creature, or null.
     */
    public Creature trySpawn(Field field, Location location)
    {
        Random rand = Randomizer.getRandom();
        for (SpawnEntry entry : registry) {
            if (rand.nextDouble() <= entry.probability) {
                return entry.spawner.apply(field, location);
            }
        }
        return null;
    }
}
