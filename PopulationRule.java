import java.awt.Color;

/**
 * Configuration describing how a species should be seeded and displayed.
 *
 * @version 2022.03.02
 */
public class PopulationRule
{
    private final double creationProbability;
    private final OrganismFactory factory;
    private final Species species;
    private final Color color;

    public PopulationRule(double creationProbability, OrganismFactory factory,
                          Species species, Color color)
    {
        this.creationProbability = creationProbability;
        this.factory = factory;
        this.species = species;
        this.color = color;
    }

    public double getCreationProbability()
    {
        return creationProbability;
    }

    public Organism create(Field field, Location location)
    {
        return factory.create(true, field, location);
    }

    public Species getSpecies()
    {
        return species;
    }

    public Color getColor()
    {
        return color;
    }
}
