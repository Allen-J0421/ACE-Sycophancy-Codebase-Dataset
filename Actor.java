import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

/**
 * A class representing shared characteristics of actors.
 *
 * @version 27.02.22
 */
public abstract class Actor
{
    // Whether the actor is alive or not.
    private boolean alive;
    // The actor's field.
    private Field field;
    // The actor's position in the field.
    private Location location;
    //The actor's age
    protected int age;
    //A reference to the time object of the simulation
    private Time time; 
    //The diseases that the actor has
    protected Set<Disease> setDiseases;
    //If the animal can go on water
    protected boolean canGoWater;
    //If the animal can go on land
    protected boolean canGoLand;

    /**
     * Create a new actor at location in field with time as well.
     * With a set that will add any disease that it catches
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param time The time in the simulation
     */
    public Actor(Time time, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        this.time = time;
        setDiseases = new HashSet<>();
    }

    /**
     * Make this actor act - that is: make it do
     * whatever it wants/needs to do.
     * Checks if the location it is in is land or water, if it is in a location where
     * it can not be in, it will die by either being on water when it can only be on land vice versa
     * @param newActors A list to receive newly born actors.
     * @param weather The weather condition of the simulation.
     */
    public void act(List<Actor> newActors, WeatherCond weather)
    {
        incrementAge();
        if(isAlive() && ((!field.isUnderWater(location.getRow(), location.getCol()) && !canMoveOnLand()) || (field.isUnderWater(location.getRow(), location.getCol()) && !canMoveOnWater()))) {
            setDead();
        }
    }

    /**
     * Check whether the actor is alive or not.
     * @return true if the actor is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the actor is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(this, location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the actor's location.
     * @return The actor's location.
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
            field.clear(this, location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the actor's field.
     * @return The actor's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Return the time in the field.
     * @return The time.
     */
    protected Time getTime()
    {
        return time;
    }

    /**
     * Increments the actors age by 1
     * If the actor has a disease then the max age will decrease and if the current age
     * is greater than the maxAge then they will die.
     * Each disease reduces the maxAge by a certain amount.
     */
    private void incrementAge()
    {
        age++;
        int maxAge = getMaxAge();
        for(Disease disease : setDiseases){
            Double severity = disease.getActorsAffectedMap().get(getClass());
            if(severity != null){
                maxAge *= severity;
            }
        }
        if(age > maxAge) {
            setDead();
        }
    }

    /**
     * Returns the actors max age
     * @return The max age
     */
    abstract protected int getMaxAge();

    /**
     * Checks if there is free locations and places the offspring there
     * If the parent has diseases then those transferred by birth it will be transfered to the child
     * @param newActors The List of new actors that have been born to add to the simulator List of actors
     */
    protected void giveBirth(List<Actor> newActors)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(this, getLocation());
        int births = breed();
        for(int b = 0; b < births && !free.isEmpty(); b++) {
            Location loc = free.remove(0);
            Actor young = birth(loc,setDiseases);
            newActors.add(young);
        }
    }

    /**
     * Returns if the actor can move on land
     * @return true if the actor can move on land
     */
    public boolean canMoveOnLand()
    {
        return canGoLand;
    }

    /**
     * Returns if the actor can move on water
     * @return true if the actor can move on water
     */
    public boolean canMoveOnWater()
    {
        return canGoWater;
    }
    
    /**
     * Return if the actor has a disease
     * @return true if the actor has a disease
     */
    public boolean hasDisease(){
        return !setDiseases.isEmpty();
    }
    
    /**
     * Returns the set of diseases the actor has
     * @return The set of diseases the actor has
     */
    public Set<Disease> getActorDiseaseSet()
    {
        return setDiseases;
    }

    /**
     * Inherit diseases from parent — called from newborn constructors.
     */
    protected void inheritDiseases(Set<Disease> parentDiseases) {
        for(Disease disease : parentDiseases) {
            if(disease.isSpreadByBirth()) {
                setDiseases.add(disease);
            }
        }
    }

    /**
     * Assign starting diseases by probability — called from random-age constructors.
     * @param diseases The global disease list from the simulation.
     */
    protected void initStartingDiseases(List<Disease> diseases) {
        Random rand = Randomizer.getRandom();
        for(Disease disease : diseases) {
            Double prob = disease.getStartingActorsMap().get(getClass());
            if(prob != null && rand.nextDouble() <= prob) {
                setDiseases.add(disease);
            }
        }
    }

    /**
     * Returns how many actors are produced during birth
     * @return The number of actors birthed
     */
    abstract protected int breed();

    /**
     * Creates a new actor 
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new actor created
     */
    abstract protected Actor birth(Location loc, Set<Disease>... parentDiseases);
}
