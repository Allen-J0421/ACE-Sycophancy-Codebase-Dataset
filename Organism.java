import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An abstract class representing any organism in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Organism implements Entity {

    // define fields
    //number of steps an organism remains for after dying but not having been eaten
    private static final int LIFETIME_AFTER_DEATH = 40;
    private boolean alive;
    private boolean removed;
    private Field field;
    private Location location;
    private int howLongDead;

    private int age;

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for an Organism in the simulation.
     *
     * @param randomAge Whether we assign this organism a random age or not.
     * @param field The field in which this organism resides.
     * @param location The location in which this organism is spawned into.
     */
    public Organism(boolean randomAge, Field field, Location location) {
        this.howLongDead = 0;
        alive = true;
        removed = false;
        this.field = field;
        setLocation(location);

        if (randomAge) {
            age = rand.nextInt(getMaxAge());
        } else {
            age = 0;
        }
    }

    /**
     * Abstract method for what an organism does, i.e. what is always run at every step.
     *
     * @param newOrganisms A list of all newborn organisms in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    abstract public void act(List<Entity> newOrganisms, Weather weather, TimeOfDay time);

    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Removes the current organism from the field.
     */
    protected void remove() {
        setDead();
        if(location != null) {
            field.clear(location);
            location = null;
            setField(null);
        }
        removed = true;
    }

    /**
     * Return whether the current organism object is supposed
     * to be removed from the simulation.
     *
     * @return A boolean value representing if the organism has been removed or not.
     */
    public boolean isRemoved() {
        return removed;
    }

    /**
     * Indicate that the organism is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
    }

    /**
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return this.location;
    }

    /**
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
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
     * Clear this organism from its current field location without marking it removed.
     */
    protected void clearLocation() {
        if (location != null) {
            field.clear(location);
            location = null;
            setField(null);
        }
    }

    /**
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField()
    {
        return this.field;
    }

    /**
     * Getter method for the probability to breed of the organism.
     *
     * @return A double value representing the breeding probability.
     */
    abstract public double getBreedingProbability();

    /**
     * Getter method for the maximum litter size of the organism's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    abstract public int getMaxLitterSize();

    /**
     * Getter method for the maximum age of the organism.
     *
     * @return An integer value representing the maximum age.
     */
    abstract public int getMaxAge();

    /**
     * Getter method for the age of breeding of the organism.
     *
     * @return A double value representing the breeding age.
     */
    abstract public int getBreedingAge();

    /**
     * Called when breeding occurs for this organism.
     *
     * @return The number of births as a result of breeding.
     */
    protected int breed() {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Create a new instance of this organism.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Organism instance.
     */
    abstract protected Organism createNewOrganism(Field field, Location location);

    /**
     * Check whether or not this organism is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newOrganisms A list to return newly born organisms.
     */
    protected void giveBirth(List<Entity> newOrganisms) {
        // New organisms are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newOrganisms.add(createNewOrganism(field, loc));
        }
    }

    /**
     * Checks all adjacent location for organisms that meet specific
     * breeding conditions, and returns true if it is even possible.
     *
     * @return Whether this organism can breed or not.
     */
    abstract protected boolean canBreed();

    /**
     * Increase the age.
     * This could result in the organisms death.
     */
    protected void incrementAge()
    {
        age++;

        if (age > getMaxAge()) {
            //setDead();
            remove();
        }
    }

    /**
     * Getter method for the current age of this organism.
     *
     * @return An integer value representing age.
     */
    protected int getAge() {
        return this.age;
    }

    /**
     * Setter method for the field in which this organism resides.
     * @param field The field where the organism is located.
     */
    protected void setField(Field field) {
        this.field = field;
    }

    /**
     * Prevents overcrowding of dead animals
     * Remove the organism from the field after being dead for a s
     * specified number of steps and not being eaten.
     */
    protected void decayifDead() {
        this.howLongDead++;
        if (this.howLongDead > LIFETIME_AFTER_DEATH){
            remove();
        }
    }
}
