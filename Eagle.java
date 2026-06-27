/**
 * A simple model of an eagle.
 * Eagles age, move, eat snakes and rats, and die.
 *
 * @version 01.03.22
 */

public class Eagle extends Animal
{
    // Characteristics shared by all eagles (class variables).

    // The age at which an eagle can start to breed.
    private static final int BREEDING_AGE = 50;
    // The age to which an eagle can live.
    private static final int MAX_AGE = 700;
    // The likelihood of an  eagle breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 11;
    // The food value of a single rat. In effect, this is the
    // number of steps an eagle can go before it has to eat again.
    private static final int RAT_FOOD_VALUE = 40;
    // The food value of a single snake. In effect, this is the
    // number of steps an eagle can go before it has to eat again.
    private static final int SNAKE_FOOD_VALUE = 60;

    /**
     * Create an eagle. An eagle can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Eagle(boolean randomAge, Field field, Location location) {
        super(randomAge, field, location, MAX_AGE, SNAKE_FOOD_VALUE);
    }

    protected int getMaxAge() { return MAX_AGE; }
    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected boolean isActiveAt(int time) { return (time >= 6) && (time <= 22); }

    protected Animal createOffspring(Field field, Location location) {
        return new Eagle(false, field, location);
    }

    // Eater: eagles eat snakes and rats and trample plants.
    public int eatSnake(Snake snake) { return SNAKE_FOOD_VALUE; }
    public int eatRat(Rat rat)       { return RAT_FOOD_VALUE; }
    public boolean tramplesPlants()  { return true; }

    // Eagles cannot be eaten; acceptInteraction() inherits the default -1 from Animal.

    // Preserves the original RNG call that occurs when fog is active.
    @Override
    protected Location findFood() {
        if (getFog()) rand.nextInt(2);
        return super.findFood();
    }
}
