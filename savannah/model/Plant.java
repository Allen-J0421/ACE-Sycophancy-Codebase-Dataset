package savannah.model;

import java.util.List;

import savannah.config.SimulationConfig;
import savannah.engine.SimulationContext;

/**
 * Plants can grow, be can be partially eaten, they can be completely eaten and die, plants
 * can spread (the weather effects their ability to spread).
 *
 * @version 26/02/2022
 */
public class Plant extends LivingOrganism
{
    // its current health percentage
    private double healthPercentage;
    // The likelihood a plants being able to spread into areas with no plants
    private double spreadProbability;
    
    /**
     * Create a new PLANT at location in field.
     * 
     * @param randomHealthPercentage If true, the plant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(SimulationContext context, boolean randomHealthPercentage, Location location) 
    {
        super(context, location, SpeciesType.PLANT);

        foodValue = speciesType.plantConfig(getConfig()).foodValue;
        healthPercentage = speciesType.initialPlantHealth(randomHealthPercentage, rand, getConfig());
    }

    /**
     * Create a new PLANT at location in field.
     * 
     * @param randomHealthPercentage If true, the plant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param config Shared simulation configuration.
     */
    public Plant(boolean randomHealthPercentage, Field field, Location location) 
    {
        this(new SimulationContext(field, SimulationConfig.DEFAULT), randomHealthPercentage, location);
    }

    /**
     * Create a new PLANT at location in field.
     * 
     * @param randomHealthPercentage If true, the plant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param config Shared simulation configuration.
     */
    public Plant(boolean randomHealthPercentage, Field field, Location location, SimulationConfig config) 
    {
        this(new SimulationContext(field, config), randomHealthPercentage, location);
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
        incrementAge();
        if(!Time.isNight()) 
        {
            if(isAlive()) 
            {
                spreadProbability = speciesType.getPlantSpreadProbability(Weather.getWeather(), getConfig());
                
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
        healthPercentage -= speciesType.plantConfig(getConfig()).percentageEaten;
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
        healthPercentage += speciesType.plantConfig(getConfig()).growthRate;
        if (healthPercentage > 1.0) 
        {
            healthPercentage = 1.0;
        }
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
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation(), Plant.class);
        
        // New plants are spread into adjacent locations.
        for(int b = 0; free.size() > 0; b++) 
        {
            newPlants.add(createOffspring(free));
        }
    }
    
    /**
     * Used to instantiate baby plantlets
     * 
     * @param free List of free locations to spawn
     * @return Returns a new plant object.
     */
    private Plant createOffspring(List<Location> free) 
    {
        Location loc = free.remove(0);
        return speciesType.createPlant(getContext(), false, loc);
    }
}
