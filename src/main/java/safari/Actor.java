package safari;

import java.util.List;
import java.util.Random;

/**
 * An actor is a representation of a living thing in the simulation.
 *
 * @version 2022.03.01
 */
public abstract class Actor 
{
    // The actor's field.
    private Field field;
    // The actor's position in the field.
    private Location location;
    // Whether the actor is alive or not.
    private boolean alive;
    //The actor's age
    private int age;
    //how much the actor grows. This is added to the animals food level.
    private double growthLevel;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    /**
     * Create a new actor at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Actor(Field field, Location location)
    {
        this.field = field;
        setLocation(location);
        alive = true;
    }

    /**
     * Return the actor's location.
     * @return The actors's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the actor at the new location in the given field.
     * @param newLocation The actor's new location.
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
     * Return the actors's field.
     * @return The actors's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Set the field.
     * @param place. The field.
     */
    protected void setField(Field place){
        field = place;
    }

    /**
     * Check whether the actor is alive or not.
     * @return true if the actor is still alive.
     */
    protected boolean isActive()
    {
        return alive;
    }

    /**
     * Set if the actor is alive or not.
     * @param alive. If the actor is alive.
     */
    protected void setAlive(boolean alive)
    {
        this.alive = alive;
    }

    /**
     * Indicate that the actor is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        setAlive(false);
        if(getLocation() != null) {
            getField().clear(getLocation());
            location = null ;
            field = null;
        }
    }

    /**
     * Make this actor act - that is: make it do
     * whatever it wants/needs to do.
     * @param newActors A list to receive newly born actors.
     * @param simulator The simulator.
     */
    public abstract void act(List<Actor> newActors, Simulator simulator);

    /**
     * Get age of an actor
     * @return age of the actor.
     */
    protected int getAge(){
        return age;
    }

    /**
     * Set age of an actor
     * @param age. Age of the actor.
     */
    protected void setAge(int  age){
        this.age = age ;
    }

    /**
     * Increase the age. This could result in the actor's death.
     * Actor's age only increases once every 4 steps.
     * @param step. The number of steps in the simulation
     */
    protected void incrementAge(int step)
    {
        if(step % 4 == 0){
            age++;
        }
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Gets the max age of an actor.
     * @return Max age of the actor.
     */
    abstract protected int getMaxAge();

    /**
     * Return how much the actor has grown.
     * @return  How much the actor grows.
     */
    protected double getGrowthLevel(){
        return growthLevel;
    }

    /**
     * Increase the actor growth level by how much the actor has grown during this step.
     * @param value. How much more the actor has grown.
     */
    protected void setGrowthLevel(double value){
        growthLevel = value + growthLevel;
    }
    
    /**
     * Returns the shared random generator object
     * @return The random generator object
     */
    protected Random getRandom(){
        return rand;
    }
}