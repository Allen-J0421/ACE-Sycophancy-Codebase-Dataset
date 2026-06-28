import java.util.List;
/**
 * A minimalist implementation of a Reindeer, a reindeer can only eat plants and not animals,
 * only the act method is unique to the reindeer.
 *
 * @version 1.0
 */
public class Reindeer extends HerbivoreAnimal
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    private static final int BREEDING_AGE = 4;
    private static final int MAX_AGE = 28;
    private static final double BREEDING_PROBABILITY = 0.7;
    private static final int MAX_LITTER_SIZE = 5;
    private static final int BASE_HUNGER_LEVEL = 15;
    private static final int FEEDING_VALUE = 18;
    private static final List<Class<? extends Plant>> TARGET_PLANTS = List.of(Grass.class, Sage.class, Sedge.class);
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates a new Reindeer.
     * 
     * @param randomAge Boolean flag dictating whether or not the attributes will be assigned a random value
     * @param field The environment the animal can move in
     * @param location Location of the animal in the field/grid
     * @param gender Gender of the animal
     */
    public Reindeer(boolean randomAge, Field field, Location location, Gender gender)
    {
        super(randomAge, field, location, gender, BASE_HUNGER_LEVEL, MAX_AGE);
    }
    
    /*///////////////////////////////////////////////////////////////
                            ANIMAL BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/   
    
    @Override protected int getMaxAge() { return MAX_AGE; }

    @Override protected int getBreedingAge() { return BREEDING_AGE; }

    @Override protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    @Override protected double getBreedingProbability(Weather weather) { return BREEDING_PROBABILITY; }

    @Override protected List<Class<? extends Plant>> getTargetPlants() { return TARGET_PLANTS; }

    /**
     * Returns the amount by which the hungerlevel would increment by if the animal were to be eaten
     * @return the feeding value
     */
    public int getFeedingValue()
    {
        return FEEDING_VALUE;
    }
}
