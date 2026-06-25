import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.HashSet;

/**
 * A model of a hippopotamus. Hippopotamus will eat monkeys and plants,
 * but will kill monkeys, plants and bears.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Hippopotamus extends Animal
{
    // Characteristics shared by all foxes (class variables).
    
    // The age at which a hippopotamus can start to breed.
    private static final int BREEDING_AGE = 25;
    // The age to which a hippopotamus can live.
    private static final int MAX_AGE = 120;
    // The likelihood of a hippopotamus breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The max health of a hippopotamus
    private static final int MAX_HEALTH = 100;
    // Whether the animal is diurnal or nocturnal
    private static final boolean IS_DIURNAL = true;
    
    // Individual characteristics (instance fields).
    
    // The food a hippopotamus will eat.
    private HashSet<Class> foodSources;
    // The classes a hippopotamus will kill
    private static HashSet<Class> killable;

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hippopotamus(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        foodSources = new HashSet<>();
        foodSources.add(Monkey.class);
        foodSources.add(Plant.class);
        
        killable = new HashSet<>();
        killable.add(Monkey.class);
        killable.add(Plant.class);
        killable.add(Bear.class);
    }
    
    // Accessor and mutator methods
    
    // Accessor and mutator methods
    /**
     * Return whether the hippopotamus is diurnal
     * 
     * @return boolean True if the bea is diurnal, false if nocturnal
     */
    protected boolean getIsDiurnal() {
        return IS_DIURNAL;
    }
    
    /**
     * Returns breeding age of a hippopotamus
     * 
     * @return int of the hippopotamus's breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Returns max age of a hippopotamus
     * 
     * @return int of the hippopotamus's max age.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Returns breeding probability of a hippopotamus
     * 
     * @return double of the hippopotamus's breeding probability
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Returns max litter size of a hippopotamus
     * 
     * @return int of the hippopotamus's max litter size
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Returns whether the target animal is of instance Hippopotamus
     * 
     * @param target A target object that we want to check is of type Hippopotamus
     * @return boolean True if target is of type Hippopotamus
     */
    protected boolean getAnimalClass(Object target)
    {
        return target instanceof Hippopotamus;
    }
    
    /**
     * Returns the HashSet of allowed food for a hippopotamus to eat
     * 
     * @return HashSet<Class> of subclasses that a hippopotamus can eat
     */
    protected HashSet<Class> getFoodSources() {
        return foodSources;
    }
    
    /**
     * Returns the HashSet of allowed classes for a hippo to kill
     * 
     * @return HashSet<Class> of subclasses that a hippopotamus can kill
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
     * This is what the fox does most of the time: it hunts for
     * monkeys. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newMonkeyes A list to return newly born foxes.
     */
    public void act(List<Organism> newHippopotamuss)
    {
        incrementAge();
        incrementHealth();
        if(isAlive()) {
            giveBirth(newHippopotamuss);
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
     * Creates and returns a new hippopotamus object
     * 
     * @return Organism object of subclass hippopotamus
     */
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Hippopotamus(randomAge, field, location);
    }
}
