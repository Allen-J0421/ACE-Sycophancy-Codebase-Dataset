import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * A simple model of a carp.
 * Carp age, move, breed, spread disease, eat algae and die.
 * In the process, they may adopt different behaviours.
 *
 * @version 15/03/2022
 */
public class Carp extends Prey
{
    // The age at which a carp can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a carp can live.
    private static final int MAX_AGE = 25;
    // The likelihood of a carp breeding.
    private static double BREEDING_PROBABILITY = 0.65;
    // The maximum number of births a carp can have.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single algae. In effect, this is the
    // number of steps a carp can go before it has to eat again.
    private static final int ALGAE_FOOD_VALUE = 6;
    
    /**
     * Create a new carp. A carp can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the carp will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected Carp(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Returns the food value of the algae.
     * If the parameter is not an instance of algae, then 
     * return 0 as this means no food is found.
     * @param plant A plant instance.
     * @return the food value for an algae or 0.
     */
    protected int getFoodValue(Plant plant){
        if(plant instanceof Algae){
            return ALGAE_FOOD_VALUE;
        }
        return 0;
    }

    /**
     * Carp do not eat animals.
     * @param animal An animal instance.
     * @return 0.
     */
    protected int getFoodValue(Animal animal){
        return 0;
    }
    
    /**
     * Returns true if the parameter is food for the carp.
     * @param plant A plant instance.
     * @return true/false.
     */
    protected boolean isFood(Plant plant){
        return (plant instanceof Algae);
    }
    
    /**
     * Carp do not eat animals.
     * @param animal An animal instance.
     * @return false.
     */
    protected boolean isFood(Animal animal){
        return false;
    }
    
    /**
     * Returns the carp's food level.
     * @return the carp's food level.
     */
    protected int getFoodLevel(){
        return Carp.ALGAE_FOOD_VALUE;
    }  
     
    /**
     * Returns the carp's maximum age before it dies.
     * @return the carp's maximum age before it dies.
     */
    protected int getMaxAge(){
        return Carp.MAX_AGE;
    }
    
    /**
     * Returns the carp's maximum possible litter size.
     * @return the carp's maximum possible litter size.
     */
    protected int getMaxLitterSize(){
        return Carp.MAX_LITTER_SIZE;
    }
    
    /**
     * Returns the carp's probability of breeding successfully.
     * @return the carp's probability of breeding successfully.
     */
    protected double getBreedingProbability(){
        return Carp.BREEDING_PROBABILITY;
    }
    
    /**
     * Gives the carp a new breeding probability.
     * @param newBreedingProbability The carp's probability of breeding.
     */
    protected void setBreedingProbability(double newBreedingProbability){
        BREEDING_PROBABILITY = newBreedingProbability;
    }
    
    /**
     * Returns the carp's minimum breeding age.
     * @return the carp's minimum breeding age.
     */    
    protected int getBreedingAge(){
        return Carp.BREEDING_AGE;
    }
    
    /**
     * Returns a new Carp instance.
     * @param randomAge If true, the carp will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @return a new Carp instance.
     */
    protected Organism getNewOrganism(boolean randomAge, Field field, Location loc){
        return new Carp(randomAge, field, loc);
    }
}
