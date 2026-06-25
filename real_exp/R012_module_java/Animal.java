import java.util.List;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;

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
    public Animal(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        
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
    
    /**
     * Abstract method to return whether the target object is of a specific subclass of animal
     * 
     * @param target The object being compared
     * @return boolean True if target is of type of a specific animal subclass
     */
    abstract protected boolean getAnimalClass(Object thing);
    
    /**
     * Abstract method to return the list of food sources of a specific subclass of animal
     * 
     * @return ArrayList<Class> of class types that a subclass of animal is allowed to eat.
     */
    abstract protected HashSet<Class> getFoodSources();
    
    /**
     * Abstract method to return the list of killable classes of a specific subclass of animal
     * 
     * @return ArrayList<Class> of class types that a subclass of animal is allowed to kill.
     */
    abstract protected HashSet<Class> getKillable();
    // Accessor and mutator methods
    
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
    
    // Functional methods
    
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
                if (getAnimalClass(thing))
                {
                    Animal animal = (Animal) thing;
                    return animal.getGender() == true && getAge() >= getBreedingAge();
                }
            }
        }
        return false;
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
}
