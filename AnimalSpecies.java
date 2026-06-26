import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Species-level animal configuration and offspring factory.
 */
public enum AnimalSpecies
{
    COD(50, 13, 6, 0.3, 10, true, 1) {
        public Animal createYoung(Field field, Location location)
        {
            return new Cod(false, field, location);
        }
    },
    SALMON(50, 13, 4, 0.3, 15, true, 2) {
        public Animal createYoung(Field field, Location location)
        {
            return new Salmon(false, field, location);
        }
    },
    SHARK(150, 8, 6, 0.4, 8, false, 2) {
        public Animal createYoung(Field field, Location location)
        {
            return new Shark(false, field, location);
        }
    },
    WHALE(150, 8, 6, 0.2, 8, false, 2) {
        public Animal createYoung(Field field, Location location)
        {
            return new Whale(false, field, location);
        }
    };

    private enum FoodSource
    {
        SEAWEED,
        COD,
        SALMON
    }

    private final int maxAge;
    private final int initialFoodValue;
    private final int breedingAge;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final boolean requiresDifferentSexToBreed;
    private final int mateSearchDistance;
    private final Map<FoodSource, Integer> foodValues;
    private final Set<AnimalSpecies> compatibleMates;

    static {
        COD.eats(FoodSource.SEAWEED, 13)
           .matesWith(COD);
        SALMON.eats(FoodSource.SEAWEED, 13)
              .matesWith(SALMON);
        SHARK.eats(FoodSource.COD, 8)
             .eats(FoodSource.SALMON, 8)
             .matesWith(COD);
        WHALE.eats(FoodSource.COD, 8)
             .eats(FoodSource.SALMON, 8)
             .matesWith(SALMON);
    }

    AnimalSpecies(int maxAge, int initialFoodValue, int breedingAge, double breedingProbability,
                  int maxLitterSize, boolean requiresDifferentSexToBreed, int mateSearchDistance)
    {
        this.maxAge = maxAge;
        this.initialFoodValue = initialFoodValue;
        this.breedingAge = breedingAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.requiresDifferentSexToBreed = requiresDifferentSexToBreed;
        this.mateSearchDistance = mateSearchDistance;
        foodValues = new EnumMap<>(FoodSource.class);
        compatibleMates = EnumSet.noneOf(AnimalSpecies.class);
    }

    public abstract Animal createYoung(Field field, Location location);

    public int getMaxAge()
    {
        return maxAge;
    }

    public int getInitialFoodValue()
    {
        return initialFoodValue;
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

    public boolean requiresDifferentSexToBreed()
    {
        return requiresDifferentSexToBreed;
    }

    public int getMateSearchDistance()
    {
        return mateSearchDistance;
    }

    public int getFoodValueFromSeaweed()
    {
        return getFoodValue(FoodSource.SEAWEED);
    }

    public int getFoodValueFrom(AnimalSpecies prey)
    {
        FoodSource preySource = foodSourceFor(prey);
        if(preySource == null) {
            return 0;
        }
        return getFoodValue(preySource);
    }

    public boolean canMateWith(AnimalSpecies other)
    {
        return compatibleMates.contains(other);
    }

    private AnimalSpecies eats(FoodSource foodSource, int foodValue)
    {
        foodValues.put(foodSource, foodValue);
        return this;
    }

    private AnimalSpecies matesWith(AnimalSpecies mate)
    {
        compatibleMates.add(mate);
        return this;
    }

    private int getFoodValue(FoodSource foodSource)
    {
        Integer value = foodValues.get(foodSource);
        return value == null ? 0 : value;
    }

    private static FoodSource foodSourceFor(AnimalSpecies species)
    {
        switch(species) {
            case COD:
                return FoodSource.COD;
            case SALMON:
                return FoodSource.SALMON;
            default:
                return null;
        }
    }
}
