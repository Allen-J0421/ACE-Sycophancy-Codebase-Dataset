import java.util.Set;

/**
 * Immutable bundle of the fixed biological characteristics that define an
 * animal species: breeding limits, lifespan, appetite, food value, activity
 * period and diet.
 *
 * Previously every {@link Animal} subclass repeated these as a block of static
 * constants together with a matching set of trivial accessor methods
 * (e.g. {@code MAX_AGE} plus {@code MAX_AGE()}). Gathering them here lets each
 * subclass declare its species profile once and removes that boilerplate from
 * the hierarchy.
 *
 * @version 2022.03.02
 */
public class AnimalTraits
{
    // The age at which the animal can start to breed.
    private final double breedingAge;
    // The maximum number of births in a single litter.
    private final int maxLitterSize;
    // The likelihood of the animal breeding in a given step.
    private final double breedingProbability;
    // The age to which the animal can live.
    private final int maxAge;
    // The food level the animal can reach by eating.
    private final int maxFoodLevel;
    // The food value this animal provides when eaten.
    private final int foodValue;
    // Whether the animal is active at night rather than during the day.
    private final boolean nocturnal;
    // The set of organism classes this animal eats.
    private final Set<Class> diet;

    /**
     * Create an immutable description of a species' characteristics.
     */
    public AnimalTraits(double breedingAge, int maxLitterSize, double breedingProbability,
                        int maxAge, int maxFoodLevel, int foodValue, boolean nocturnal, Set<Class> diet)
    {
        this.breedingAge = breedingAge;
        this.maxLitterSize = maxLitterSize;
        this.breedingProbability = breedingProbability;
        this.maxAge = maxAge;
        this.maxFoodLevel = maxFoodLevel;
        this.foodValue = foodValue;
        this.nocturnal = nocturnal;
        this.diet = diet;
    }

    public double getBreedingAge()         { return breedingAge; }
    public int getMaxLitterSize()          { return maxLitterSize; }
    public double getBreedingProbability() { return breedingProbability; }
    public int getMaxAge()                 { return maxAge; }
    public int getMaxFoodLevel()           { return maxFoodLevel; }
    public int getFoodValue()              { return foodValue; }
    public boolean isNocturnal()           { return nocturnal; }
    public Set<Class> getDiet()            { return diet; }
}
