import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A simple model of a panther.
 * Panthers age, move, breed, spread disease, eat monkeys and possums and die.
 * In the process, they may adopt different behaviours.
 *
 * @version 15/03/2022
 */
public class Panther extends Predator
{
    // The age at which a panther can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a panther can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a panther breeding.
    private static double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births a panther can have.
    private static final int MAX_LITTER_SIZE = 4;
    // The food values of a single possum and monkey. In effect, this is the
    // number of steps a panther can go before it has to eat again.
    private static final int POSSUM_FOOD_VALUE = 10;
    private static final int MONKEY_FOOD_VALUE = 10;
    
    /**
     * Create a new panther. A panther can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the panther will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected Panther(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Returns the food value of the possum or monkey. 
     * If the parameter is neither of these prey, then 
     * return 0 as this means no food is found.
     * @param animal An animal instance.
     * @return the food value for a possum, monkey or 0.
     */
    protected int getFoodValue(Animal animal){
        if(animal instanceof Possum){
            return POSSUM_FOOD_VALUE;
        }
        if(animal instanceof Monkey){
            return MONKEY_FOOD_VALUE;
        }
        return 0;
    }
    
    /**
     * Panthers do not eat plants.
     * @param plant A plant instance.
     * @return 0.
     */
    protected int getFoodValue(Plant plant){
        return 0;
    }
    
    /**
     * Returns true if the parameter is food for the panther.
     * @param animal An animal instance.
     * @return true/false.
     */
    protected boolean isFood(Animal animal){
        return (animal instanceof Possum) || (animal instanceof Monkey);
    }
    
    /**
     * Panthers do not eat plants.
     * @param plant A plant instance.
     * @return false.
     */
    protected boolean isFood(Plant plant){
        return false;
    }
    
    /**
     * Returns the panther's food level.
     * @return the panther's food level.
     */
    protected int getFoodLevel(){
        return Panther.MONKEY_FOOD_VALUE;
    }
    
    /**
     * Returns the panther's maximum age before it dies.
     * @return the panther's maximum age before it dies.
     */
    protected int getMaxAge(){
        return Panther.MAX_AGE;
    }
    
    /**
     * Returns the panther's maximum possible litter size.
     * @return the panther's maximum possible litter size.
     */
    protected int getMaxLitterSize(){
        return Panther.MAX_LITTER_SIZE;
    }
    
    /**
     * Returns the panther's probability of breeding successfully.
     * @return the panther's probability of breeding successfully.
     */
    protected double getBreedingProbability(){
        return Panther.BREEDING_PROBABILITY;
    }
    
    /**
     * Gives the panther a new breeding probability.
     * @param newBreedingProbability The panther's probability of breeding.
     */
    protected void setBreedingProbability(double newBreedingProbability){
        BREEDING_PROBABILITY = newBreedingProbability;
    }   
    
    /**
     * Returns the panther's minimum breeding age.
     * @return the panther's minimum breeding age.
     */
    protected int getBreedingAge() {
        return Panther.BREEDING_AGE;
    }

    /**
     * Panthers do not sleep.
     * @return false.
     */
    protected boolean isAsleep()
    {
        return false;
    }
    
    /**
     * Returns a new Panther instance.
     * @param randomAge If true, the panther will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @return a new Panther instance.
     */
    protected Organism getNewOrganism(boolean randomAge, Field field, Location loc){
        return new Panther(randomAge, field, loc);
    }
}
