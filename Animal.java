import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * <p>Species-specific parameters (max age, breeding rates, food value, etc.)
 * are no longer declared as constants in each subclass. Instead, every animal
 * receives a {@link SpeciesConfig.AnimalParams} object at construction time,
 * and {@code Animal} provides concrete getter methods that read from it. This
 * keeps all tuneable values in {@link SpeciesConfig} and out of the species files.
 *
 * <p>The shared {@code rand} field is {@code protected} so subclass constructors
 * can use it for random-age initialisation without declaring their own instance.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Entity
{
    /** Gender of an animal. */
    public enum Gender { M, F }

    // Shared RNG — protected so species constructors can use it directly.
    protected static final Random rand = Randomizer.getRandom();

    // Tunable species parameters supplied at construction time.
    private final SpeciesConfig.AnimalParams config;

    // The animal's burn level; when > 3 the animal dies.
    private int burn;
    // The animal's gender.
    private Gender gender;
    // The animal's age (accessible by subclasses for random initialisation).
    protected int age;

    /**
     * Create a new animal at location in field.
     *
     * @param config The species parameters for this animal type.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(SpeciesConfig.AnimalParams config, Field<Entity> field, Location location)
    {
        super(field, location);
        this.config = config;
        burn = 0;
        gender = rand.nextBoolean() ? Gender.M : Gender.F;
        age = 0;
    }

    /**
     * Make this animal act.
     * @param newEntities A list to receive any animals born this step.
     * @param step The current step.
     * @param weather The current weather.
     */
    @Override
    public abstract void act(List<Entity> newEntities, int step, String weather);

    // --- Species configuration (read from SpeciesConfig, not overridden per class) ---

    protected int    getMaxAge()              { return config.maxAge; }
    protected int    getBreedingAge()         { return config.breedingAge; }
    protected double getBreedingProbability() { return config.breedingProbability; }
    protected int    getMaxLitterSize()       { return config.maxLitterSize; }
    public    int    foodValue()              { return config.foodValue; }
    /** Upper bound on food level used when spawning at a random age. */
    protected int    getMaxFoodLevel()        { return config.maxFoodLevel; }
    /** Starting food level for a newly born (age 0) animal. */
    protected int    getInitialFoodLevel()    { return config.initialFoodLevel; }

    // --- Shared lifecycle logic ---

    /**
     * Increment age; the animal dies if it exceeds its maximum age.
     */
    protected void incrementAge()
    {
        age++;
        if (age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * @return true if this animal has reached breeding age.
     */
    protected boolean canBreed()
    {
        return age >= getBreedingAge();
    }

    /**
     * Generate the number of offspring for this step.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Update the animal's burn status based on the current weather.
     * @param weather The current weather.
     */
    protected void updateBurnStatus(String weather)
    {
        if (burn > 0) {
            if (!weather.equals("Rainy")) {
                burn();
            } else {
                recover();
            }
        }
    }

    /**
     * Increment burn damage; the animal dies if burn exceeds 3.
     */
    protected void burn()
    {
        burn++;
        if (burn > 3) {
            setDead();
        }
    }

    /**
     * Reset burn damage (called when weather is rainy).
     */
    protected void recover()
    {
        burn = 0;
    }

    /**
     * @return the animal's gender.
     */
    protected Gender getGender()
    {
        return gender;
    }

    /** Animals count toward the viability check. */
    @Override
    public boolean countsTowardViability()
    {
        return true;
    }
}
