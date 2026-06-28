import java.util.List;
import java.util.Iterator;
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
    // The concrete species this animal instance belongs to.
    private final Class<? extends Animal> animalClass;
    // The animal's maximum health.
    private final int maxHealth;
    // Gender of the animal
    private boolean isMale;
    // Current health of the animal
    private int currentHealth;
    // Whether the animal is infected
    private boolean isInfected;
    // The food sources available to this animal.
    private final HashSet<Class> foodSources;
    // The classes this animal is willing to kill.
    private final HashSet<Class> killable;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomAge, Field field, Location location,
            int breedingAge, int maxAge, double breedingProbability,
            int maxLitterSize, boolean isDiurnal, int maxHealth,
            Class<? extends Animal> animalClass, HashSet<Class> foodSources,
            HashSet<Class> killable)
    {
        super(randomAge, field, location, breedingAge, maxAge, breedingProbability,
                maxLitterSize, isDiurnal);
        this.maxHealth = maxHealth;
        this.animalClass = animalClass;
        this.foodSources = new HashSet<>(foodSources);
        this.killable = new HashSet<>(killable);
        
        if (randomAge) {
            currentHealth = getRand().nextInt(maxHealth);
        }
        if (getRand().nextInt(2) == 0) {
            isMale = true;
        }
        else {
            isMale = false;
        }
        
        isInfected = false;
    }
    
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
     * Returns the animal's max health.
     *
     * @return the animal's max health.
     */
    protected int getMaxHealth()
    {
        return maxHealth;
    }

    /**
     * Returns whether the supplied object is the same species as this animal.
     *
     * @param thing The object being compared.
     * @return True if the object is of this animal's species.
     */
    protected boolean getAnimalClass(Object thing)
    {
        return animalClass.isInstance(thing);
    }

    /**
     * Returns the food sources available to this animal.
     *
     * @return The animal's food sources.
     */
    protected HashSet<Class> getFoodSources() {
        return foodSources;
    }

    /**
     * Returns the classes this animal can kill.
     *
     * @return The animal's killable classes.
     */
    protected HashSet<Class> getKillable() {
        return killable;
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
     * Handles the standard post-survival behavior shared by most animals.
     *
     * @param newOrganisms A list to receive newborn animals.
     */
    protected void performStandardAct(List<Organism> newOrganisms)
    {
        giveBirth(newOrganisms);
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
     * Helper for species constructors to create a set of classes.
     *
     * @param classes The classes to include.
     * @return A HashSet containing the supplied classes.
     */
    protected static HashSet<Class> classSet(Class... classes)
    {
        HashSet<Class> set = new HashSet<>();
        for(Class clazz : classes) {
            set.add(clazz);
        }
        return set;
    }
}
