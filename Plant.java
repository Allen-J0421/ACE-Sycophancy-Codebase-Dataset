import java.util.List;
import java.util.Random;
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
     * Returns how many plants are produced during birth.
     * Uses the abstract breeding parameters defined by each subclass.
     * @return The number of births (may be zero).
     */
    public int breed()
    {
        Random rand = Randomizer.getRandom();
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            return rand.nextInt(getMaxLitterSize()) + 1;
        }
        return 0;
    }

    private boolean canBreed()
    {
        return age >= getBreedingAge();
    }

    abstract protected int getBreedingAge();
    abstract protected double getBreedingProbability();
    abstract protected int getMaxLitterSize();

    /**
     * Creates a new plant
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new plant created
     */
    abstract protected Plant birth(Location loc,Set<Disease>... parentDiseases);
}