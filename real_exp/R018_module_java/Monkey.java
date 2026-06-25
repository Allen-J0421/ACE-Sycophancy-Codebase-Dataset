import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * A simple model of a monkey.
 * Monkeys age, move, breed, spread disease, eat leaves and die.
 * In the process, they may adopt different behaviours.
 *
 * @version 15/03/2022
 */
public class Monkey extends Prey
{
    // The age at which a monkey can start to breed.
    private static final int BREEDING_AGE = 7;
    // The age to which a monkey can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a monkey breeding.
    private static double BREEDING_PROBABILITY = 0.6;
    // The maximum number of births a monkey can have.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of leaves. In effect, this is the
    // number of steps a monkey can go before it has to eat again.
    private static final int LEAVES_FOOD_VALUE = 7;
    
    /**
     * Create a new monkey. A monkey may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the monkey will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected Monkey(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Returns the food value of the leaves.
     * If the parameter is not an instance of leaves, then 
     * return 0 as this means no food is found.
     * @param plant A plant instance.
     * @return the food value for leaves or 0.
     */
    protected int getFoodValue(Plant plant){
        if(plant instanceof Leaves){
            return LEAVES_FOOD_VALUE;
        }
        return 0;
    }
    
    /**
     * Monkeys do not eat animals.
     * @param animal An animal instance.
     * @return 0.
     */
    protected int getFoodValue(Animal animal){
        return 0;
    }
    
    /**
     * Returns true if the parameter is food for the monkey.
     * @param plant A plant instance.
     * @return true/false.
     */
    protected boolean isFood(Plant plant){
        return (plant instanceof Leaves);
    }
    
    /**
     * Monkeys do not eat animals.
     * @param animal An animal instance.
     * @return false.
     */
    protected boolean isFood(Animal animal){
        return false;
    }
    
    /**
     * Returns the monkey's food level.
     * @return the monkey's food level.
     */
    protected int getFoodLevel(){
        return Monkey.LEAVES_FOOD_VALUE;
    }  
    
    /**
     * Returns the monkey's maximum age before it dies.
     * @return the monkey's maximum age before it dies.
     */
    protected int getMaxAge(){
        return Monkey.MAX_AGE;
    }
    
    /**
     * Returns the monkey's maximum possible litter size.
     * @return the monkey's maximum possible litter size.
     */
    protected int getMaxLitterSize(){
        return Monkey.MAX_LITTER_SIZE;
    }
    
    /**
     * Returns the monkey's probability of breeding successfully.
     * @return the monkey's probability of breeding successfully.
     */
    protected double getBreedingProbability(){
        return Monkey.BREEDING_PROBABILITY;
    }
    
    /**
     * Gives the monkey a new breeding probability.
     * @param newBreedingProbability The monkey's probability of breeding.
     */
    protected void setBreedingProbability(double newBreedingProbability){
        BREEDING_PROBABILITY = newBreedingProbability;
    }
    
    /**
     * Returns the monkey's minimum breeding age.
     * @return the monkey's minimum breeding age.
     */  
    protected int getBreedingAge(){
        return Monkey.BREEDING_AGE;
    }
    
    /**
     * Returns a new Monkey instance.
     * @param randomAge If true, the monkey will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @return a new Monkey instance.
     */
    protected Organism getNewOrganism(boolean randomAge, Field field, Location loc){
        return new Monkey(randomAge, field, loc);
    }
}
