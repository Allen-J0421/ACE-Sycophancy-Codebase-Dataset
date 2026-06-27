import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A class representing shared characteristics of all animals within the simultion.
 * Animals will find food, have a gender and be able to become infected.
 * 
 * @version 2022.03.02
 */
public abstract class Animal extends Organism
{
    // A singleton shared weather object between all organisms and the simulator
    private static final Weather weather = Weather.getWeather();
    // The runtime class used to identify animals of the same species.
    private final Class<? extends Animal> speciesClass;
    // The kinds of organisms this animal can eat.
    private final Set<Class<?>> foodSources;
    // The kinds of organisms this animal can kill.
    private final Set<Class<?>> killable;
    // Gender of the animal
    private boolean isMale;
    // Current health of the animal
    private int currentHealth;
    // Whether the animal is infected
    private boolean isInfected;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomAge, Field field, Location location,
                  Class<? extends Animal> speciesClass,
                  Set<Class<?>> foodSources,
                  Set<Class<?>> killable)
    {
        super(randomAge, field, location);
        this.speciesClass = speciesClass;
        this.foodSources = foodSources;
        this.killable = killable;
        
        if (randomAge) {
            currentHealth = getRand().nextInt(getMaxHealth());
        }
        if (getRand().nextInt(2) == 0) {
            isMale = true;
        }
        else {
            isMale = false;
        }
        
        isInfected = false;
    }
    
    // Abstract methods
    
    /**
     * Abstract method that returns the max health of the animal
     * 
     * @return the animal's max health
     */
    abstract protected int getMaxHealth();

    // Accessor and mutator methods

    /**
     * Create an immutable set of organism classes for species configuration.
     *
     * @param classes The organism classes in the set.
     * @return An immutable set containing the given classes.
     */
    protected static Set<Class<?>> classSet(Class<?>... classes)
    {
        return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(classes)));
    }
    
    /**
     * Returns whether the animal is male
     * 
     * @boolean True if the animal is male, false if animal is female
     */
    protected boolean getGender()
    {
        return isMale;
    }
    
    /**
     * Return the current health of the animal
     * 
     * @return int of the animal's current health
     */
    protected int getCurrentHealth() {
        return currentHealth;
    }
    
    /**
     * Return whether the animal is infected
     * 
     * @return True if the animal is infected, false if not
     */
    protected boolean getIsInfected() {
        return isInfected;
    }
    
    /**
     * Sets the animal to infected state
     */
    protected void infect() {
        isInfected = true;
    }

    /**
     * Return the list of food sources of this animal.
     *
     * @return The set of classes this animal can eat.
     */
    protected Set<Class<?>> getFoodSources()
    {
        return foodSources;
    }

    /**
     * Return the list of classes this animal can kill.
     *
     * @return The set of classes this animal can kill.
     */
    protected Set<Class<?>> getKillable()
    {
        return killable;
    }
    
    // Functional methods

    /**
     * Animals share the same life-cycle: age, lose health, apply any
     * species-specific effects, give birth, and then try to move.
     *
     * @param newOrganisms A list to receive newborn organisms.
     */
    public void act(List<Organism> newOrganisms)
    {
        incrementAge();
        incrementHealth();
        applyStepEffects();
        if(isAlive()) {
            giveBirth(newOrganisms);
            moveToNextLocation();
        }
    }
    
    /**
     * Checks if the animal can breed
     * 
     * @return true if the animal can breed
     */
    protected boolean canBreed()
    {
        if (isMale == false)
        {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext())
            {
                Location where = it.next();
                Object thing = field.getObjectAt(where);
                if (speciesClass.isInstance(thing))
                {
                    Animal animal = (Animal) thing;
                    return animal.getGender() == true && getAge() >= getBreedingAge();
                }
            }
        }
        return false;
    }

    /**
     * Hook for subclasses that need additional work before breeding and moving.
     */
    protected void applyStepEffects()
    {
    }
    
    /**
     * A method that will make the animal object search the adjacent squares around it,
     * then find and eat an eligible food source.
     */
    protected Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if (animal != null) {                       // makes sure to filter out empty squares
                Organism target = (Organism) animal;
                if (this.getFoodSources().contains(target.getClass()) && target.isAlive() && getRand().nextDouble() <= weather.getWeatherModifier()) {
                    target.setDead();
                    //currentHealth = this.getMaxHealth();
                    if (this.getFoodSources().contains(target.getClass())) {
                        currentHealth = this.getMaxHealth();
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Decrease this animal's. This could result in the animal's death by hunger.
     */
    protected void incrementHealth()
    {
        currentHealth--;
        if(currentHealth == 0) {
            setDead();
        }
    }

    /**
     * Move towards food if possible, otherwise move into any adjacent free cell.
     * If no move is possible, the animal dies from overcrowding.
     */
    private void moveToNextLocation()
    {
        Location newLocation = findFood();
        if(newLocation == null) {
            newLocation = getField().freeAdjacentLocation(getLocation());
        }

        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            setDead();
        }
    }
}
