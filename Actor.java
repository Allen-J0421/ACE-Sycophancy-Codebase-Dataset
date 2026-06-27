import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

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
    // The actor's display name.
    private final String actorName;
    // The actor's maximum age.
    private final int maxAge;
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
    public Actor(Time time, Field field, Location location, String actorName, int maxAge)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        this.time = time;
        this.actorName = actorName;
        this.maxAge = maxAge;
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
        if(isAlive() && !field.canTraverse(this, location)) {
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
     * Returns the actors Name
     * @return The actors name
     */
    protected String getActorName()
    {
        return actorName;
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
        int adjustedMaxAge = maxAge;
        for(Disease disease : setDiseases){
            adjustedMaxAge *= disease.getActorsAffectedMap().get(getActorName());
        }
        if(age > adjustedMaxAge) {
            setDead();
        }

    }

    /**
     * Returns the actors max age
     * @return The max age
     */
    protected int getMaxAge()
    {
        return maxAge;
    }

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
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Actor young = birth(loc, setDiseases);
            newActors.add(young);
        }
    }

    /**
     * Copy birth-spread diseases from a parent into this actor.
     * @param parentDiseases The parent's diseases.
     */
    protected void inheritBirthDiseases(Set<Disease> parentDiseases)
    {
        for(Disease disease : parentDiseases) {
            if(disease.isSpreadByBirth()) {
                setDiseases.add(disease);
            }
        }
    }

    /**
     * Seed this actor with any diseases it can start with.
     * @param actorName The actor name used for disease lookup.
     */
    protected void seedStartingDiseases(String actorName)
    {
        Random random = Randomizer.getRandom();
        for(Disease disease : Simulator.diseases) {
            Double startingProbability = disease.getStartingActorsMap().get(actorName);
            if(startingProbability != null && random.nextDouble() <= startingProbability) {
                setDiseases.add(disease);
            }
        }
    }

    /**
     * Determine how many offspring this actor can produce.
     * @param breedingAge The minimum age required to breed.
     * @param breedingProbability The chance of a successful breeding attempt.
     * @param maxLitterSize The largest number of offspring produced.
     * @param random The random source for breeding.
     * @return The number of offspring.
     */
    protected int breedOffspringCount(int breedingAge, double breedingProbability, int maxLitterSize, Random random)
    {
        if(age >= breedingAge && random.nextDouble() <= breedingProbability) {
            return random.nextInt(maxLitterSize) + 1;
        }
        return 0;
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
        return setDiseases.size() > 0;
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
    abstract protected Actor birth(Location loc, Set<Disease> parentDiseases);
}
