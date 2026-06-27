import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Shared lifecycle and diet settings for an animal species.
 *
 * @version 2022.03.02
 */
public class AnimalAttributes extends OrganismAttributes
{
    private final int maxHealth;
    private final Set<Species> foodSources;

    public AnimalAttributes(Species species, boolean diurnal, int breedingAge,
                            int maxAge, double breedingProbability,
                            int maxLitterSize, int maxHealth,
                            OrganismFactory factory, Set<Species> foodSources)
    {
        super(species, diurnal, breedingAge, maxAge, breedingProbability,
              maxLitterSize, factory);
        this.maxHealth = maxHealth;
        this.foodSources = foodSources;
    }

    public int getMaxHealth()
    {
        return maxHealth;
    }

    public Set<Species> getFoodSources()
    {
        return foodSources;
    }

    public static Set<Species> speciesSet(Species... species)
    {
        return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(species)));
    }
}
