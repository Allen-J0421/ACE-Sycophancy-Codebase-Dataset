import java.util.List;
import java.util.Set;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 27.02.22
 */
public abstract class Plant extends Actor
{

    protected double waterLevel;
    protected double sunLightLevel;
    private static final double maxWaterLevel = 2.5;

    /**
     * Create a new plant at location in field with time as well.
     * With a set that will add any disease that it catches
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param time The time in the simulation
     */
    public Plant(Time time, Field field, Location location)
    {
        super(time, field, location);
        waterLevel = 1.5;
        sunLightLevel = 1.5;
    }

    /**
     * Initialize the shared state for a newborn plant.
     * @param canGoLand Whether the plant can move on land.
     * @param canGoWater Whether the plant can move on water.
     * @param parentDiseases The diseases inherited from the parent.
     */
    protected void initializeNewbornState(boolean canGoLand, boolean canGoWater, Set<Disease> parentDiseases)
    {
        this.canGoLand = canGoLand;
        this.canGoWater = canGoWater;
        age = 0;
        inheritBirthDiseases(parentDiseases);
    }

    /**
     * Initialize the shared state for a plant that starts with a random age.
     * @param canGoLand Whether the plant can move on land.
     * @param canGoWater Whether the plant can move on water.
     * @param maxAge The exclusive upper bound for the initial age.
     * @param actorName The actor name used for startup disease seeding.
     */
    protected void initializeRandomStartState(boolean canGoLand, boolean canGoWater, int maxAge, String actorName)
    {
        this.canGoLand = canGoLand;
        this.canGoWater = canGoWater;
        age = Randomizer.getRandom().nextInt(maxAge);
        seedStartingDiseases(actorName);
    }

    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     * Checks weather and updates water and sunlight levels accordingly.
     * @param newPlants A list to receive newly born plants.
     * @param weather The weather condition of the simulation.
     */
    public void act(List<Actor> newPlants, WeatherCond weather)
    {
        super.act(newPlants, weather);
        if (isAlive()){
            waterLevel-=0.5;
            sunLightLevel-= 0.25;
            sunLightLevel += getField().getWeatherAttributeValueAt("brightness", getLocation());
            if (waterLevel +getField().getWeatherAttributeValueAt("dampness", getLocation()) > maxWaterLevel){
                waterLevel = maxWaterLevel;
            }
            else {
                waterLevel += getField().getWeatherAttributeValueAt("dampness", getLocation());
            }
            if (waterLevel < 0 || sunLightLevel <= 0){
                setDead();
            }
            else {
                giveBirth(newPlants);            
            }
        }
    }

    /**
     * Returns how many plants are produced during birth
     * @return The number of plants birthed
     */
    abstract public int breed();

    /**
     * Creates a new plant
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new plant created
     */
    abstract protected Plant birth(Location loc, Set<Disease> parentDiseases);
}
