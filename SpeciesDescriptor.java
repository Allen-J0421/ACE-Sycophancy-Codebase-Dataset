import java.awt.Color;

/**
 * Describes a single species that can populate the simulation: the class that
 * represents it, the colour it is drawn with, how likely it is to be created
 * when the field is seeded, and how to construct a new instance of it.
 *
 * Holding these pieces of data together in one place removes the need to keep
 * three parallel lists (creation probabilities, colours and constructor calls)
 * in sync inside {@link Simulator}. Adding a new species becomes a single new
 * entry rather than an edit in several locations.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class SpeciesDescriptor
{
    /**
     * A factory that creates a new actor of this species at a location.
     * This hides the differences between consumer and producer constructors
     * (consumers take a randomAge flag, producers do not) behind a uniform
     * call.
     */
    @FunctionalInterface
    public interface ActorFactory
    {
        Actor create(Field field, Location location);
    }

    // The class representing this species:
    private final Class<? extends Actor> species;
    // The colour this species is drawn with in the view:
    private final Color color;
    // The probability of creating one of these when seeding a location:
    private final double creationProbability;
    // A factory used to construct a new instance of this species:
    private final ActorFactory factory;

    /**
     * Create a descriptor for a species.
     *
     * @param species             The class representing the species.
     * @param color               The colour the species is drawn with.
     * @param creationProbability The probability of seeding one per location.
     * @param factory             A factory creating a new instance.
     */
    public SpeciesDescriptor(Class<? extends Actor> species, Color color,
                             double creationProbability, ActorFactory factory)
    {
        this.species = species;
        this.color = color;
        this.creationProbability = creationProbability;
        this.factory = factory;
    }

    /**
     * @return The class representing this species.
     */
    public Class<? extends Actor> getSpecies() { return species; }

    /**
     * @return The colour this species is drawn with.
     */
    public Color getColor() { return color; }

    /**
     * @return The probability of seeding this species at a given location.
     */
    public double getCreationProbability() { return creationProbability; }

    /**
     * Create a new instance of this species.
     *
     * @param field    The field the new actor occupies.
     * @param location The location of the new actor.
     * @return The newly created actor.
     */
    public Actor create(Field field, Location location)
    {
        return factory.create(field, location);
    }
}
