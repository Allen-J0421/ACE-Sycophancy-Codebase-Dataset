import java.util.EnumSet;
import java.util.Set;
/**
 * A minimalist implementation of a Sheep, a sheep can only eat plants and not animals,
 * only the act method is unique to the sheep.
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
    private static final Set<PlantSpecies> TARGET_PLANTS =
        EnumSet.of(PlantSpecies.GRASS, PlantSpecies.SEDGE, PlantSpecies.SAGE);
    
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
                            ANIMAL BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Method in charge of the sheep's action's during a step. During a step,
     * a sheep will age, increase in hunger, seek to mate as well as look for food.
     * 
     * @param newSheeps the new sheeps to be born in case the sheep succesfully mates.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    @Override
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    @Override
    protected double getBreedingProbability(Weather weather)
    {
        return BREEDING_PROBABILITY;
    }

    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    @Override
    protected Set<PlantSpecies> getTargetPlants()
    {
        return TARGET_PLANTS;
    }
    
    /**
     * Returns the amount by which the hungerlevel would increment by if the animal were to be eaten
     * @return the feeding value
     */
    public int getFeedingValue()
    {
        return FEEDING_VALUE;
    }

    @Override
    protected AnimalSpecies getSpecies()
    {
        return AnimalSpecies.SHEEP;
    }
}
