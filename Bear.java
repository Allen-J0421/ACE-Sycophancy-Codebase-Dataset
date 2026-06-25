import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A model of a bear. Bears will eat monkeys,
 * but will kill monkeys and plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Bear extends Animal implements Infectable
{
    // Characteristics shared by all bears (class variables).
    
    // The age at which a bear can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a bear can live.
    private static final int MAX_AGE = 70;
    // The likelihood of a bear breeding.
    private static final double BREEDING_PROBABILITY = 0.125;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The max health of a bear
    private static final int MAX_HEALTH = 40;
    // Whether the animal is diurnal or nocturnal
    private static final boolean IS_DIURNAL = true;
    
    // Individual characteristics (instance fields).
    
    // The food a bear will eat.
    private static HashSet<Class> foodSources;
    // The classes a bear will kill
    private static HashSet<Class> killable;

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        foodSources = new HashSet<>();
        foodSources.add(Monkey.class);
        
        killable = new HashSet<>();
        killable.add(Monkey.class);
        killable.add(Plant.class);
    }
    
    // Accessor and mutator methods
    
    // Accessor and mutator methods
    /**
     * Return whether the bear is diurnal
     * 
     * @return boolean True if the bea is diurnal, false if nocturnal
     */
    protected boolean getIsDiurnal() {
        return IS_DIURNAL;
    }
    
    /**
     * Returns breeding age of a bear
     * 
     * @return int of the bear's breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Returns max age of a bear
     * 
     * @return int of the bear's max age.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Returns breeding probability of a bear
     * 
     * @return double of the bear's breeding probability
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Returns max litter size of a bear
     * 
     * @return int of the bear's max litter size
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Returns whether the target animal is of instance Bear
     * 
     * @param target A target object that we want to check is of type Bear
     * @return boolean True if target is of type Bear
     */
    protected boolean getAnimalClass(Object target)
    {
        return target instanceof Bear;
    }
    
    /**
     * Returns the ArrayList of allowed food for a bear to eat
     * 
     * @return ArrayList<Class> of subclasses that a bear can eat
     */
    protected HashSet<Class> getFoodSources() {
        return foodSources;
    }
    
    /**
     * Returns the HashSet of allowed classes for a bear to kill
     * 
     * @return HashSet<Class> of subclasses that a bear can kill
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
    public void act(List<Organism> newBears)
    {
        incrementAge();
        incrementHealth();
        if (isAlive()) {
            illness();
        }
        if(isAlive()) {
            spreadDisease();
            giveBirth(newBears);
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
     * Decides whether this animal will infect another eligible animal.
     */
    public void spreadDisease() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if (object != null) {
                Organism organism = (Organism) object;
                if (organism instanceof Infectable && organism.isAlive() && getRand().nextDouble() <= SPREAD_PROBABILITY ) {
                    Animal target = (Animal) organism;
                    target.infect();
                }
            }
        }
    }
    
    /**
     * Creates and returns a new bear object
     * 
     * @return Organism object of subclass bear
     */
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Bear(randomAge, field, location);
    }
    
    /**
     * Makes the bear take extra damage if it is infected
     */
    public void illness() {
        if (getIsInfected()) {
            incrementHealth();
        }
    }
}
