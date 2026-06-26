/**
 * Immutable configuration describing one animal species.
 */
public class AnimalProfile extends SpeciesProfile
{
    private final boolean predator;
    private final int maximumAge;
    private final int breedingAge;
    private final int maxLitterSize;
    private final int strength;
    private final boolean hibernates;
    private final boolean nocturnal;

    public AnimalProfile(String name, boolean predator, int maximumTemperature, int minimumTemperature, int maximumAge, int breedingAge, double breedingProbability, int maxLitterSize, int nutritionalValue, int strength, boolean hibernates, boolean nocturnal)
    {
        super(name, maximumTemperature, minimumTemperature, nutritionalValue, breedingProbability);
        this.predator = predator;
        this.maximumAge = maximumAge;
        this.breedingAge = breedingAge;
        this.maxLitterSize = maxLitterSize;
        this.strength = strength;
        this.hibernates = hibernates;
        this.nocturnal = nocturnal;
    }

    public boolean isPredator()
    {
        return predator;
    }

    public int getMaximumAge()
    {
        return maximumAge;
    }

    public int getBreedingAge()
    {
        return breedingAge;
    }

    public int getMaxLitterSize()
    {
        return maxLitterSize;
    }

    public int getStrength()
    {
        return strength;
    }

    public boolean canHibernate()
    {
        return hibernates;
    }

    public boolean isNocturnal()
    {
        return nocturnal;
    }
}
