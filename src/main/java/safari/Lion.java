package safari;

import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.HashMap;

/**
 * A simple model of a Lion.
 * Lions age, move, eat gazelle & cheetahs, breed and die.
 *
 * @version 2022.03.01
 */
public class Lion extends Predator
{
    // Characteristics shared by all Lions (class variables).

    // The age at which a Lions can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Lion can live.
    private static final  int MAX_AGE = 1500;
    // The likelihood of a Lion breeding.
    private static final double BREEDING_PROBABILITY = 0.40752995; 
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN =20;
    // The minimum food value of a single prey. In effect, this is the
    // number of steps a Lion can go before it has to eat again.
    //Lions have 2 prey.
    private static final int PREY_GAZELLE_FOOD_VALUE = 24;
    private static final int PREY_CHEETAH_FOOD_VALUE = 25;

    private static final double MAX_FOOD_LEVEL = 50;
    
    // The likelihood of a Lion finding food depending on the weather.

    private static final double SUNNY_FINDING_FOOD_PROBABILITY = 1;
    private static final double RAINY_FINDING_FOOD_PROBABILITY = 0.9;
    private static final double FOGGY_FINDING_FOOD_PROBABILITY = 0.8;

    // Individual characteristics (instance fields).
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
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(PREY_CHEETAH_FOOD_VALUE));
        }
        else {
            setAge( 0);
            setFoodLevel(PREY_CHEETAH_FOOD_VALUE);
        }
        food = new HashMap<>();
        setGrowthLevel(getAge()/100.0);
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

    /**
     * Returns the maximum number of babies the lion can give birth to at once.
     * @return max litter size of the lion.
     */
    protected int getMaxLitterSize(){
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the breeding probability of the lion
     * @return breeding probability of the lion.
     */
    protected double getBreedingProbability(){
        return BREEDING_PROBABILITY;
    }

    /**
     * Returns the current lion occupying the location.
     * @return the current lion.
     */
    protected Animal getAnimal(){
        return this;
    }

    /**
     * Get breeding age of a lion.
     * @return int. Breeding age of the lion;
     */
    protected int getBreedingAge(){
        return BREEDING_AGE;
    }

    /**
     * Gets the max age of a lion.
     * @return int. Max age of the lion.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }

    /**
     * Adds the food the lion eats & the corresponding food value to a hashMap
     * @param field The field the lion is in
     */
    private void addFood(Field field){
        Location tempLocation = new Location(0,0);
        Gazelle gazelle = new Gazelle(true,field,tempLocation );
        food.put(gazelle, PREY_GAZELLE_FOOD_VALUE);
        gazelle.setDead();
        Cheetah cheetah = new Cheetah(true,field,tempLocation );
        food.put(cheetah, PREY_CHEETAH_FOOD_VALUE);
        cheetah.setDead();
    }

    /**
     * Returns the HashMap which contains what food the lion eats and the amount of food each prey gives.
     * @return The HashMap which contains the Actor and an Integer.
     */   
    protected HashMap<Actor, Integer> getFood(){
        return food;
    }

    /**
     * Gets the maximum food level a lion can have.
     * @return Max food level of the lion.
     */
    protected double getMaxFoodLevel(){
        return MAX_FOOD_LEVEL;
    }

    /**
     * Gets the maximum time a lion needs to wait until it can breed again
     * @return Max time before the lion can breed again.
     */
    protected int getMaxTimeUntilBreedingAgain(){
        return MAX_TIME_UNTIL_BREEDING_AGAIN;
    }
    
    /**
     * Gets the probability the lion will find food when it is sunny
     * @return The probability the lion will find food when it is sunny
     */
    protected double getSunnyFindingFoodProbability(){
        return SUNNY_FINDING_FOOD_PROBABILITY;
    }
    
    /**
     * Gets the probability the lion will find food when it is rainy
     * @return The probability the lion will find food when it is rainy
     */
    protected double getRainyFindingFoodProbability(){
        return RAINY_FINDING_FOOD_PROBABILITY;
    }
    
    /**
     * Gets the probability the lion will find food when it is foggy
     * @return The probability the lion will find food when it is foggy
     */
    protected double getFoggyFindingFoodProbability(){
        return FOGGY_FINDING_FOOD_PROBABILITY;
    }
}
