import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.HashSet;

/**
 * A model of a leopard. Leopards will eat sloths,
 * but will kill sloths and plants.
 * They will move and look for food, age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Leopard extends Animal
{
    // Characteristics shared by all beares (class variables).
    
    // The age at which a leopard can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a leopard can live.
    private static final int MAX_AGE = 55;
    // The likelihood of a leopard breeding.
    private static final double BREEDING_PROBABILITY = 0.145;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The max health of a leopard
    private static final int MAX_HEALTH = 40;
    // Whether the animal is diurnal or nocturnal
    private static final boolean IS_DIURNAL = false;
    
    // Individual characteristics (instance fields).
    
    // The food a leopard will eat.
    private static HashSet<Class> foodSources;
    // The classes a leopard will kill
    private static HashSet<Class> killable;

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Leopard(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        foodSources = new HashSet<>();
        foodSources.add(Sloth.class);
        
        killable = new HashSet<>();
        killable.add(Sloth.class);
        killable.add(Plant.class);
    }
    
    // Accessor and mutator methods
    
    // Accessor and mutator methods
    /**
     * Return whether the leopard is diurnal
     * 
     * @return boolean True if the bea is diurnal, false if nocturnal
     */
    protected boolean getIsDiurnal() {
        return IS_DIURNAL;
    }
    
    /**
     * Returns breeding age of a leopard
     * 
     * @return int of the leopard's breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Returns max age of a leopard
     * 
     * @return int of the leopard's max age.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Returns breeding probability of a leopard
     * 
     * @return double of the leopard's breeding probability
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Returns max litter size of a leopard
     * 
     * @return int of the leopard's max litter size
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Returns whether the target animal is of instance Leopard
     * 
     * @param target A target object that we want to check is of type Leopard
     * @return boolean True if target is of type Leopard
     */
    protected boolean getAnimalClass(Object target)
    {
        return target instanceof Leopard;
    }
    
    /**
     * Returns the HashSet of allowed food for a leopard to eat
     * 
     * @return HashSet<Class> of subclasses that a leopard can eat
     */
    protected HashSet<Class> getFoodSources() {
        return foodSources;
    }
    
    /**
     * Returns the HashSet of allowed classes for a leopard to kill
     * 
     * @return HashSet<Class> of subclasses that a leopard can kill
     */
    protected HashSet<Class> getKillable() {
        return killable;
    }
    
    /**
     * Return the max health of the animal
     * 
     * @return the animal's max health
     */
    protected int getMaxHealth()
    {
        return MAX_HEALTH;
    }
    
    // Functional methods
    
    /**
     * This is what the bear does most of the time: it hunts for
     * monkeys. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newMonkeyes A list to return newly born bears.
     */
    public void act(List<Organism> newLeopards)
    {
        incrementAge();
        incrementHealth();
        if(isAlive()) {
            giveBirth(newLeopards);
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * Creates and returns a new leopard object
     * 
     * @return Organism object of subclass leopard
     */
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Leopard(randomAge, field, location);
    }
}
