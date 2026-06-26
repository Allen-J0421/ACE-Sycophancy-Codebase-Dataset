import java.util.*;
import java.util.function.BiFunction;

/**
 * Responsible for populating the simulation field at startup and for
 * weather-driven in-step grass growth.
 * Extracted from SimulationEngine to separate world-building from the step loop.
 *
 * @version 2022.03.02
 */
public class FieldPopulator
{
    private static final int HUNTER_LIMIT = 5;
    private static final Random rand = Randomizer.getRandom();

    private final Field field;
    private final ActorManager actorManager;
    private final Map<Class<?>, Double> probabilities;
    private int hunterCount = 0;

    /**
     * @param field         The field to populate.
     * @param actorManager  The actor manager that receives newly spawned actors.
     * @param probabilities Per-class spawn probability.
     */
    public FieldPopulator(Field field, ActorManager actorManager, Map<Class<?>, Double> probabilities)
    {
        this.field = field;
        this.actorManager = actorManager;
        this.probabilities = probabilities;
    }

    /** Reset the hunter count so populate() can be called again on a fresh simulation. */
    public void reset()
    {
        hunterCount = 0;
    }

    /**
     * Randomly populate the field with organisms.
     * The factory map is tried in insertion order; only the first matching entry
     * is spawned per cell.
     */
    public void populate()
    {
        LinkedHashMap<Class<?>, BiFunction<Location, Gender, Actor>> factories = new LinkedHashMap<>();
        factories.put(Grass.class,  (loc, sex) -> new Grass(field, loc));
        factories.put(Deer.class,   (loc, sex) -> new Deer(true, field, loc, sex));
        factories.put(Coyote.class, (loc, sex) -> new Coyote(true, field, loc, sex));
        factories.put(Wolf.class,   (loc, sex) -> new Wolf(true, field, loc, sex));
        factories.put(Eagle.class,  (loc, sex) -> new Eagle(true, field, loc, sex));
        factories.put(Mouse.class,  (loc, sex) -> new Mouse(true, field, loc, sex));
        factories.put(Hunter.class, (loc, sex) -> new Hunter(field, loc));

        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Gender sex = Randomizer.getRandomSex();
                for (Map.Entry<Class<?>, BiFunction<Location, Gender, Actor>> entry : factories.entrySet()) {
                    Class<?> cls = entry.getKey();
                    if (cls == Hunter.class && hunterCount >= HUNTER_LIMIT) continue;
                    if (rand.nextDouble() <= probabilities.get(cls)) {
                        actorManager.add(entry.getValue().apply(location, sex));
                        if (cls == Hunter.class) hunterCount++;
                        break;
                    }
                }
                // else leave the location empty
            }
        }
    }

    /**
     * Randomly generate grass in free field patches, but only when it is raining.
     * @param environment The current simulation environment.
     */
    public void plantGrassInPatches(Environment environment)
    {
        double grassProb = probabilities.get(Grass.class);
        for (Location location : field.getRandomFreePatches(grassProb)) {
            if (rand.nextDouble() <= grassProb
                    && environment.getWeather().getCurrentWeather() == WeatherType.RAINING) {
                actorManager.add(new Grass(field, location));
            }
        }
    }
}
