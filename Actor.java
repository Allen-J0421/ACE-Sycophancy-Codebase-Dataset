import java.util.Random;
import java.util.List;

/**
 * An actor in our simulation that performs a set of actions.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public abstract class Actor
{
    // Whether the actor is alive or not:
    private boolean isAlive;
    // The actor's field:
    private Field field;
    // The actor's position in the field:
    private Location location;
    // The worth of the actor if consumed:
    private int consumptionWorth;
    // The gender of the actor:
    private Gender gender;
    // The probability of this actor breeding:
    private double breedingProbability;
    // The max number of births this actor can have in one step:
    private int maxBirthsAtOnce;
    // A shared random number generator controlling breeding:
    protected static final Random rand = Randomizer.getRandom();
    // The maximum amount of food an actor can eat:
    private int maxSustenanceLevel;
    // The maximum age the actor can have:
    private int maxAge;
    // The current age of the actor:
    private int currentAge;

    /**
     * Create a new actor at a location in the field.
     *
     * @param field            The field currently occupied.
     * @param location         The location within the field.
     * @param consumptionWorth The worth of the actor if consumed.
     */
    public Actor(Field field, Location location, int consumptionWorth,
                 double breedingProbability, int maxBirthsAtOnce, int maxSustenanceLevel, int maxAge)
    {
        isAlive = true;
        this.field = field;
        setLocation(location);
        this.consumptionWorth = consumptionWorth;
        this.breedingProbability = breedingProbability;
        this.maxBirthsAtOnce = maxBirthsAtOnce;
        gender = Gender.getRandom();
        this.maxSustenanceLevel = maxSustenanceLevel;
        this.maxAge = maxAge;
    }

    /** @return The maximum age this actor can reach. */
    protected int getMaxAge() { return maxAge; }

    /** @return The current age of this actor. */
    protected int getCurrentAge() { return currentAge; }

    /** Set the current age of this actor. */
    protected void setCurrentAge(int age) { currentAge = age; }
    
    /**
     * Make this actor act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newActors A list to receive newly born actors.
     */
    abstract public void act(List<Actor> newActors);
    
    /**
     * @return The probability of this actor breeding.
     */
    protected double getBreedingProbability() { return breedingProbability; }
    
    /**
     * @return The max number of births this actor can have in one step.
     */
    protected int getMaxBirthsAtOnce() { return maxBirthsAtOnce; }
    
    /**
     * @return The gender of this actor.
     */
    public Gender getGender() { return gender; }
    
    /**
     * @return The worth of this actor if consumed.
     */
    public int getConsumptionWorth() { return consumptionWorth; }

    /**
     * @return True if the actor is still alive.
     */
    public boolean getIsAlive() { return isAlive; }

    /**
     * Indicate that the actor is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        isAlive = false;
        if (location != null)
        {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * @return The actor's location.
     */
    protected Location getLocation() { return location; }
    
    /**
     * Place the actor at the new location in the given field.
     * 
     * @param newLocation The actor's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if (location != null) field.clear(location);
        
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * @return The actor's field.
     */
    protected Field getField() { return field; }
    
    /**
     * Increase the age.
     * This could result in the actor's death.
     */
    protected void incrementAge()
    {
        currentAge++;
        if (currentAge > maxAge) setDead();
    }
    /**
     * Returns true if the actor becomes a carcass when eaten, false otherwise.
     * Consumers override this to return true; all other actors use this default.
     */
    protected boolean becomeCarcass() { return false; }
}