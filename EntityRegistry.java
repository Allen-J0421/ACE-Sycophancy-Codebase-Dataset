import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.awt.Color;

/**
 * The single source of truth for every entity type in the simulation.
 *
 * Each {@link Registration} entry bundles the four things the {@code Simulator}
 * needs to know about a species: how likely it is to appear at each cell, what
 * colour it renders as, how to construct an instance, and which Java class it
 * represents (for the view's colour map).
 *
 * To add a new species to the simulation, add one {@code Registration} line here.
 * No other class needs to change.
 */
public class EntityRegistry
{
    /**
     * Pairs an {@link EntityFactory} with the display and population metadata
     * the {@code Simulator} needs for one entity type.
     */
    public static class Registration
    {
        /** The runtime class, used to register a display colour. */
        public final Class<?>       type;
        /** Per-cell creation probability during field initialisation. */
        public final double         probability;
        /** Display colour in the simulation view. */
        public final Color          color;
        /** Factory that constructs a new instance of this entity type. */
        public final EntityFactory  factory;

        public Registration(Class<?> type, double probability,
                            Color color, EntityFactory factory)
        {
            this.type        = type;
            this.probability = probability;
            this.color       = color;
            this.factory     = factory;
        }
    }

    // -------------------------------------------------------------------------
    // Species table — add one Registration here to introduce a new species.
    // Entries are checked in insertion order per cell; the first match wins.
    // -------------------------------------------------------------------------

    private static final List<Registration> REGISTRATIONS = Collections.unmodifiableList(
        Arrays.asList(
            new Registration(Lion.class,  0.01, Color.RED,    (f, l) -> new Lion(true, f, l)),
            new Registration(Deer.class,  0.02, Color.BLUE,   (f, l) -> new Deer(true, f, l)),
            new Registration(Owl.class,   0.05, Color.ORANGE, (f, l) -> new Owl(true, f, l)),
            new Registration(Mouse.class, 0.04, Color.YELLOW, (f, l) -> new Mouse(true, f, l)),
            new Registration(Cat.class,   0.05, Color.PINK,   (f, l) -> new Cat(true, f, l)),
            new Registration(Grass.class, 0.40, Color.GREEN,  (f, l) -> new Grass(true, f, l))
        )
    );

    /** Utility class — not instantiated. */
    private EntityRegistry() {}

    /**
     * Return all registered entity types in population-priority order.
     *
     * @return An unmodifiable list of {@link Registration} entries.
     */
    public static List<Registration> getAll()
    {
        return REGISTRATIONS;
    }
}
