import java.util.List;
/**
 * A minimalist implementation of a Fox, a fox can only eat other animals and not plants.
 *
 * @version 1.0
 */
public class CarnivoreFox extends CarnivoreAnimal
{

    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/

    private static final int BREEDING_AGE = 12;
    private static final int MAX_AGE = 71;
    private static final double BREEDING_PROBABILITY = 0.076;
    private static final int BASE_HUNGER_LEVEL = 25;
    private static final int FEEDING_VALUE = 18;
    private static final List<Class<? extends Animal>> PREY_DIET = List.of(Sheep.class, Reindeer.class);
    private static final int MAX_LITTER_SIZE = 3;

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a new Fox.
     *
     * @param randomAge Boolean flag dictating whether or not the attributes will be assigned a random value
     * @param field The environment the animal can move in
     * @param location Location of the animal in the field/grid
     * @param gender Gender of the animal
     */
    public CarnivoreFox(boolean randomAge, Field field, Location location, Gender gender)
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
    @Override protected List<Class<? extends Animal>> getPreyDiet() { return PREY_DIET; }
    @Override protected boolean isRestrictedToDay() { return true; }

    @Override
    protected Animal createOffspring(Field field, Location location, Gender gender)
    {
        return new CarnivoreFox(false, field, location, gender);
    }

    @Override
    public int getFeedingValue()
    {
        return FEEDING_VALUE;
    }
}
