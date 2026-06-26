import java.util.List;

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
    public void act(List<Plant> newPlants)
    {
        incrementAge();
        if(!Time.isNight() && isAlive())
        {
            if (rand.nextDouble() < getSpreadProbability())
            {
                populate(newPlants);
            }
        }
    }

    /**
     * Return the current weather's effect on plant spread.
     */
    private double getSpreadProbability()
    {
        switch (Weather.getWeather())
        {
            case Sunny:
                return 0.2;
            case Rainy:
                return 0.14;
            case Foggy:
                return 0.05;
            case Cloudy:
                return 0.1;
            case Clear:
                return 0.08;
            default:
                return 0;
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
    protected void populate(List<Plant> newPlants)
    {
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation(), Plant.class);

        // New plants are spread into adjacent locations.
        while(free.size() > 0)
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

        return new Plant(false, field, loc);
    }
}
