import java.util.Collections;
import java.util.Set;

/**
 * Typed species configuration loaded from external properties.
 *
 * @version 2022.03.02
 */
public class SpeciesConfig
{
    private final boolean diurnal;
    private final int breedingAge;
    private final int maxAge;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final double creationProbability;
    private final Integer maxHealth;
    private final Set<Species> foodSources;

    public SpeciesConfig(boolean diurnal, int breedingAge, int maxAge,
                         double breedingProbability, int maxLitterSize,
                         double creationProbability,
                         Integer maxHealth, Set<Species> foodSources)
    {
        this.diurnal = diurnal;
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.creationProbability = creationProbability;
        this.maxHealth = maxHealth;
        this.foodSources = Collections.unmodifiableSet(foodSources);
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

    public double getCreationProbability()
    {
        return creationProbability;
    }

    public Integer getMaxHealth()
    {
        return maxHealth;
    }

    public Set<Species> getFoodSources()
    {
        return foodSources;
    }
}
