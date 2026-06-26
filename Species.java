import java.awt.Color;
import java.util.function.BiFunction;

/**
 * Single source of truth for the per-species metadata that was previously
 * scattered across {@link Simulator}: the per-cell creation probability used
 * when populating the field, the colours used to render the species, and a
 * factory for creating new individuals.
 *
 * Constants are declared in population-priority order, i.e. the order in which
 * {@code Simulator.populate()} rolls for each grid cell. This ordering is
 * behaviourally significant (it determines the random-number draw sequence), so
 * it must not be changed casually.
 *
 * Adding a new animal species is now a single enum constant.
 */
public enum Species
{
    LION   (Lion.class,    0.0125, Color.RED,    Color.WHITE, (field, location) -> new Lion(true, field, location, false, false)),
    CHEETAH(Cheetah.class, 0.0125, Color.ORANGE, Color.BLACK, (field, location) -> new Cheetah(true, field, location, false, false)),
    ZEBRA  (Zebra.class,   0.08,   Color.BLACK,  Color.WHITE, (field, location) -> new Zebra(true, field, location, false, false)),
    GIRAFFE(Giraffe.class, 0.08,   Color.YELLOW, Color.BLACK, (field, location) -> new Giraffe(true, field, location, false, false)),
    LEMUR  (Lemur.class,   0.081,  Color.BLUE,   Color.WHITE, (field, location) -> new Lemur(true, field, location, false, false));

    // The concrete class represented, used to key the view's colour map.
    private final Class<? extends Animal> speciesClass;
    // The probability this species is created in any given grid position.
    private final double creationProbability;
    // The colour the species is drawn with, and the colour of its key text.
    private final Color fillColor;
    private final Color textColor;
    // Creates a new individual (with a random age) at a field location.
    private final BiFunction<Field, Location, Animal> spawner;

    Species(Class<? extends Animal> speciesClass, double creationProbability,
            Color fillColor, Color textColor, BiFunction<Field, Location, Animal> spawner)
    {
        this.speciesClass = speciesClass;
        this.creationProbability = creationProbability;
        this.fillColor = fillColor;
        this.textColor = textColor;
        this.spawner = spawner;
    }

    /**
     * @return The concrete class represented by this species.
     */
    public Class<? extends Animal> speciesClass()
    {
        return speciesClass;
    }

    /**
     * @return The probability of creating this species in any given grid cell.
     */
    public double creationProbability()
    {
        return creationProbability;
    }

    /**
     * @return The colour used to draw this species.
     */
    public Color fillColor()
    {
        return fillColor;
    }

    /**
     * @return The colour used for this species' key text.
     */
    public Color textColor()
    {
        return textColor;
    }

    /**
     * Create a new individual of this species at the given location.
     *
     * @param field The field to create the individual in.
     * @param location The location to place the individual at.
     * @return The newly created animal.
     */
    public Animal spawn(Field field, Location location)
    {
        return spawner.apply(field, location);
    }
}
