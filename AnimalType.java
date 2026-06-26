import java.util.Locale;

/**
 * Enumerates the animal species available in the simulation.
 */
public enum AnimalType
{
    FOX(true, 0.09)
    {
        @Override
        Animal create(boolean randomAge, Field field, Location location, Gender gender)
        {
            return new CarnivoreFox(randomAge, field, location, gender);
        }
    },
    WOLVERINE(true, 0.09)
    {
        @Override
        Animal create(boolean randomAge, Field field, Location location, Gender gender)
        {
            return new Wolverine(randomAge, field, location, gender);
        }
    },
    BEAR(true, 0.04)
    {
        @Override
        Animal create(boolean randomAge, Field field, Location location, Gender gender)
        {
            return new Bear(randomAge, field, location, gender);
        }
    },
    SHEEP(false, 0.11)
    {
        @Override
        Animal create(boolean randomAge, Field field, Location location, Gender gender)
        {
            return new Sheep(randomAge, field, location, gender);
        }
    },
    REINDEER(false, 0.11)
    {
        @Override
        Animal create(boolean randomAge, Field field, Location location, Gender gender)
        {
            return new Reindeer(randomAge, field, location, gender);
        }
    };

    private final boolean carnivore;
    private final double spawnProbability;

    AnimalType(boolean carnivore, double spawnProbability)
    {
        this.carnivore = carnivore;
        this.spawnProbability = spawnProbability;
    }

    public boolean isCarnivore()
    {
        return carnivore;
    }

    public double getSpawnProbability()
    {
        return spawnProbability;
    }

    abstract Animal create(boolean randomAge, Field field, Location location, Gender gender);

    public Animal createWithRandomGender(Field field, Location location)
    {
        Gender randomGender = Utils.getRandomEnumValue(Gender.class);
        return create(true, field, location, randomGender);
    }

    public static AnimalType fromString(String animalType)
    {
        if(animalType == null) {
            return null;
        }
        return valueOf(animalType.trim().toUpperCase(Locale.ROOT));
    }
}
