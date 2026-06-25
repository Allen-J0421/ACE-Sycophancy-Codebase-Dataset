import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * A simple model of a possum.
 * Possums age, move, breed, spread disease, eat leaves and die.
 * In the process, they may adopt different behaviours.
 *
 * @version 15/03/2022
 */
public class Possum extends Prey
{
    // The age at which a possum can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a possum can live.
    private static final int MAX_AGE = 30;
    // The likelihood of a possum breeding.
    private static double BREEDING_PROBABILITY = 0.35;
    // The maximum number of births a possum can have.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of leaves. In effect, this is the
    // number of steps a possum can go before it has to eat again.
    private static final int LEAVES_FOOD_VALUE = 7;

    /**
     * Create a new possum. A possum may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the possum will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected Possum(boolean randomAge, Field field, Location location)
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
     * Possums do not eat animals.
     * @param animal An animal instance.
     * @return 0.
     */
    protected int getFoodValue(Animal animal){
        return 0;
    }  
    
    /**
     * Returns true if the parameter is food for the possum.
     * @param plant A plant instance.
     * @return true/false.
     */
    protected boolean isFood(Plant plant){
        return (plant instanceof Leaves);
    }
    
    /**
     * Possums do not eat animals.
     * @param animal An animal instance.
     * @return false.
     */
    protected boolean isFood(Animal animal){
        return false;
    }
    
    /**
     * Returns the possum's food level.
     * @return the possum's food level.
     */
    protected int getFoodLevel(){
        return Possum.LEAVES_FOOD_VALUE;
    }  
     
    /**
     * Returns the possum's maximum age before it dies.
     * @return the possum's maximum age before it dies.
     */
    protected int getMaxAge(){
        return Possum.MAX_AGE;
    }
    
    /**
     * Returns the possum's maximum possible litter size.
     * @return the possum's maximum possible litter size.
     */
    protected int getMaxLitterSize(){
        return Possum.MAX_LITTER_SIZE;
    }
    
    /**
     * Returns the possum's probability of breeding successfully.
     * @return the possum's probability of breeding successfully.
     */
    protected double getBreedingProbability(){
        return Possum.BREEDING_PROBABILITY;
    }
    
    /**
     * Gives the possum a new breeding probability.
     * @param newBreedingProbability The possum's probability of breeding.
     */
    protected void setBreedingProbability(double newBreedingProbability){
        BREEDING_PROBABILITY = newBreedingProbability;
    }
    
    /**
     * Returns the possum's minimum breeding age.
     * @return the possum's minimum breeding age.
     */  
    protected int getBreedingAge(){
        return Possum.BREEDING_AGE;
    }
    
    /**
     * Returns a new Possum instance.
     * @param randomAge If true, the possum will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @return a new Possum instance.
     */
    protected Organism getNewOrganism(boolean randomAge, Field field, Location loc){
        return new Possum(randomAge, field, loc);
    }
}
