import java.util.*;

/**
 * This class models grass.
 * Grass can germinate, grow and get eaten by animals.
 *
 * @version 2022.03.02
 */
public class Grass extends Plant
{
    private static final PlantStats STATS = new PlantStats(3, 2);

    @Override
    protected PlantStats getStats() { return STATS; }

    private int foodValue;
    private static double GERMINATION_RATE = 0.1;

    @Override
    protected int FOOD_VALUE() { return foodValue; }

    /**
     * Creates grass with a random stage of growth.
     * Food value starts equal to the initial growth stage.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(Field field, Location location)
    {
        super(field, location);
        this.foodValue = getStageOfGrowth();
    }

    /**
     * Make the grass act.
     * The germination rate is affected by weather; growth advances every stepsPerStage steps.
     * @param newGrass To store the newly germinated grass.
     * @param environment The environment that the grass resides in.
     */
    public void act(List<Actor> newGrass, Environment environment)
    {
        if (environment.getWeather().getCurrentWeather() == WeatherType.SUNNY) {
            changeGerminationRate(0.15);
        } else {
            changeGerminationRate(0.1);
        }

        if (environment.getTime().getStep() % getStats().getStepsPerStage() == 0) {
            incrementGrowth();
        }

        if (isAlive() && environment.getTime().isDay() && rand.nextDouble() <= GERMINATION_RATE
                && environment.getWeather().getCurrentWeather() != WeatherType.CLOUDY) {
            germinate(newGrass);
        }
    }

    /**
     * Spread to all free adjacent locations.
     * @param newGrass A list to return newly created grass.
     */
    protected void germinate(List<Actor> newGrass)
    {
        Field field = getField();
        for (Location loc : field.getFreeAdjacentLocations(getLocation())) {
            newGrass.add(new Grass(field, loc));
        }
    }

    /**
     * Change the germination rate of grass.
     * @param newRate The new germination rate.
     */
    public void changeGerminationRate(double newRate)
    {
        if (isAlive()) {
            GERMINATION_RATE = newRate;
        }
    }

    /**
     * Advance the growth stage and increase food value accordingly.
     * @return true if the grass grew; false if already at maximum stage.
     */
    @Override
    public boolean incrementGrowth()
    {
        if (super.incrementGrowth()) {
            foodValue++;
            return true;
        }
        return false;
    }
}
