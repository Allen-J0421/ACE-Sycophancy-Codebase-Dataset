import java.util.function.BiFunction;

/**
 * Immutable configuration for foraging animal behavior.
 */
public final class ForagingBehavior
{
    private final int breedingAge;
    private final int maxAge;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final int foodValue;
    private final int searchDistance;
    private final Class<? extends Animal> mateSpecies;
    private final int mateDistance;
    private final boolean breedingRequiresMate;
    private final Class<?>[] preyTypes;
    private final BiFunction<Field, Location, Creature> offspringFactory;

    public ForagingBehavior(
        int breedingAge,
        int maxAge,
        double breedingProbability,
        int maxLitterSize,
        int foodValue,
        int searchDistance,
        Class<? extends Animal> mateSpecies,
        int mateDistance,
        boolean breedingRequiresMate,
        BiFunction<Field, Location, Creature> offspringFactory,
        Class<?>... preyTypes)
    {
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.foodValue = foodValue;
        this.searchDistance = searchDistance;
        this.mateSpecies = mateSpecies;
        this.mateDistance = mateDistance;
        this.breedingRequiresMate = breedingRequiresMate;
        this.offspringFactory = offspringFactory;
        this.preyTypes = preyTypes.clone();
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

    public int getFoodValue()
    {
        return foodValue;
    }

    public int getSearchDistance()
    {
        return searchDistance;
    }

    public Class<? extends Animal> getMateSpecies()
    {
        return mateSpecies;
    }

    public int getMateDistance()
    {
        return mateDistance;
    }

    public boolean breedingRequiresMate()
    {
        return breedingRequiresMate;
    }

    public Class<?>[] getPreyTypes()
    {
        return preyTypes.clone();
    }

    public Creature createOffspring(Field field, Location location)
    {
        return offspringFactory.apply(field, location);
    }
}
