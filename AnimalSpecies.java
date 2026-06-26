/**
 * Species-level animal configuration and offspring factory.
 */
public enum AnimalSpecies
{
    COD(50, 13, 6, 0.3, 10, true, 1, 13, 0, 0, null) {
        public Animal createYoung(Field field, Location location)
        {
            return new Cod(false, field, location);
        }
    },
    SALMON(50, 13, 4, 0.3, 15, true, 2, 13, 0, 0, null) {
        public Animal createYoung(Field field, Location location)
        {
            return new Salmon(false, field, location);
        }
    },
    SHARK(150, 8, 6, 0.4, 8, false, 2, 0, 8, 8, COD) {
        public Animal createYoung(Field field, Location location)
        {
            return new Shark(false, field, location);
        }
    },
    WHALE(150, 8, 6, 0.2, 8, false, 2, 0, 8, 8, SALMON) {
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
    private final int seaweedFoodValue;
    private final int codFoodValue;
    private final int salmonFoodValue;
    private final AnimalSpecies compatibleMate;

    AnimalSpecies(int maxAge, int initialFoodValue, int breedingAge, double breedingProbability,
                  int maxLitterSize, boolean requiresDifferentSexToBreed, int mateSearchDistance,
                  int seaweedFoodValue, int codFoodValue, int salmonFoodValue,
                  AnimalSpecies compatibleMate)
    {
        this.maxAge = maxAge;
        this.initialFoodValue = initialFoodValue;
        this.breedingAge = breedingAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.requiresDifferentSexToBreed = requiresDifferentSexToBreed;
        this.mateSearchDistance = mateSearchDistance;
        this.seaweedFoodValue = seaweedFoodValue;
        this.codFoodValue = codFoodValue;
        this.salmonFoodValue = salmonFoodValue;
        this.compatibleMate = compatibleMate == null ? this : compatibleMate;
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
        return seaweedFoodValue;
    }

    public int getFoodValueFrom(AnimalSpecies prey)
    {
        if(prey == COD) {
            return codFoodValue;
        }
        if(prey == SALMON) {
            return salmonFoodValue;
        }
        return 0;
    }

    public boolean canMateWith(AnimalSpecies other)
    {
        return compatibleMate == other;
    }
}
