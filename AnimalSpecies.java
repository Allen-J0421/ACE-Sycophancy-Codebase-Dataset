import java.awt.Color;

/**
 * Central definition of the supported animal species and their creation metadata.
 */
public enum AnimalSpecies implements SpawnableActorType<Animal>
{
    FOX("Fox", true, 0.09, new Color(227, 93, 57), 25, 18, 71, 12, 0.076, 3, false, 1.0, CarnivoreFox::new),
    REINDEER("Reindeer", false, 0.11, new Color(217, 162, 147), 15, 18, 28, 4, 0.7, 5, true, 1.0, Reindeer::new),
    SHEEP("Sheep", false, 0.11, Color.LIGHT_GRAY, 6, 18, 20, 3, 0.6, 5, true, 1.0, Sheep::new),
    BEAR("Bear", true, 0.04, new Color(112, 62, 49), 18, 30, 80, 25, 0.115, 3, false, 0.5, Bear::new),
    WOLVERINE("Wolverine", true, 0.09, Color.BLACK, 25, 18, 83, 8, 0.0702, 3, true, 1.0, Wolverine::new);

    private final String displayName;
    private final boolean carnivore;
    private final double spawnProbability;
    private final Color color;
    private final int baseHungerLevel;
    private final int feedingValue;
    private final int maxAge;
    private final int breedingAge;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final boolean activeAtNight;
    private final double snowBreedingFactor;
    private final AnimalCreator creator;

    AnimalSpecies(
        String displayName,
        boolean carnivore,
        double spawnProbability,
        Color color,
        int baseHungerLevel,
        int feedingValue,
        int maxAge,
        int breedingAge,
        double breedingProbability,
        int maxLitterSize,
        boolean activeAtNight,
        double snowBreedingFactor,
        AnimalCreator creator
    ) {
        this.displayName = displayName;
        this.carnivore = carnivore;
        this.spawnProbability = spawnProbability;
        this.color = color;
        this.baseHungerLevel = baseHungerLevel;
        this.feedingValue = feedingValue;
        this.maxAge = maxAge;
        this.breedingAge = breedingAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.activeAtNight = activeAtNight;
        this.snowBreedingFactor = snowBreedingFactor;
        this.creator = creator;
    }

    public boolean isCarnivore()
    {
        return carnivore;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public ActorCategory getCategory()
    {
        return ActorCategory.ANIMAL;
    }

    @Override
    public double getSpawnProbability()
    {
        return spawnProbability;
    }

    @Override
    public Color getColor()
    {
        return color;
    }

    public int getBaseHungerLevel()
    {
        return baseHungerLevel;
    }

    public int getFeedingValue()
    {
        return feedingValue;
    }

    public int getMaxAge()
    {
        return maxAge;
    }

    public int getBreedingAge()
    {
        return breedingAge;
    }

    public double getBreedingProbability(Weather weather)
    {
        if(weather == Weather.SNOW) {
            return snowBreedingFactor * breedingProbability;
        }
        return breedingProbability;
    }

    public int getMaxLitterSize()
    {
        return maxLitterSize;
    }

    public boolean canActDuring(DayState dayState)
    {
        return activeAtNight || dayState != DayState.NIGHT;
    }

    @Override
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
