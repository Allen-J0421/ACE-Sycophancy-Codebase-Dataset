import java.util.List;
import java.util.Random;

/**
 * A class representing the shared characteristics of plants
 *
 * @version 01.03.22
 */

public abstract class Plant implements Interactable, LivingEntity
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
    protected static final Random rand = Randomizer.getRandom();

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
    public void act(List<LivingEntity> newEntities, int time) {
        if (isAlive()) {
            grow(newEntities);
        }
    }

    private void grow(List<LivingEntity> newEntities) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int offspring = breed(getReproducingProbability(), getMaxOffspringSize());
        for (int b = 0; b < offspring && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newEntities.add(createOffspring(field, loc));
        }
    }

    protected abstract double getReproducingProbability();
    protected abstract int getMaxOffspringSize();
    protected abstract Plant createOffspring(Field field, Location location);

    /**
     * Dispatches to the appropriate eatXxx method on the eater.
     * Subclasses implement this to call eater.eatGrass(this), eater.eatAcacia(this), etc.
     */
    protected abstract int eatMe(Eater eater);

    /**
     * Handles interaction with a predator. Asks the eater if it eats this plant
     * type; if not, checks whether the eater tramples plants instead.
     * Kills this plant on any non-negative result.
     */
    public int acceptInteraction(Eater eater) {
        int foodValue = eatMe(eater);
        if (foodValue < 0) {
            foodValue = eater.tramplesPlants() ? 0 : -1;
        }
        if (foodValue >= 0) setDead();
        return foodValue;
    }

    /**
     * Check whether the plant is alive or not.
     * @return true if the plant is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    public void setDead() {
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
}
