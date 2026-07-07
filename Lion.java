import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

/**
 * A simple model of a Lion.
 * Lions age, move, eat gazelle & cheetahs, breed and die.
 *
 * @version 2022.03.01
 */
public class Lion extends Predator
{
    private static final AnimalConfig CONFIG = new AnimalConfig(
        15,          // breedingAge
        1500,        // maxAge
        0.40752995,  // breedingProbability
        2,           // maxLitterSize
        20,          // maxTimeUntilBreedingAgain
        50.0,        // maxFoodLevel
        1.0, 0.9, 0.8, // sunny / rainy / foggy finding-food probability
        0,           // initialFoodBase  (predator: no base offset)
        25,          // initialFoodCap   (cheetah prey food value)
        100.0        // growthDivisor
    );
    private static final int PREY_GAZELLE_FOOD_VALUE = 24;

    private HashMap<Actor, Integer> food;

    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        food = new HashMap<>();
        initialise(randomAge, CONFIG.initialFoodBase, CONFIG.initialFoodCap, CONFIG.growthDivisor);
        addFood(field);
    }

    /**
     * This is what the Lion does most of the time: it hunts for
     * gazelle & cheetahs. In the process, it might breed, die of hunger,
     * die of disease or die of old age.
     *
     * @param newLions A list to return newly born Lions.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newLions,Simulator simulator)
    {
        setGrowthLevel(0.01);
        if(simulator.isDay()){
            incrementAge(simulator.getSteps());
            incrementHunger();
            if(isActive()) {
                giveBirth(newLions);
                super.act(newLions,simulator);
            }
        }else{
            //space for potential night activities
        }
    }

    protected int getMaxLitterSize()             { return CONFIG.maxLitterSize; }
    protected double getBreedingProbability()     { return CONFIG.breedingProbability; }

    /**
     * Creates a new Lion offspring at the given location.
     * @return A new Lion.
     */
    protected Animal reproduce(Field field, Location loc){
        return new Lion(false, field, loc);
    }

    protected int getBreedingAge()                { return CONFIG.breedingAge; }
    protected int getMaxAge()                     { return CONFIG.maxAge; }

    /**
     * Adds the food the lion eats & the corresponding food value to a hashMap.
     * @param field The field the lion is in.
     */
    private void addFood(Field field){
        Location tempLocation = new Location(0,0);
        Gazelle gazelle = new Gazelle(true,field,tempLocation);
        food.put(gazelle, PREY_GAZELLE_FOOD_VALUE);
        gazelle.setDead();
        Cheetah cheetah = new Cheetah(true,field,tempLocation);
        food.put(cheetah, CONFIG.initialFoodCap);
        cheetah.setDead();
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
