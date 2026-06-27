import java.awt.Color;

/**
 * A simple model of a dingo.
 * Dingoes age, move, eat snakes, and die.
 *
 * @version 01.03.22
 */
public class Dingo extends Animal
{
    // Characteristics shared by all dingoes (class variables).

    // The probability that a dingo will be created in any given grid position.
    private static final double CREATION_PROBABILITY = 0.09;
    // The age at which a dingo can start to breed.
    private static final int BREEDING_AGE = 50;
    // The age to which a dingo can live.
    private static final int MAX_AGE = 700;
    // The likelihood of a dingo breeding.
    private static final double BREEDING_PROBABILITY = 0.04;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single snake. In effect, this is the
    // number of steps a dingo can go before it has to eat again.
    private static final int SNAKE_FOOD_VALUE = 100;

    /**
     * Create a dingo. A dingo can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the dingo will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Dingo(boolean randomAge, Field field, Location location) {
        super(randomAge, field, location, MAX_AGE, SNAKE_FOOD_VALUE);
    }

    protected int getMaxAge() { return MAX_AGE; }
    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected boolean isActiveAt(int time) { return (time >= 8) && (time <= 24); }

    protected Animal createOffspring(Field field, Location location) {
        return new Dingo(false, field, location);
    }

    // Eater: dingoes eat snakes and trample plants.
    public int eatSnake(Snake snake) { return SNAKE_FOOD_VALUE; }
    public boolean tramplesPlants()  { return true; }

    // Dingoes cannot be eaten; acceptInteraction() inherits the default -1 from Animal.

    // In fog, there is a 50% chance the dingo finds nothing.
    @Override
    protected Location findFood() {
        if (getFog() && rand.nextInt(2) != 0) {
            return null;
        }
        return super.findFood();
    }

    public static final EntityProvider PROVIDER = new EntityProvider() {
        public Class<?> getEntityClass()       { return Dingo.class; }
        public Color getColor()                { return Color.ORANGE; }
        public double getCreationProbability() { return CREATION_PROBABILITY; }
        public LivingEntity create(Field f, Location l) { return new Dingo(true, f, l); }
    };
}
