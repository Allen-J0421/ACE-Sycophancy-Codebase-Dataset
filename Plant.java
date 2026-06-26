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
    private static final double OPTIMAL_BREEDING_FACTOR = 4.0;
    protected boolean alive;
    protected Location location;
    protected int age;
    protected int health;
    protected int feedingValue; 
    protected Field field;
     
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
     * @param newPlants A list to receive newly generated plants.
     * @param weather The current weather
     * @param dayState The different state of the day
     */
    
    public abstract void act(List<Actor> newPlants, Weather weather, DayState dayState);

    /**
     * Create a new offspring of the current concrete plant type.
     *
     * @param field the field the offspring will inhabit
     * @param location the location for the offspring
     * @return a new plant of the same concrete type
     */
    protected abstract Plant createOffspring(Field field, Location location);
    
    /**
     * Increments the age of the plant.
     * 
     * @param maxAge The maximum age a plant can take.
     */
    private void incrementAge(int maxAge) {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }
    
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
                Plant plant = createOffspring(field, loc);
                newPlants.add(plant);
            }
        }
    }

    /**
     * Apply the common plant growth cycle for daytime plants.
     *
     * @param newPlants the newly generated plants.
     * @param weather the current weather.
     * @param dayState the current day state.
     * @param maxAge the maximum age of the plant.
     * @param spreadProbability the base probability of spreading.
     */
    protected void actWithGrowthCycle(List<Actor> newPlants,
                                      Weather weather,
                                      DayState dayState,
                                      int maxAge,
                                      double spreadProbability)
    {
        if(dayState == DayState.NIGHT) {
            return;
        }
        grow(maxAge);
        if(!isAlive()) {
            return;
        }

        double effectiveProbability = spreadProbability;
        if(weather == Weather.RAIN || weather == Weather.SUNNY) {
            effectiveProbability *= OPTIMAL_BREEDING_FACTOR;
        }
        multiply(effectiveProbability, newPlants);
    }
    
    /*///////////////////////////////////////////////////////////////
                          ACCESSOR AND MUTATORS
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Accessor method to denote whether or not the plant is alive.
     * 
     * @return flag conveying if the plant is alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
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
     * Mutator method for the location of the plant.
     * 
     * @param newLocation the new location to be set.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clearPlant(location);
        }
        location = newLocation;
        field.placePlant(this, newLocation);
    }
    
    /**
     * Sets the plant dead and removes all references to it, as well as its storage.
     */
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
