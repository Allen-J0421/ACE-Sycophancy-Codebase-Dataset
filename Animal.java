import java.util.Iterator;
import java.util.List;

/**
 * A class representing shared characteristics of all animals within the simultion.
 * Animals will find food, have a gender and be able to become infected.
 * 
 * @version 2022.03.02
 */
public abstract class Animal extends Organism
{
    // Shared lifecycle and diet settings for this animal's species.
    private final AnimalAttributes attributes;
    // Shared weather state provided by the simulation container.
    private final Weather weather;
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
                  AnimalAttributes attributes, Weather weather)
    {
        super(randomAge, field, location, attributes);
        this.attributes = attributes;
        this.weather = weather;
        
        if (randomAge) {
            currentHealth = getRand().nextInt(attributes.getMaxHealth());
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
     * Checks if the animal can breed
     * 
     * @return true if the animal can breed
     */
    protected boolean hasBreedingMate()
    {
        if (isMale == false)
        {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext())
            {
                Location where = it.next();
                Organism organism = (Organism) field.getOccupantAt(where);
                if (organism != null && organism.getSpecies() == getSpecies())
                {
                    Animal animal = (Animal) organism;
                    return animal.getGender() == true
                        && getAge() >= getAttributes().getBreedingAge();
                }
            }
        }
        return false;
    }

    /**
     * A method that will make the animal object search the adjacent squares around it,
     * then find and eat an eligible food source.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Organism target = (Organism) field.getOccupantAt(where);
            if (target != null) {
                if (attributes.getFoodSources().contains(target.getSpecies())
                    && target.isAlive()
                    && getRand().nextDouble() <= weather.getWeatherModifier()) {
                    target.setDead();
                    currentHealth = attributes.getMaxHealth();
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
