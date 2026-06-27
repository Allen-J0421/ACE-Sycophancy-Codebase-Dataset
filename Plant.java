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
    protected interface PlantFactory
    {
        Plant create(Time time, Field field, Location location, Set<Disease> parentDiseases);
    }

    protected static class PlantProfile
    {
        private final String name;
        private final int breedingAge;
        private final int maxAge;
        private final double breedingProbability;
        private final int maxLitterSize;
        private final boolean canGoLand;
        private final boolean canGoWater;
        private final PlantFactory factory;

        protected PlantProfile(String name, int breedingAge, int maxAge,
                               double breedingProbability, int maxLitterSize,
                               boolean canGoLand, boolean canGoWater, PlantFactory factory)
        {
            this.name = name;
            this.breedingAge = breedingAge;
            this.maxAge = maxAge;
            this.breedingProbability = breedingProbability;
            this.maxLitterSize = maxLitterSize;
            this.canGoLand = canGoLand;
            this.canGoWater = canGoWater;
            this.factory = factory;
        }
    }

    protected double waterLevel;
    protected double sunLightLevel;
    private static final double maxWaterLevel = 2.5;
    private final PlantProfile profile;
    private final Random rand;

    /**
     * Create a new plant at location in field with time as well.
     * With a set that will add any disease that it catches
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param time The time in the simulation
     */
    protected Plant(Time time, Field field, Location location, PlantProfile profile, Random rand)
    {
        this(time, field, location, profile, rand, null);
    }

    /**
     * Create a new born plant with inherited diseases.
     *
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param profile The species profile for this plant.
     * @param rand The shared random generator for this species.
     * @param parentDiseases The diseases inherited from the parent.
     */
    protected Plant(Time time, Field field, Location location, PlantProfile profile,
                    Random rand, Set<Disease> parentDiseases)
    {
        super(time, field, location);
        this.profile = profile;
        this.rand = rand;
        canGoLand = profile.canGoLand;
        canGoWater = profile.canGoWater;
        waterLevel = 1.5;
        sunLightLevel = 1.5;

        if(parentDiseases == null) {
            age = rand.nextInt(profile.maxAge);
            addStartingDiseases(profile.name, setDiseases, rand);
        }
        else {
            age = 0;
            inheritBirthDiseases(setDiseases, parentDiseases);
        }
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
            sunLightLevel += getField().getWeatherAttributeValueAt(WeatherAttribute.BRIGHTNESS, getLocation());
            if (waterLevel + getField().getWeatherAttributeValueAt(WeatherAttribute.DAMPNESS, getLocation()) > maxWaterLevel){
                waterLevel = maxWaterLevel;
            }
            else {
                waterLevel += getField().getWeatherAttributeValueAt(WeatherAttribute.DAMPNESS, getLocation());
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
    public final int breed()
    {
        return calculateBreedingCount(profile.breedingAge, profile.breedingProbability,
                                      profile.maxLitterSize, rand);
    }

    /**
     * Creates a new plant
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new plant created
     */
    protected final Plant birth(Location loc, Set<Disease> parentDiseases)
    {
        Plant young = profile.factory.create(getTime(), getField(), loc, parentDiseases);
        young.placeInField();
        return young;
    }

    /**
     * Returns the actors Name.
     * @return The actor name.
     */
    protected final String getActorName()
    {
        return profile.name;
    }

    /**
     * Returns the actors max age.
     * @return The max age.
     */
    protected final int getMaxAge()
    {
        return profile.maxAge;
    }
}
