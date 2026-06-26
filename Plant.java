import java.util.List;
import java.util.Iterator;

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
    @SuppressWarnings("this-escape")
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

        setLocation(location);
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
                switch (Weather.getWeather())
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
        healthPercentage += GROWTH_RATE;
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
            field.clear(location, Plant.class);
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
    @SuppressWarnings("this-escape")
    protected final void setLocation(Location newLocation)
    {
        if(location != null)
        {
            field.clear(location, Plant.class);
        }

        location = newLocation;
        field.place(this, newLocation);
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
        Plant offspring = null;

        offspring = new Plant(false, field, loc);

        return offspring;
    }
}
