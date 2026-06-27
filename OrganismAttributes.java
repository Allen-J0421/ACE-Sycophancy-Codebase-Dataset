/**
 * Shared lifecycle settings for an organism species.
 *
 * @version 2022.03.02
 */
public class OrganismAttributes
{
    private final Species species;
    private final boolean diurnal;
    private final int breedingAge;
    private final int maxAge;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final OrganismFactory factory;

    public OrganismAttributes(Species species, boolean diurnal, int breedingAge,
                              int maxAge, double breedingProbability,
                              int maxLitterSize, OrganismFactory factory)
    {
        this.species = species;
        this.diurnal = diurnal;
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.factory = factory;
    }

    public Species getSpecies()
    {
        return species;
    }

    public boolean isDiurnal()
    {
        return diurnal;
    }

    public int getBreedingAge()
    {
        return breedingAge;
    }

    public int getMaxAge()
    {
        return maxAge;
    }

    public double getBreedingProbability()
    {
        return breedingProbability;
    }

    public int getMaxLitterSize()
    {
        return maxLitterSize;
    }

    public Organism create(boolean randomAge, Field field, Location location)
    {
        return factory.create(randomAge, field, location);
    }
}
