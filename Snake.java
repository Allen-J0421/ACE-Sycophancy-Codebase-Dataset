import java.awt.Color;

/**
 * A simple model of a snake.
 * Snakes age, move, eat rats, breed, and die.
 *
 * @version 01.03.22
 */
public class Snake extends Animal
{
// Characteristics shared by all snakes (class variables).

    // The probability that a snake will be created in any given grid position.
    private static final double CREATION_PROBABILITY = 0.12;
    // The age at which a snake can start to breed.
    private static final int BREEDING_AGE = 30;
    // The age to which a snake can live.
    private static final int MAX_AGE = 700;
    // The likelihood of a snake breeding.
    private static final double BREEDING_PROBABILITY = 0.33;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 11;
    // The food value of a single rat. In effect, this is the
    // number of steps a snake can go before it has to eat again.
    private static final int RAT_FOOD_VALUE = 100;

    /**
     * Create a snake. A snake can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the snake will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Snake(boolean randomAge, Field field, Location location) {
        super(randomAge, field, location, MAX_AGE, RAT_FOOD_VALUE);
    }

    protected int getMaxAge() { return MAX_AGE; }
    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected boolean isActiveAt(int time) { return (time >= 5) && (time <= 23); }

    protected Animal createOffspring(Field field, Location location) {
        return new Snake(false, field, location);
    }

    // Eater: snakes eat rats and trample plants.
    public int eatRat(Rat rat)      { return RAT_FOOD_VALUE; }
    public boolean tramplesPlants() { return true; }

    // Interactable: snakes can be eaten (by eagles and dingoes).
    public int acceptInteraction(Eater eater) {
        int foodValue = eater.eatSnake(this);
        if (foodValue >= 0) setDead();
        return foodValue;
    }

    public static final EntityProvider PROVIDER = new EntityProvider() {
        public Class<?> getEntityClass()       { return Snake.class; }
        public Color getColor()                { return Color.BLACK; }
        public double getCreationProbability() { return CREATION_PROBABILITY; }
        public LivingEntity create(Field f, Location l) { return new Snake(true, f, l); }
    };
}
