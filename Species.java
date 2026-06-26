import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Single source of truth for every species in the simulation.
 *
 * Each constant bundles all per-species metadata the rest of the program needs:
 * the actor class, a display name, the GUI colour, the default creation
 * probability, the maximum number that may be spawned, and a factory that
 * creates one. Adding a species is a one-line change here: the population logic
 * ({@link SimulationEngine}) and the view's colours, population checkboxes and
 * tooltips ({@link SimulatorView}) all derive from this list, so no other file
 * needs editing.
 *
 * The order of the constants is the population priority order: when a cell is
 * populated, species are considered top-to-bottom and the first whose
 * probability succeeds is placed.
 *
 * @version 2022.03.02
 */
public enum Species
{
    //      class         display    colour         prob    maxCount            spawner
    GRASS  (Grass.class,  "Grass",   Color.GREEN,   0.030,  Integer.MAX_VALUE, (f, l, s, e) -> new Grass(f, l)),
    DEER   (Deer.class,   "Deer",    Color.ORANGE,  0.080,  Integer.MAX_VALUE, (f, l, s, e) -> new Deer(true, f, l, s)),
    COYOTE (Coyote.class, "Coyote",  Color.BLUE,    0.010,  Integer.MAX_VALUE, (f, l, s, e) -> new Coyote(true, f, l, s)),
    WOLF   (Wolf.class,   "Wolf",    Color.RED,     0.010,  Integer.MAX_VALUE, (f, l, s, e) -> new Wolf(true, f, l, s)),
    EAGLE  (Eagle.class,  "Eagle",   Color.MAGENTA, 0.010,  Integer.MAX_VALUE, (f, l, s, e) -> new Eagle(true, f, l, s)),
    MOUSE  (Mouse.class,  "Mouse",   Color.PINK,    0.080,  Integer.MAX_VALUE, (f, l, s, e) -> new Mouse(true, f, l, s)),
    HUNTER (Hunter.class, "Hunter",  Color.BLACK,   0.030,  5,                 (f, l, s, e) -> new Hunter(f, l, e));

    /**
     * Factory that creates a single actor of a species at a location.
     */
    @FunctionalInterface
    public interface Spawner {
        Actor spawn(Field field, Location location, Animal.Gender sex, Environment environment);
    }

    private final Class<?> actorClass;
    private final String displayName;
    private final Color color;
    private final double defaultProbability;
    private final int maxCount;
    private final Spawner spawner;

    Species(Class<?> actorClass, String displayName, Color color,
            double defaultProbability, int maxCount, Spawner spawner)
    {
        this.actorClass = actorClass;
        this.displayName = displayName;
        this.color = color;
        this.defaultProbability = defaultProbability;
        this.maxCount = maxCount;
        this.spawner = spawner;
    }

    /** @return The actor class this species creates. */
    public Class<?> actorClass() { return actorClass; }

    /** @return A human-readable name for the species. */
    public String displayName() { return displayName; }

    /** @return The colour used to draw this species in the view. */
    public Color color() { return color; }

    /** @return The default probability of creating this species in a free cell. */
    public double defaultProbability() { return defaultProbability; }

    /** @return The maximum number of this species that may be spawned. */
    public int maxCount() { return maxCount; }

    /**
     * Create a single actor of this species.
     */
    public Actor spawn(Field field, Location location, Animal.Gender sex, Environment environment)
    {
        return spawner.spawn(field, location, sex, environment);
    }

    // Reverse lookup from actor class to species.
    private static final Map<Class<?>, Species> BY_CLASS = new HashMap<>();
    static {
        for(Species species : values()) {
            BY_CLASS.put(species.actorClass, species);
        }
    }

    /**
     * @param actorClass An actor class.
     * @return The species for that class, or null if it is not a registered species.
     */
    public static Species forClass(Class<?> actorClass)
    {
        return BY_CLASS.get(actorClass);
    }
}
