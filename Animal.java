import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Entity
{
    /** Gender of an animal. */
    public enum Gender { M, F }

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // The animal's burn level; when > 3 the animal dies.
    private int burn;
    // The animal's gender.
    private Gender gender;
    // The animal's age (accessible by subclasses for random initialisation).
    protected int age;

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field<Entity> field, Location location)
    {
        super(field, location);
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

    // --- Species configuration: each subclass provides these ---

    /** @return this species' maximum lifespan. */
    protected abstract int getMaxAge();

    /** @return the minimum age at which this species can breed. */
    protected abstract int getBreedingAge();

    /** @return the per-step probability of breeding when eligible. */
    protected abstract double getBreedingProbability();

    /** @return the maximum litter size. */
    protected abstract int getMaxLitterSize();

    /** @return the food value this animal provides when eaten. */
    public abstract int foodValue();

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
}
