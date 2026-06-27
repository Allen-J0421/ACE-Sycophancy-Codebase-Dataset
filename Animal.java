import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal
{
    /** Gender of an animal. */
    public enum Gender { M, F }

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
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
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        burn = 0;
        gender = rand.nextBoolean() ? Gender.M : Gender.F;
        age = 0;
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param step The current step.
     * @param weather The current weather.
     */
    abstract public void act(List<Animal> newAnimals, int step, String weather);

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

    // --- Existing shared state and methods ---

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if (location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if (location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
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
