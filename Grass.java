import java.awt.Color;
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

    // The probability that grass will be created in any given grid position.
    private static final double CREATION_PROBABILITY = 0.36;
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

    protected double getReproducingProbability() { return REPRODUCING_PROBABILITY; }
    protected int getMaxOffspringSize() { return MAX_OFFSPRING_SIZE; }

    protected Plant createOffspring(Field field, Location location) {
        return new Grass(field, location);
    }

    protected int eatMe(Eater eater) { return eater.eatGrass(this); }

    public static final EntityProvider PROVIDER = new EntityProvider() {
        public Class<?> getEntityClass()       { return Grass.class; }
        public Color getColor()                { return Color.CYAN; }
        public double getCreationProbability() { return CREATION_PROBABILITY; }
        public LivingEntity create(Field f, Location l) { return new Grass(f, l); }
    };
}
