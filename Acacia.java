import java.util.List;

/**
 * A simple model of an acacia plant.
 * Acacia plants reproduce and grow into other locations
 *
 * @version 01.03.22
 */

public class Acacia extends Plant
{
    // Characteristics shared by all acacias (class variables).
    
    // The likelihood of an acacia reproducing.
    private static final double REPRODUCING_PROBABILITY = 0.71;
    // The maximum number of offspring that can be produced.
    private static final int MAX_OFFSPRING_SIZE = 9;

    /**
     * Create an acacia. An acacia is created as a newborn with age 0
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Acacia(Field field, Location location)
    {
        super(field, location);
    }

    /**
     * This is what the acacia does most of the time:
     * it grows and reproduces offspring into adjacent locations
     * @param newAcacias A list to return newly produced acacia.
     */
    public void act(List<Plant> newAcacias) {
        performAct(newAcacias);
    }

    protected Plant createYoung(Field field, Location location) {
        return new Acacia(field, location);
    }

    protected double getReproducingProbability() {
        return REPRODUCING_PROBABILITY;
    }

    protected int getMaxOffspringSize() {
        return MAX_OFFSPRING_SIZE;
    }
}
