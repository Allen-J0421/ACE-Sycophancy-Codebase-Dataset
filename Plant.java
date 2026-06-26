import java.util.List;

/**
 * Plants can grow, be can be partially eaten, they can be completely eaten and die, plants
 * can spread (the weather effects their ability to spread).
 *
 * @version 26/02/2022
 */
public class Plant extends LivingOrganism implements Ageable, Breedable
{
    // its current health percentage
    private double healthPercentage;
    // The likelihood a plants being able to spread into areas with no plants
    private double spreadProbability;
    // The rate at which plants grow back and increase their health percentage
    private static final double GROWTH_RATE = 0.05;
    // The amount of health percentage which is taken away every time an animal eats a plant
    private static final double PERCENTAGE_EATEN = 0.52;
    // The food value of itself - how much the hunter's food level increases when eating it.
    private static final int FOOD_VALUE = 3;
    
    /**
     * Create a new PLANT at location in field.
     * 
     * @param randomHealthPercentage If true, the plant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomHealthPercentage, Field field, Location location) 
    {
        super(field, location);
        
        foodValue = FOOD_VALUE;
        
        healthPercentage = 1;
        if (randomHealthPercentage) 
        {
            healthPercentage = rand.nextDouble();
            
            if (healthPercentage < 0.1)
            {
                healthPercentage = 0.1;
            }
        }
    }
    
    /**
     * @Override
     * 
     * This is what the plants does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * 
     * @param newPlants A list to return new Plants.
     */
    public void act(List<LivingOrganism> newPlants)
    {
        ageOneStep();
        if(!getTime().isNight()) 
        {
            if(isAlive()) 
            {
                switch (getWeather().getWeather())
                {
                    case Sunny:
                        spreadProbability = 0.2;
                        break;
                    case Rainy:
                        spreadProbability = 0.14;
                        break;
                    case Foggy:
                        spreadProbability = 0.05;
                        break;
                    case Cloudy:
                        spreadProbability = 0.1;
                        break;
                    case Clear:
                        spreadProbability = 0.08;
                        break;
                }    
                
                if (rand.nextDouble() < spreadProbability) 
                {
                    populate(newPlants);
                }
            }
        }
    }
    
    /**
     * When called, the plant gets eaten and loses a
     * pre-defined amount of health.
     */
    protected int beEaten() 
    {
        healthPercentage -= PERCENTAGE_EATEN;
        if (healthPercentage <= 0)
        {
            setDead();
        }
        
        return foodValue;
    }
    
    /**
     * @Override
     *
     * Makes the plant grow to full health.
     */
    protected void incrementAge() 
    {
        ageOneStep();
    }

    /**
     * @Override
     * 
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) 
        {
            getField().clear(location, Plant.class);
            location = null;
        }
    }
    
    /**
     * @Override
     * 
     * Place the plant at the new location in the given field.
     * 
     * @param newLocation The plant's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) 
        {
            getField().clear(location, Plant.class);
        }
        
        location = newLocation;
        getField().place(this, newLocation);
    }
    
    /**
     * @Override
     * 
     * Check whether or not this Plant is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newPlants A list to return newly born Plants.
     */
    protected void populate(List<LivingOrganism> newPlants)
    {
        populateOffspring(newPlants);
    }
    
    /**
     * Used to instantiate baby plantlets
     * 
     * @param free List of free locations to spawn
     * @return Returns a new plant object.
     */
    private Plant createOffspring(Location location) 
    {
        return new Plant(false, getField(), location);
    }

    public void applyAgeProgression()
    {
        healthPercentage += GROWTH_RATE;
        if (healthPercentage > 1.0) 
        {
            healthPercentage = 1.0;
        }
    }

    public Field currentField()
    {
        return getField();
    }

    public Location currentLocation()
    {
        return getLocation();
    }

    public boolean organismIsAlive()
    {
        return isAlive();
    }

    public void markDead()
    {
        setDead();
    }

    public Class<?> getBreedingSpaceType()
    {
        return Plant.class;
    }

    public int determineOffspringCount(int availableLocations)
    {
        return availableLocations;
    }

    public LivingOrganism spawnOffspringAt(Location location)
    {
        return createOffspring(location);
    }
}
