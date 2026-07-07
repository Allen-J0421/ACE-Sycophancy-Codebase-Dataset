
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

/**
 * A simple model of a Zebra.
 * Zebras age, move, eat grass, breed, and die.
 *
 * @version 2022.03.01
 */
public class Zebra extends Animal
{
    private static final AnimalConfig CONFIG = new AnimalConfig(
        2,                      // breedingAge
        160,                    // maxAge
        0.27587999058995,       // breedingProbability
        4,                      // maxLitterSize
        15,                     // maxTimeUntilBreedingAgain
        28.0,                   // maxFoodLevel
        1.0, 1.0, 1.0,          // sunny / rainy / foggy finding-food probability
        19,                     // initialFoodBase  (grass food value; always pre-set for prey)
        19,                     // initialFoodCap   (same value: grass food value)
        67.0                    // growthDivisor
    );

    private HashMap<Actor, Integer> food;

    /**
     * Create a new Zebra. A Zebra may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Zebra will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Zebra(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        food = new HashMap<>();
        initialise(randomAge, CONFIG.initialFoodBase, CONFIG.initialFoodCap, CONFIG.growthDivisor);
        addFood(field);
    }

    /**
     * This is what the Zebra does most of the time: it finds
     * grass to eat. In the process, it might breed, die of hunger,
     * die of disease or die of old age.
     *
     * @param newZebras A list to return newly born Zebras.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newZebra, Simulator simulator)
    {
        setGrowthLevel(0.015);
        if(simulator.isDay()){
            incrementAge(simulator.getSteps());
            incrementHunger();
            if(isActive()) {
                giveBirth(newZebra);
                super.act(newZebra,simulator);
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
     * Creates a new Zebra offspring at the given location.
     * @return A new Zebra.
     */
    protected Animal reproduce(Field field, Location loc){
        return new Zebra(false, field, loc);
    }

    /**
     * Adds the food the zebra eats & the corresponding food value to a hashMap.
     * @param field The field the zebra is in.
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
