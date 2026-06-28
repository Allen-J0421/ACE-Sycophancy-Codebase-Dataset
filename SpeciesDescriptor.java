import java.awt.Color;

/**
 * Describes a species in the simulation: its class, display colour,
 * creation probability during population, and a factory for creating
 * new instances.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class SpeciesDescriptor
{
    @FunctionalInterface
    public interface ActorFactory
    {
        Actor create(Field field, Location location);
    }

    private final Class<?> speciesClass;
    private final Color color;
    private final double creationProbability;
    private final ActorFactory factory;

    public SpeciesDescriptor(Class<?> speciesClass, Color color,
                             double creationProbability, ActorFactory factory)
    {
        this.speciesClass = speciesClass;
        this.color = color;
        this.creationProbability = creationProbability;
        this.factory = factory;
    }

    public Class<?> getSpeciesClass()       { return speciesClass; }
    public Color    getColor()              { return color; }
    public double   getCreationProbability(){ return creationProbability; }
    public Actor    create(Field field, Location location)
    {
        return factory.create(field, location);
    }
}
