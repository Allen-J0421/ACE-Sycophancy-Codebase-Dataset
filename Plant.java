import java.util.Random;
import java.util.List;
/**
 * A blueprint for how a typical plant behaves. A plant grows according to the weather 
 * conditions and daylight, and feeds more as it grows.
 *
 * @version 1.0
 */
public abstract class Plant extends Organism
{

    /*///////////////////////////////////////////////////////////////
                                  STATE
    //////////////////////////////////////////////////////////////*/

    private static final int FEEDING_FACTOR = 2;
    // Multiplier applied to the spread probability under favourable weather.
    private static final double OPTIMAL_BREEDING_FACTOR = 4.0;
    protected int health;
    protected int feedingValue;

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
    public Plant(boolean randomAge,Field field, Location location, int maxAge) 
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
                            PLANT BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     *
     * This is a template method capturing the life cycle every plant shares
     * during a step: by day it grows, and if still alive it spreads - faster
     * in favourable (rainy or sunny) weather. Species customise the growth
     * ceiling and spread rate through the hooks below.
     *
     * @param newPlants A list to receive newly generated plants.
     * @param weather The current weather
     * @param dayState The different state of the day
     */
    public final void act(List<Actor> newPlants, Weather weather, DayState dayState)
    {
        if(dayState == DayState.NIGHT) {
            return;
        }
        grow(getMaxAge());
        if(!isAlive()) {
            return;
        }
        // Plants spread faster in favourable (rainy or sunny) weather.
        if(weather == Weather.RAIN || weather == Weather.SUNNY) {
            multiply(OPTIMAL_BREEDING_FACTOR * getMultiplyProbability(), newPlants);
            return;
        }
        multiply(getMultiplyProbability(), newPlants);
    }

    /*///////////////////////////////////////////////////////////////
                            TEMPLATE METHOD HOOKS
    //////////////////////////////////////////////////////////////*/

    /**
     * @return the maximum age this species can reach before dying.
     */
    protected abstract int getMaxAge();

    /**
     * @return the per-cell probability that this species spreads to a free
     *         adjacent terrain location each step.
     */
    protected abstract double getMultiplyProbability();
    
    /**
     * Updates how much the plant will feed if it is to be eaten.
     */
    private void updateFeedingValue()
    {
        this.feedingValue = age * FEEDING_FACTOR;
    }
    
    /**
     * Updates the age and how much the plant will feed.
     * 
     * @param maxAge The maximum age a plant can take.
     */
    protected void grow(int maxAge)
    {
        incrementAge(maxAge);
        updateFeedingValue();
    }
    
    /**
     * Imitates the spread/growth of a plant.
     * 
     * @param spreadProbability likelihood a spreading to the next cell.
     * @param newPlants the newly generated plants.
     */
    protected void multiply(double spreadProbability, List<Actor> newPlants)
    {
        List<Location> freeLocs = field.getFreeAdjacentTerrain(location);
        for ( Location loc : freeLocs) {
            if(rand.nextDouble() < spreadProbability) {
                Plant plant = reproduce(field, loc);
                newPlants.add(plant);
            }
        }
    }

    /**
     * Factory method creating a new plant of this plant's own species.
     * Implemented by each concrete species, replacing reflective instantiation.
     *
     * @param field The field the new plant occupies.
     * @param location The new plant's location within the field.
     * @return a newly created plant of the same species.
     */
    protected abstract Plant reproduce(Field field, Location location);
    
    /**
     * Accessor method for the feeding value of the plant.
     * 
     * @return the feeding value of the plant.
     */
    public int getFeedingValue()
    {
        return feedingValue;
    }
    
    /**
     * Plants live on the field's plant terrain grid.
     *
     * @param location Where to place this plant.
     */
    @Override
    protected void placeInField(Location location)
    {
        field.placePlant(this, location);
    }

    /**
     * Remove this plant from the field's plant terrain grid.
     *
     * @param location The location to clear.
     */
    @Override
    protected void clearFromField(Location location)
    {
        field.clearPlant(location);
    }
}
