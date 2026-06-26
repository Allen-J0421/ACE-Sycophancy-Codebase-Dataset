import java.util.Random;

/**
 * A simple model of an ant.
 * Ants age, move, eat acacia and grass, breed, and die.
 *
 * @version 01.03.22
 */
public class Ant extends Animal
{
    // Characteristics shared by all ants (class variables).

    // The age at which an ant can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which an ant can live.
    private static final int MAX_AGE = 400;
    // The likelihood of an ant breeding.
    private static final double BREEDING_PROBABILITY = 0.32;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 14;
    // The food value of a single acacia. In effect, this is the
    // number of steps an ant can go before it has to eat again.
    private static final int ACACIA_FOOD_VALUE = 60;
    // The food value of a single grass. In effect, this is the
    // number of steps an ant can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 60;
    // The food sources ants eat, in search order.
    private static final FoodSource[] FOOD_SOURCES = {
        new FoodSource(Acacia.class, ACACIA_FOOD_VALUE),
        new FoodSource(Grass.class, GRASS_FOOD_VALUE)
    };
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create an ant. An ant can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the ant will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Ant(boolean randomAge, Field field, Location location) {
        super(field, location);
        this.setGender();
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            setFoodLevel(rand.nextInt(ACACIA_FOOD_VALUE));
        }
        else {
            setAge(0);
            setFoodLevel(ACACIA_FOOD_VALUE);
        }
    }

    protected int getMaxAge() {
        return MAX_AGE;
    }

    protected int getBreedingAge() {
        return BREEDING_AGE;
    }

    protected double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    protected int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    protected boolean isActive(int time) {
        return isActiveBetween(time, 4, 20);
    }

    protected Animal createYoung(Field field, Location location) {
        return new Ant(false, field, location);
    }

    protected FoodSource[] getFoodSources() {
        return FOOD_SOURCES;
    }

    protected boolean canTramplePlants() {
        return false;
    }

}
