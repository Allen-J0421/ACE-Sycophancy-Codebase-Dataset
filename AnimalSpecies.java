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

    private final int maxAge;
    private final int initialFoodValue;
    private final int breedingAge;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final boolean requiresDifferentSexToBreed;
    private final int mateSearchDistance;

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

}
