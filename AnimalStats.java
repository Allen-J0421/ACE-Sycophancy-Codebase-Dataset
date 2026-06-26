import java.util.Set;

/**
 * Immutable configuration bundle for an animal species.
 * Centralises all species-specific constants so that each subclass only needs
 * to provide a single static STATS instance rather than implementing abstract
 * getter methods or setting protected fields in its constructor.
 *
 * @version 2022.03.02
 */
public class AnimalStats
{
    private final double breedingAge;
    private final int maxLitterSize;
    private final double breedingProbability;
    private final int maxAge;
    private final int maxFoodLevel;
    private final int foodValue;
    private final Set<Class> diet;
    private final boolean nocturnal;

    public AnimalStats(double breedingAge, int maxLitterSize, double breedingProbability,
                       int maxAge, int maxFoodLevel, int foodValue, Set<Class> diet,
                       boolean nocturnal)
    {
        this.breedingAge = breedingAge;
        this.maxLitterSize = maxLitterSize;
        this.breedingProbability = breedingProbability;
        this.maxAge = maxAge;
        this.maxFoodLevel = maxFoodLevel;
        this.foodValue = foodValue;
        this.diet = diet;
        this.nocturnal = nocturnal;
    }

    public double    getBreedingAge()        { return breedingAge; }
    public int       getMaxLitterSize()       { return maxLitterSize; }
    public double    getBreedingProbability() { return breedingProbability; }
    public int       getMaxAge()              { return maxAge; }
    public int       getMaxFoodLevel()        { return maxFoodLevel; }
    public int       getFoodValue()           { return foodValue; }
    public Set<Class> getDiet()              { return diet; }
    public boolean   isNocturnal()            { return nocturnal; }
}
