import java.util.List;
/**
 * A minimalist implementation of a Sheep, a sheep can only eat plants and not animals.
 *
 * @version 1.0
 */
public class Sheep extends HerbivoreAnimal
{

    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/

    private static final int BREEDING_AGE = 3;
    private static final int MAX_AGE = 20;
    private static final double BREEDING_PROBABILITY = 0.6;
    private static final int MAX_LITTER_SIZE = 5;
    private static final int BASE_HUNGER_LEVEL = 6;
    private static final int FEEDING_VALUE = 18;
    private static final List<Class<? extends Plant>> TARGET_PLANTS = List.of(Grass.class, Sedge.class, Sage.class);

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a new Sheep.
     *
     * @param randomAge Boolean flag dictating whether or not the attributes will be assigned a random value
     * @param field The environment the animal can move in
     * @param location Location of the animal in the field/grid
     * @param gender Gender of the animal
     */
    public Sheep(boolean randomAge, Field field, Location location, Gender gender)
    {
        super(randomAge, field, location, gender, BASE_HUNGER_LEVEL, MAX_AGE);
    }

    /*///////////////////////////////////////////////////////////////
                            BEHAVIOUR HOOKS
    //////////////////////////////////////////////////////////////*/

    @Override protected int getMaxAge() { return MAX_AGE; }
    @Override protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }
    @Override protected double getBreedingProbability(Weather weather) { return BREEDING_PROBABILITY; }
    @Override protected int getBreedingAge() { return BREEDING_AGE; }
    @Override protected List<Class<? extends Plant>> getTargetPlants() { return TARGET_PLANTS; }

    @Override
    protected Animal createOffspring(Field field, Location location, Gender gender)
    {
        return new Sheep(false, field, location, gender);
    }

    @Override
    public int getFeedingValue()
    {
        return FEEDING_VALUE;
    }
}
