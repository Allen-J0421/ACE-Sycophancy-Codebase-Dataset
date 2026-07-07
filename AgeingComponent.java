import java.util.Random;

/**
 * Tracks the age of an entity and signals when it should die of old age.
 * Constructed with a maximum lifespan; tick() increments age each step
 * and returns true when the entity has exceeded its maximum.
 *
 * @version 2022.03.02
 */
public class AgeingComponent
{
    private static final Random rand = Randomizer.getRandom();

    private int age;
    private final int maxAge;

    /**
     * @param maxAge    Maximum age before the entity should die.
     * @param randomAge If true, start at a random age in [0, maxAge); otherwise start at 0.
     */
    public AgeingComponent(int maxAge, boolean randomAge)
    {
        this.maxAge = maxAge;
        this.age    = randomAge ? rand.nextInt(maxAge) : 0;
    }

    /**
     * Advance age by one step.
     * @return true if the entity has exceeded its maximum age and should die.
     */
    public boolean tick()
    {
        age++;
        return age > maxAge;
    }

    public int getAge() { return age; }

    /** Returns true if the entity has reached the given minimum age. */
    public boolean isOldEnough(double minAge) { return age >= minAge; }
}
