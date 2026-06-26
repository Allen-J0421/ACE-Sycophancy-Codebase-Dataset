import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Immutable configuration for an animal species.
 */
public class AnimalProfile
{
    @FunctionalInterface
    public interface AnimalFactory
    {
        Animal create(Field field, Location location);
    }

    private Class<? extends Animal> speciesClass;
    private int maxAge;
    private int breedingAge;
    private double breedingProbability;
    private int maxLitterSize;
    private int foodValue;
    private int mateSearchDistance;
    private boolean requiresMate;
    private List<Class<? extends Creature>> preyTypes;
    private AnimalFactory animalFactory;

    @SafeVarargs
    public AnimalProfile(Class<? extends Animal> speciesClass, int maxAge, int breedingAge,
                         double breedingProbability, int maxLitterSize, int foodValue,
                         int mateSearchDistance, boolean requiresMate, AnimalFactory animalFactory,
                         Class<? extends Creature>... preyTypes)
    {
        this.speciesClass = speciesClass;
        this.maxAge = maxAge;
        this.breedingAge = breedingAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.foodValue = foodValue;
        this.mateSearchDistance = mateSearchDistance;
        this.requiresMate = requiresMate;
        this.animalFactory = animalFactory;
        this.preyTypes = Collections.unmodifiableList(Arrays.asList(preyTypes));
    }

    public Class<? extends Animal> getSpeciesClass()
    {
        return speciesClass;
    }

    public int getMaxAge()
    {
        return maxAge;
    }

    public int getBreedingAge()
    {
        return breedingAge;
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

    public int getMateSearchDistance()
    {
        return mateSearchDistance;
    }

    public boolean requiresMate()
    {
        return requiresMate;
    }

    public List<Class<? extends Creature>> getPreyTypes()
    {
        return preyTypes;
    }

    public Animal createYoung(Field field, Location location)
    {
        return animalFactory.create(field, location);
    }
}
