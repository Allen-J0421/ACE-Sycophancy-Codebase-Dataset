import java.awt.Color;
import java.util.Locale;

/**
 * Enumerates the animal species available in the simulation.
 */
public enum AnimalType
{
    FOX(true, 0.09, "Fox", new Color(227, 93, 57), CarnivoreFox.class)
    {
        @Override
        Animal create(boolean randomAge, Field field, Location location, Gender gender)
        {
            return new CarnivoreFox(randomAge, field, location, gender);
        }
    },
    REINDEER(false, 0.11, "Reindeer", new Color(217, 162, 147), Reindeer.class)
    {
        @Override
        Animal create(boolean randomAge, Field field, Location location, Gender gender)
        {
            return new Reindeer(randomAge, field, location, gender);
        }
    },
    SHEEP(false, 0.11, "Sheep", Color.LIGHT_GRAY, Sheep.class)
    {
        @Override
        Animal create(boolean randomAge, Field field, Location location, Gender gender)
        {
            return new Sheep(randomAge, field, location, gender);
        }
    },
    BEAR(true, 0.04, "Bear", new Color(112, 62, 49), Bear.class)
    {
        @Override
        Animal create(boolean randomAge, Field field, Location location, Gender gender)
        {
            return new Bear(randomAge, field, location, gender);
        }
    },
    WOLVERINE(true, 0.09, "Wolverine", Color.BLACK, Wolverine.class)
    {
        @Override
        Animal create(boolean randomAge, Field field, Location location, Gender gender)
        {
            return new Wolverine(randomAge, field, location, gender);
        }
    };

    private final boolean carnivore;
    private final double spawnProbability;
    private final String displayName;
    private final Color color;
    private final Class<? extends Animal> actorClass;

    AnimalType(boolean carnivore, double spawnProbability, String displayName, Color color, Class<? extends Animal> actorClass)
    {
        this.carnivore = carnivore;
        this.spawnProbability = spawnProbability;
        this.displayName = displayName;
        this.color = color;
        this.actorClass = actorClass;
    }

    public boolean isCarnivore()
    {
        return carnivore;
    }

    public double getSpawnProbability()
    {
        return spawnProbability;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Color getColor()
    {
        return color;
    }

    public Class<? extends Animal> getActorClass()
    {
        return actorClass;
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
        try {
            return valueOf(animalType.trim().toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static AnimalType fromActorClass(Class<?> actorClass)
    {
        if(actorClass == null) {
            return null;
        }
        for(AnimalType type : values()) {
            if(type.getActorClass().equals(actorClass)) {
                return type;
            }
        }
        return null;
    }
}
