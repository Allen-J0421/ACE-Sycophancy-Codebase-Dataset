import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    // The animal's hurt level, when > 3 die
    private int burn;
    // The Deer's gender.
    private String gender;

    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        burn = 0;
        if (rand.nextBoolean()) {
            gender = "M";
        }
        else {
            gender = "F";
        }
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param step The current step.
     * @param weather The current weather.
     */
    abstract public void act(List<Animal> newAnimals, int step, String weather);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * update the animal's burn status.
     * @param weather The current weather.
     */
    protected void updateBurnStatus(String weather)
    {
        if (burn > 0) {
            if (weather != "Rainy") {
                burn();
            }
            else {
                recover();
            }
        }
    }
    
    /**
     * animal that step on fire will get burn and if burn status > 3 the animal will die 
     */
    protected void burn()
    {
        burn++;
        if (burn > 3) {
            setDead();
        }
    }
    
    /**
     * if weather is rainy the animal will recover and reset it's burn status to zero.
     */
    protected void recover()
    {
        burn = 0;
    }

    /**
     * @return the animal's gender
     */
    protected String Gender()
    {
        return gender;
    }
}
