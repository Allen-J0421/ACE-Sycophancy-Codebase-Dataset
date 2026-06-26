import java.awt.Color;

/**
 * Central definition of the supported animal species and their creation metadata.
 */
public enum AnimalSpecies
{
    FOX(true, 0.09, CarnivoreFox.class, new Color(227, 93, 57), CarnivoreFox::new),
    REINDEER(false, 0.11, Reindeer.class, new Color(217, 162, 147), Reindeer::new),
    SHEEP(false, 0.11, Sheep.class, Color.LIGHT_GRAY, Sheep::new),
    BEAR(true, 0.04, Bear.class, new Color(112, 62, 49), Bear::new),
    WOLVERINE(true, 0.09, Wolverine.class, Color.BLACK, Wolverine::new);

    private final boolean carnivore;
    private final double spawnProbability;
    private final Class<? extends Animal> actorClass;
    private final Color color;
    private final AnimalCreator creator;

    AnimalSpecies(
        boolean carnivore,
        double spawnProbability,
        Class<? extends Animal> actorClass,
        Color color,
        AnimalCreator creator
    ) {
        this.carnivore = carnivore;
        this.spawnProbability = spawnProbability;
        this.actorClass = actorClass;
        this.color = color;
        this.creator = creator;
    }

    public boolean isCarnivore()
    {
        return carnivore;
    }

    public double getSpawnProbability()
    {
        return spawnProbability;
    }

    public Class<? extends Animal> getActorClass()
    {
        return actorClass;
    }

    public Color getColor()
    {
        return color;
    }

    public Animal createRandom(Field field, Location location)
    {
        return creator.create(true, field, location, Utils.getRandomEnumValue(Gender.class));
    }

    public Animal createYoung(Field field, Location location, Gender gender)
    {
        return creator.create(false, field, location, gender);
    }

    @FunctionalInterface
    private interface AnimalCreator
    {
        Animal create(boolean randomAge, Field field, Location location, Gender gender);
    }
}
