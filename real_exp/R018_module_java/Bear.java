import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A simple model of a bear.
 * Bears age, move, breed, spread disease, sleep, eat monkeys and carp and die.
 * In the process, they may adopt different behaviours.
 *
 * @version 15/03/2022
 */
public class Bear extends Predator
{
    // The age at which a bear can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a bear can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a bear breeding.
    private static double BREEDING_PROBABILITY = 0.25;
    // The maximum number of births a bear can have.
    private static final int MAX_LITTER_SIZE = 4;
    // The food values of a single monkey and carp. In effect, this is the
    // number of steps a bear can go before it has to eat again.
    private static final int MONKEY_FOOD_VALUE = 7;
    private static final int CARP_FOOD_VALUE = 7;

    /**
     * Create a new bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected Bear(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Returns the food value of the monkey or carp. 
     * If the parameter is neither of these prey, then 
     * return 0 as this means no food is found.
     * @param animal An animal instance.
     * @return the food value for a monkey, carp or 0.
     */
    protected int getFoodValue(Animal animal){
        if(animal instanceof Carp){
            return CARP_FOOD_VALUE;
        }
        if(animal instanceof Monkey){
            return MONKEY_FOOD_VALUE;
        }
        return 0;
    }
    
    /**
     * Bears do not eat plants.
     * @param plant A plant instance.
     * @return 0.
     */
    protected int getFoodValue(Plant plant){
        return 0;
    }
    
    /**
     * Returns true if the parameter is food for the bear.
     * @param animal An animal instance.
     * @return true/false.
     */
    protected boolean isFood(Animal animal){
        return (animal instanceof Carp) || (animal instanceof Monkey);
    }
    
    /**
     * Bears do not eat plants.
     * @param plant A plant instance.
     * @return false.
     */
    protected boolean isFood(Plant plant){
        return false;
    }
    
    /**
     * Returns the bear's food level.
     * @return the bear's food level.
     */
    protected int getFoodLevel(){
        return Bear.MONKEY_FOOD_VALUE;
    }  
    
    /**
     * Returns the bear's maximum age before it dies.
     * @return the bear's maximum age before it dies.
     */
    protected int getMaxAge(){
        return Bear.MAX_AGE;
    }
    
    /**
     * Returns the bear's maximum possible litter size.
     * @return the bear's maximum possible litter size.
     */
    protected int getMaxLitterSize(){
        return Bear.MAX_LITTER_SIZE;
    }
    
    /**
     * Returns the bear's probability of breeding successfully.
     * @return the bear's probability of breeding successfully.
     */
    protected double getBreedingProbability(){
        return Bear.BREEDING_PROBABILITY;
    }
    
    /**
     * Gives the bear a new breeding probability.
     * @param newBreedingProbability The bear's probability of breeding.
     */
    protected void setBreedingProbability(double newBreedingProbability){
        BREEDING_PROBABILITY = newBreedingProbability;
    }
    
    /**
     * Returns the bear's minimum breeding age.
     * @return the bear's minimum breeding age.
     */
    protected int getBreedingAge(){
        return Bear.BREEDING_AGE;
    }
    
    /**
     * Check if the bear's is asleep or not.
     * @return The bear's sleeping status.
     */
    protected boolean isAsleep()
    {
        int minutes = getClock();
        boolean asleep = false;
        if (minutes >= 720 && minutes <= 780) { // Check if the time is between 12:00 and 13:00.
            asleep = true;
        }
        return asleep;
    }
    
    /**
     * Returns a new Bear instance.
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @return a new Bear instance.
     */
    protected Organism getNewOrganism(boolean randomAge, Field field, Location loc){
        return new Bear(randomAge, field, loc);
    }
}
