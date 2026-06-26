import java.util.List;

/**
 * A simple model of a grass plant.
 * Grass plants reproduce and grow into other locations
 *
 * @version 01.03.22
 */

public class Grass extends Plant
{
    // Characteristics shared by all grass (class variables).

    // The likelihood of a grass reproducing.
    private static final double REPRODUCING_PROBABILITY = 0.77;
    // The maximum number of offspring that can be produced.
    private static final int MAX_OFFSPRING_SIZE = 9;

    /**
     * Create a grass. A grass is created as a newborn age 0
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(Field field, Location location) {
        super(field, location);
    }

    /**
     * This is what the grass does most of the time:
     * it grows and reproduces offspring into adjacent locations
     * @param newGrass A list to return newly produced grasses.
     */
    public void act(List<Plant> newGrass) {
        if(isAlive()) {
            growPlants(newGrass, REPRODUCING_PROBABILITY, MAX_OFFSPRING_SIZE);
        }
    }

    /**
     * Create a new grass offspring.
     */
    protected Plant createOffspring(Field field, Location location) {
        return new Grass(field, location);
    }
}
