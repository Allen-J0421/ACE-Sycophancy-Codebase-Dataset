import java.util.List;

/**
 * This class models grass.
 * Grass can germinate,grow and get eaten by animals.
 * @version 2022.03.02
 */
public class Grass extends Plant
{
    private static int FOOD_VALUE;
    private static double GERMINATION_RATE = 0.1;

    /**
     * Returns the food value of the grass.
     * @return int The food value of the grass.
     */
    @Override
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    /**
     * Creates grass with a random stage of growth.
     * However, the maximum number of stages is fixed at 3,
     * and the growth rate is fixed at 2 steps per stage.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(Field field, Location location)
    {
        super(field, location);

        NUMBER_OF_STAGES = 3;
        STEPS_PER_STAGE = 2;
        STAGE_OF_GROWTH = rand.nextInt(NUMBER_OF_STAGES);
        FOOD_VALUE = STAGE_OF_GROWTH;

    }


    /**
     * Make the grass act.
     * The germination rate of grass is affected by weather.
     * @param newGrass To store the newly germinated grass.
     * @param environment The environment that the grass resides in.
     */
    public void act(List<Actor> newGrass, Environment environment)
    {
        if (environment.getWeather().getCurrentWeather() == WeatherType.SUNNY){
            changeGerminationRate(0.15);
        }

        else if (environment.getWeather().getCurrentWeather() != WeatherType.SUNNY){
            changeGerminationRate(0.1);
        }

        if(isAlive() && environment.getTime().isDay() && rand.nextDouble() <= GERMINATION_RATE && (environment.getWeather().getCurrentWeather() != (WeatherType.CLOUDY))) {
            germinate(newGrass);
        }
    }

    /**
     * Check whether this grass is to germinate at this step.
     * New grass will be made into free adjacent locations.
     * @param newGrass A list to return newly made grass.
     */
    protected void germinate(List<Actor> newGrass)
    {
        // New grass are made into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        for(int b = 0; b < free.size(); b++) {
            Location loc = free.remove(0);
            Grass young = new Grass(field, loc);
            newGrass.add(young);
        }
    }

    /**
     * Change the germination rate of grass.
     * @param newRate The new germination rate.
     */
    public void changeGerminationRate(double newRate)
    {
        if(isAlive()) {
            GERMINATION_RATE = newRate;
        }
    }

    /**
     * Increment the stage of growth of grass
     * Incrementing the growth also increases the food level by 1
     * @return true If the grass grows.
     * @return false If the grass doesn't grow.
     */
    public boolean incrementGrowth()
    {
        if(super.incrementGrowth()) {
            FOOD_VALUE++;
            return true;
        }
        return false;
    }



}
