/**
 * A simple model of a rat.
 * Rats age, move, eat ants, breed, and die.
 *
 * @version 01.03.22
 */
public class Rat extends Animal
{
    // Characteristics shared by all rats (class variables).

    // The age at which a rat can start to breed.
    private static final int BREEDING_AGE = 25;
    // The age to which a rat can live.
    private static final int MAX_AGE = 600;
    // The likelihood of a rat breeding.
    private static final double BREEDING_PROBABILITY = 0.31;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 15;
    // The food value of a single ant. In effect, this is the
    // number of steps a rat can go before it has to eat again.
    private static final int ANT_FOOD_VALUE = 100;

    /**
     * Create a rat. A rat can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the rat will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rat(boolean randomAge, Field field, Location location) {
        super(randomAge, field, location, MAX_AGE, ANT_FOOD_VALUE);
    }

    protected int getMaxAge() { return MAX_AGE; }
    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected boolean isActiveAt(int time) { return (time >= 0) && (time <= 18); }

    protected Animal createOffspring(Field field, Location location) {
        return new Rat(false, field, location);
    }

    // Eater: rats eat ants and trample plants.
    public int eatAnt(Ant ant)   { return ANT_FOOD_VALUE; }
    public boolean tramplesPlants() { return true; }

    // Interactable: rats can be eaten (by snakes and eagles).
    public int acceptInteraction(Eater eater) {
        int foodValue = eater.eatRat(this);
        if (foodValue >= 0) setDead();
        return foodValue;
    }
}
