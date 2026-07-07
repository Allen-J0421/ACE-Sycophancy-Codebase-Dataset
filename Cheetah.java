import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

/**
 * A simple model of a Cheetah.
 * Cheetahs age, move, eat zebras, breed and die.
 *
 * @version 2022.03.01
 */
public class Cheetah extends Predator
{
    private static final AnimalConfig CONFIG = new AnimalConfig(
        2,                    // breedingAge
        1200,                 // maxAge
        0.4196975694969952,   // breedingProbability
        4,                    // maxLitterSize
        11,                   // maxTimeUntilBreedingAgain
        40.0,                 // maxFoodLevel
        0.9, 0.8, 0.7,        // sunny / rainy / foggy finding-food probability
        0,                    // initialFoodBase  (predator: no base offset)
        34,                   // initialFoodCap   (zebra prey food value)
        102.0                 // growthDivisor
    );
    private static final int GAZELLE_FOOD_VALUE = 33;

    private HashMap<Actor, Integer> food;

    /**
     * Create a Cheetah. A Cheetah can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true,the Cheetah will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cheetah(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        food = new HashMap<>();
        initialise(randomAge, CONFIG.initialFoodBase, CONFIG.initialFoodCap, CONFIG.growthDivisor);
        addFood(field);
    }

    /**
     * This is what the Cheetah does most of the time: it hunts for
     * zebras. In the process, it might breed, die of hunger,
     * die of infection or die of old age.
     *
     * @param newCheetahs A list to return newly born Cheetahs.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newCheetahs, Simulator simulator)
    {
        setGrowthLevel(0.012);
        if(simulator.isDay()){
            incrementAge(simulator.getSteps());
            incrementHunger();
            if(isActive()) {
                giveBirth(newCheetahs);
                super.act(newCheetahs,simulator);
            }
        }else{
            //space for potential night activities
        }
    }

    protected int getMaxLitterSize()             { return CONFIG.maxLitterSize; }
    protected double getBreedingProbability()     { return CONFIG.breedingProbability; }

    /**
     * Creates a new Cheetah offspring at the given location.
     * @return A new Cheetah.
     */
    protected Animal reproduce(Field field, Location loc){
        return new Cheetah(false, field, loc);
    }

    protected int getBreedingAge()                { return CONFIG.breedingAge; }
    protected int getMaxAge()                     { return CONFIG.maxAge; }

    /**
     * Adds the food the cheetah eats & the corresponding food value to a hashMap.
     * @param field The field the cheetah is in.
     */
    private void addFood(Field field){
        Location tempLocation = new Location(0,0);
        Zebra zebra = new Zebra(true,field,tempLocation);
        food.put(zebra, CONFIG.initialFoodCap);
        zebra.setDead();
        Gazelle gazelle = new Gazelle(true,field,tempLocation);
        food.put(gazelle, GAZELLE_FOOD_VALUE);
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
