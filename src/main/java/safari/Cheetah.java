package safari;

import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.HashMap;
/**
 * A simple model of a Cheetah.
 * Cheetahs age, move, eat zebras, breed and die.
 *
 * @version 2022.03.01
 */
public class Cheetah extends Predator
{
    // Characteristics shared by all Cheetahs (class variables).

    // The age at which a Cheetah can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a Cheetah can live.
    private static final  int MAX_AGE = 1200;
    // The likelihood of a Cheetah breeding.
    private static  double BREEDING_PROBABILITY = 0.4196975694969952;  
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    private static final int MAX_TIME_UNTIL_BREEDING_AGAIN = 11;
    // The minimum food value of a single prey. In effect, this is the
    // number of steps a Cheetah can go before it has to eat again.
    // Cheetahs have 2 prey
    private static final int ZEBRA_FOOD_VALUE = 34; 
    private static final int GAZELLE_FOOD_VALUE = 33;
    private static final double MAX_FOOD_LEVEL = 40;
    
    // The likelihood of a Cheetah finding food depending on the weather.
    private static final double SUNNY_FINDING_FOOD_PROBABILITY = 0.9;
    private static final double RAINY_FINDING_FOOD_PROBABILITY = 0.8;
    private static final double FOGGY_FINDING_FOOD_PROBABILITY = 0.7;

    // Individual characteristics (instance fields).
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
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
            setFoodLevel(getRandom().nextInt(ZEBRA_FOOD_VALUE));
        }
        else {
            setAge(0);
            setFoodLevel(ZEBRA_FOOD_VALUE);
        }
        food = new HashMap<>();
        setGrowthLevel(getAge()/102.0);
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

    /**
     * Returns the maximum number of babies the cheetah can give birth to at once.
     * @return the max litter size of the cheetah.
     */
    protected int getMaxLitterSize(){
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the breeding probability of the cheetah.
     * @return the breeding probability of the cheetah.
     */
    protected double getBreedingProbability(){
        return BREEDING_PROBABILITY;
    }

    /**
     * Returns the current cheetah occupying the location.
     * @return the current cheetah.
     */
    protected Animal getAnimal(){
        return this;
    }

    /**
     * Get breeding age of a cheetah.
     * @return The breeding age of the cheetah.
     */
    protected int getBreedingAge(){
        return BREEDING_AGE;
    }

    /**
     * Gets the max age of a cheetah.
     * @return The max age of the cheetah.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }

    /**
     * Adds the food the cheetah eats & the corresponding food value to a hashMap.
     * @param field The field the cheetah is in.
     */
    private void addFood(Field field){
        Location tempLocation = new Location(0,0);
        Zebra zebra = new Zebra(true,field,tempLocation );
        food.put(zebra, ZEBRA_FOOD_VALUE);
        zebra.setDead();
        //Location tempLocation = new Location(0,0);
        Gazelle gazelle = new Gazelle(true,field,tempLocation );
        food.put(gazelle, GAZELLE_FOOD_VALUE);
        gazelle.setDead();
    }

    /**
     * Returns the HashMap which contains what food the cheetah eats and the amount of food each prey gives.
     * @return The HashMap which contains the Actor and an Integer.
     */   
    protected HashMap<Actor, Integer> getFood(){
        return food;
    }

    /**
     * Gets the maximum food level a cheetah can have.
     * @return Max food level of the cheetah.
     */
    protected double getMaxFoodLevel(){
        return MAX_FOOD_LEVEL;
    }
    
    /**
     * Gets the maximum time a cheetah needs to wait until it can breed again
     * @return Max time before the cheetah can breed again.
     */
    protected int getMaxTimeUntilBreedingAgain(){
        return MAX_TIME_UNTIL_BREEDING_AGAIN;
    }
    
    /**
     * Gets the probability the gazelle will find food when it is sunny
     * @return The probability the gazelle will find food when it is sunny
     */
    protected double getSunnyFindingFoodProbability(){
        return SUNNY_FINDING_FOOD_PROBABILITY;
    }
    
    /**
     * Gets the probability the gazelle will find food when it is rainy
     * @return The probability the gazelle will find food when it is rainy
     */
    protected double getRainyFindingFoodProbability(){
        return RAINY_FINDING_FOOD_PROBABILITY;
    }
    
    /**
     * Gets the probability the cheetah will find food when it is foggy
     * @return The probability the cheetah will find food when it is foggy
     */
    protected double getFoggyFindingFoodProbability(){
        return FOGGY_FINDING_FOOD_PROBABILITY;
    }
}
