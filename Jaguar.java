
import java.util.HashMap;
import java.util.List;

/**
 * A simple model of a Jaguar.
 * Jaguars age, move, eat gazelle, and die.
 *
 * @version 2022.03.01
 */
public class Jaguar extends Predator
{
    private static final AnimalConfig CONFIG = new AnimalConfig(
        10,    // breedingAge
        1000,  // maxAge
        0.2,   // breedingProbability
        5,     // maxLitterSize
        6,     // maxTimeUntilBreedingAgain
        39.0,  // maxFoodLevel
        1.0, 0.7, 0.4, // sunny / rainy / foggy finding-food probability
        0,     // initialFoodBase  (predator: no base offset)
        35,    // initialFoodCap   (gazelle prey food value)
        89.0   // growthDivisor
    );

    private HashMap<Actor, Integer> food;

    /**
     * Create a Jaguar. A Jaguar can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Jaguar will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Jaguar(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        food = new HashMap<>();
        initialise(randomAge, CONFIG.initialFoodBase, CONFIG.initialFoodCap, CONFIG.growthDivisor);
        addFood(field);
    }

    /**
     * This is what the Jaguar does most of the time: it finds
     * gazelle to eat. In the process, it might breed, die of hunger,
     * die of disease or die of old age.
     *
     * @param newJaguars A list to return newly born Jaguars.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newJaguars, Simulator simulator)
    {
        setGrowthLevel(0.013);
        if(simulator.isDay()){
            incrementAge(simulator.getSteps());
            incrementHunger();
            if(isActive()) {
                giveBirth(newJaguars);
                super.act(newJaguars,simulator);
            }
        }else{
            //space for potential night activities
        }
    }

    protected int getMaxLitterSize()             { return CONFIG.maxLitterSize; }
    protected double getBreedingProbability()     { return CONFIG.breedingProbability; }

    /**
     * Creates a new Jaguar offspring at the given location.
     * @return A new Jaguar.
     */
    protected Animal reproduce(Field field, Location loc){
        return new Jaguar(false, field, loc);
    }

    protected int getBreedingAge()                { return CONFIG.breedingAge; }
    protected int getMaxAge()                     { return CONFIG.maxAge; }

    /**
     * Adds the food the jaguar eats & the corresponding food value to a hashMap.
     * @param field The field the jaguar is in.
     */
    private void addFood(Field field){
        Location tempLocation = new Location(0,0);
        Gazelle gazelle = new Gazelle(true,field,tempLocation);
        food.put(gazelle, CONFIG.initialFoodCap);
        gazelle.setDead();
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
