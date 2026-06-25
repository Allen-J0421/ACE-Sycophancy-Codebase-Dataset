import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A model of a monkey. Monkeys will eat plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Monkey extends Animal
{
    // Characteristics shared by all monkeys (class variables).

    // The age at which a monkey can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a monkey can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a monkey breeding.
    private static final double BREEDING_PROBABILITY = 0.17;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The max health of a monkey
    private static final int MAX_HEALTH = 10;
    // Whether the animal is diurnal or nocturnal
    private static final boolean IS_DIURNAL = true;
    
    // Individual characteristics (instance fields).
    // The food a monkey will eat.
    private static HashSet<Class> foodSources;
    // The classes a monkey will kill
    private static HashSet<Class> killable;

    /**
     * Create a new monkey. A monkey may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the monkey will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Monkey(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        foodSources = new HashSet<>();
        foodSources.add(Plant.class);
        
        killable = new HashSet<>();
        killable.add(Plant.class);
    }
    
    // Accessor and mutator methods
    /**
     * Return whether the monkey is diurnal
     * 
     * @return boolean True if the monkey is diurnal, false if nocturnal
     */
    protected boolean getIsDiurnal() {
        return IS_DIURNAL;
    }
    
    /**
     * Return the breeding age of the monkey
     * 
     * @return the monkey's breeding age
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the max age of the monkey
     * 
     * @return the monkey's max age
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Return the breeding probability of the monkey
     * 
     * @return the monkey's breeding probability
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Return the max litter size of the monkey
     * 
     * @return the monkey's max litter size
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Return the max health of the monkey
     * 
     * @return the monkey's max health
     */
    protected int getMaxHealth()
    {
        return MAX_HEALTH;
    }
    
    /**
     * Returns whether the target animal is of instance Monkey
     * 
     * @param target A target object that we want to check is of type Monkey
     * @return boolean True if target is of type Monkey
     */
    protected boolean getAnimalClass(Object target)
    {
        return target instanceof Monkey;
    }
    
    /**
     * Returns the HashSet of allowed food for a monkey to eat
     * 
     * @return HashSet<Class> of subclasses that a Monkey can eat
     */
    protected HashSet<Class> getFoodSources() {
        return foodSources;
    }
    
    /**
     * Returns the HashSet of allowed classes for a monkey to kill
     * 
     * @return HashSet<Class> of subclasses that a Monkey can kill
     */
    protected HashSet<Class> getKillable() {
        return killable;
    }
    
    
    // Functional methods
    
    /**
     * This is what the monkey does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newmonkeys A list to return newly born monkeys.
     */
    public void act(List<Organism> newmonkeys)
    {
        incrementAge();
        incrementHealth();
        if(isAlive()) {
            giveBirth(newmonkeys);            
            // Try to move into a free location.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }
    
    /**
     * Creates a new Animal object for a newborn monkey
     * 
     * @return an Animal object for a newborn monkey
     */
    protected Animal createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Monkey(randomAge, field, location);
    }
}
