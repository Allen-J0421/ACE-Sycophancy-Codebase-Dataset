import java.util.List;
import java.util.Random;

/**
 * This class models grass.
 * Grass can germinate,grow and get eaten by animals.
 * @version 2022.03.02
 */
public class Grass extends Plant
{
    private static final int MAX_GROWTH_STAGES = 3;
    private static final int DEFAULT_STEPS_PER_STAGE = 2;

    private int foodValue;

    /**
     * Returns the food value of the grass.
     * @return int The food value of the grass.
     */
    protected int getFoodValue()
    {
        return foodValue;
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
        super(field.getRandomProvider(), field, location);

        numberOfStages = MAX_GROWTH_STAGES;
        stepsPerStage = DEFAULT_STEPS_PER_STAGE;
        stageOfGrowth = getRandomProvider().getRandom().nextInt(numberOfStages);
        foodValue = stageOfGrowth;

    }


    /**
     * Make the grass act.
     * The germination rate of grass is affected by weather.
     * @param newGrass To store the newly germinated grass.
     * @param environment The environment that the grass resides in.
     */
    public void act(List<Actor> newGrass, Environment environment)
    {
        Random rand = getRandomProvider().getRandom();
        WeatherService weatherService = environment.getWeatherService();
        if(isAlive()
                && environment.getTime().isDay()
                && weatherService.allowsGrassGermination()
                && rand.nextDouble() <= weatherService.getGrassGerminationRate()) {
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
            Grass young = field.getOrganismFactory().createOffspring(Grass.class, field, loc);
            newGrass.add(young);
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
            foodValue++;
            return true;
        }
        return false;
    }

}
