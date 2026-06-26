import java.util.List;
import java.util.Random;

/**
 * A class representing the shared characteristics of plants
 *
 * @version 01.03.22
 */

public abstract class Plant
{
    //whether the plant is alive or not
    private boolean alive;
    // The plant's field.
    private Field field;
    // The plant's position in the field.
    private Location location;
    // whether it is raining or not
    private boolean rain;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new plant at a location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location) {
        alive = true;
        this.field = field;
        setLocation(location);
        rain = false;
    }

    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlants A list to receive newly created plants.
     */
    abstract public void act(List<Plant> newPlants);

    /**
     * Create a new plant of the current species.
     * @param field The field in which the plant will live.
     * @param location The plant location.
     * @return A new plant of the current species.
     */
    abstract protected Plant createYoung(Field field, Location location);

    /**
     * @return The probability that this plant reproduces in one step.
     */
    abstract protected double getReproducingProbability();

    /**
     * @return The maximum number of offspring this plant can produce.
     */
    abstract protected int getMaxOffspringSize();

    /**
     * Check whether the plant is alive or not.
     * @return true if the plant is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    protected void setDead() {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
     */
    protected void setLocation(Location newLocation) {
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
     * assign true to rain
     */
    protected void setRain(){ rain = true; }

    /**
     * assign false to rain
     */
    protected void resetRain(){ rain = false; }

    /**
     * Generate a number representing the number of plants to produce,
     * if it can reproduce.
     * If it is raining, then the number of offspring is increased
     * @return The number of offspring (may be zero).
     */
    protected int breed(double REPRODUCING_PROBABILITY, int MAX_OFFSPRING_SIZE)
    {
        int offspring = 0;
        if (rand.nextDouble() <= (REPRODUCING_PROBABILITY)){
            if (rain){
                offspring = rand.nextInt(MAX_OFFSPRING_SIZE) + 3;
            }
            else {
                offspring = rand.nextInt(MAX_OFFSPRING_SIZE);
            }
        }
        return offspring;
    }

    /**
     * Execute the common plant lifecycle for one simulation step.
     * @param newPlants A list to receive newly created plants.
     */
    protected final void performAct(List<Plant> newPlants) {
        if(isAlive()) {
            grow(newPlants);
        }
    }

    private void grow(List<Plant> newPlants) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int offspring = breed(getReproducingProbability(), getMaxOffspringSize());
        for(int b = 0; b < offspring && free.size() > 0; b++) {
            Location location = free.remove(0);
            Plant young = createYoung(field, location);
            newPlants.add(young);
        }
    }
}
