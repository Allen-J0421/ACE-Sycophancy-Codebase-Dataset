import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

/**
 * A simple model of a Gazelle.
 * Gazelles age, move,eat grass, breed, and die.
 *
 * @version 2022.03.01
 */
public class Gazelle extends Animal
{
    private static final AnimalConfig CONFIG = new AnimalConfig(
        2,           // breedingAge
        80,          // maxAge
        0.8900995,   // breedingProbability
        7,           // maxLitterSize
        2,           // maxTimeUntilBreedingAgain
        28.0,        // maxFoodLevel
        1.0, 1.0, 1.0, // sunny / rainy / foggy finding-food probability
        14,          // initialFoodBase  (grass food value; always pre-set for prey)
        14,          // initialFoodCap   (same value: grass food value)
        75.0         // growthDivisor
    );

    private HashMap<Actor, Integer> food;

    /**
     * Create a new Gazelle. A Gazelle may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Gazelle will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Gazelle(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        food = new HashMap<>();
        initialise(randomAge, CONFIG.initialFoodBase, CONFIG.initialFoodCap, CONFIG.growthDivisor);
        addFood(field);
    }

    /**
     * This is what the Gazelle does most of the time: it finds
     * grass to eat. In the process, it might breed, die of hunger,
     * die of disease or die of old age.
     *
     * @param newGazelles A list to return newly born Gazelles.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newGazelles, Simulator simulator)
    {
        setGrowthLevel(0.012);
        if(simulator.isDay()){
            incrementAge(simulator.getSteps());
            incrementHunger();
            if(isActive()) {
                giveBirth(newGazelles);
                super.act(newGazelles,simulator);
            }
        }else{
            //space for potential night activities
        }
    }

    protected int getMaxLitterSize()             { return CONFIG.maxLitterSize; }
    protected double getBreedingProbability()     { return CONFIG.breedingProbability; }
    protected int getBreedingAge()                { return CONFIG.breedingAge; }
    protected int getMaxAge()                     { return CONFIG.maxAge; }

    /**
     * Creates a new Gazelle offspring at the given location.
     * @return A new Gazelle.
     */
    protected Animal reproduce(Field field, Location loc){
        return new Gazelle(false, field, loc);
    }

    /**
     * Adds the food the gazelle eats & the corresponding food value to a hashMap.
     * @param field The field the gazelle is in.
     */
    private void addFood(Field field){
        Location tempLocation = new Location(0,0);
        Plants grass = new Grass(true,field,tempLocation);
        food.put(grass, CONFIG.initialFoodCap);
        grass.setDead();
    }

    protected HashMap<Actor, Integer> getFood()   { return food; }
    protected double getMaxFoodLevel()             { return CONFIG.maxFoodLevel; }
    protected int getMaxTimeUntilBreedingAgain()   { return CONFIG.maxTimeUntilBreedingAgain; }

    protected double getFindingFoodProbability(Weather weather){
        switch(weather){
            case SUNNY: return CONFIG.sunnyFoodProbability;
            case RAINY: return CONFIG.rainyFoodProbability;
            case FOGGY: return CONFIG.foggyFoodProbability;
            default:    return getRandom().nextDouble();
        }
    }
}
