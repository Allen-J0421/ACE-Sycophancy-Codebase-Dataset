import java.util.Random;
import java.util.List;
/**
 * A blueprint for how a typical plant behaves. A plant grows according to the weather
 * conditions and daylight, and feeds more as it grows.
 *
 * @version 1.0
 */
public abstract class Plant implements Actor
{

    /*///////////////////////////////////////////////////////////////
                                  STATE
    //////////////////////////////////////////////////////////////*/

    private static final int FEEDING_FACTOR = 2;
    private boolean alive;
    private Location location;
    private int age;
    private int feedingValue;
    private Field field;

    private static final Random rand = Randomizer.getRandom();

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a plant.
     *
     * @param randomAge Boolean flag denoting whether or not to randomly generate an age for this plant
     * @param field The field currently occupied
     * @param location The location of the plant
     * @param maxAge The maximum age the plant can take
     */
    public Plant(boolean randomAge, Field field, Location location, int maxAge)
    {
        if(randomAge) {
            this.age = rand.nextInt(maxAge);
            this.feedingValue = age * FEEDING_FACTOR;
        } else {
            this.age = 0;
            this.feedingValue = 0;
        }
        this.alive = true;
        this.field = field;
        setLocation(location);
    }

    /*///////////////////////////////////////////////////////////////
                        ABSTRACT BEHAVIOUR HOOKS
    //////////////////////////////////////////////////////////////*/

    protected abstract int getMaxAge();
    protected abstract double getMultiplyProbability();
    protected abstract Plant createOffspring(Field field, Location location);

    /** Factor applied to multiply probability under optimal weather. Override to customise. */
    protected double getOptimalBreedingFactor() { return 4.0; }

    /*///////////////////////////////////////////////////////////////
                            PLANT BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newPlants A list to receive newly generated plants.
     * @param weather The current weather
     * @param dayState The different state of the day
     */
    public void act(List<Actor> newPlants, Weather weather, DayState dayState)
    {
        if(dayState == DayState.NIGHT) {
            return;
        }
        grow(getMaxAge());
        if(!isAlive()) {
            return;
        }
        if(weather == Weather.RAIN || weather == Weather.SUNNY) {
            multiply(getOptimalBreedingFactor() * getMultiplyProbability(), newPlants);
            return;
        }
        multiply(getMultiplyProbability(), newPlants);
    }

    private void incrementAge(int maxAge)
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }

    private void updateFeedingValue()
    {
        this.feedingValue = age * FEEDING_FACTOR;
    }

    private void grow(int maxAge)
    {
        incrementAge(maxAge);
        updateFeedingValue();
    }

    private void multiply(double spreadProbability, List<Actor> newPlants)
    {
        List<Location> freeLocs = field.getFreeAdjacentTerrain(location);
        for (Location loc : freeLocs) {
            if(rand.nextDouble() < spreadProbability) {
                newPlants.add(createOffspring(field, loc));
            }
        }
    }

    /*///////////////////////////////////////////////////////////////
                          ACCESSOR AND MUTATORS
    //////////////////////////////////////////////////////////////*/

    public boolean isAlive()
    {
        return alive;
    }

    public int getFeedingValue()
    {
        return feedingValue;
    }

    private void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.placePlant(this, newLocation);
    }

    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clearPlant(location);
            location = null;
            field = null;
        }
    }
}
