import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable species-specific configuration used by Animal.
 */
public final class AnimalTraits
{
    private final int breedingAge;
    private final int maxAge;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final int maxFoodLevel;
    private final int foodValue;
    private final Set<Class<?>> diet;

    public AnimalTraits(int breedingAge,
                        int maxAge,
                        double breedingProbability,
                        int maxLitterSize,
                        int maxFoodLevel,
                        int foodValue,
                        Class<?>... diet)
    {
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.maxFoodLevel = maxFoodLevel;
        this.foodValue = foodValue;
        this.diet = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(diet)));
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

    public int getMaxFoodLevel()
    {
        return maxFoodLevel;
    }

    public int getFoodValue()
    {
        return foodValue;
    }

    public Set<Class<?>> getDiet()
    {
        return diet;
    }
}
