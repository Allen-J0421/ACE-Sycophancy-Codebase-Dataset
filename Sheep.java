import java.util.EnumSet;
import java.util.Set;
/**
 * A minimalist implementation of a Sheep, a sheep can only eat plants and not animals,
 * only the act method is unique to the sheep.
 *
 * @version 1.0
 */
public class Sheep extends SpeciesHerbivoreAnimal
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
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
        super(AnimalSpecies.SHEEP, randomAge, field, location, gender);
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
    protected Set<PlantSpecies> getTargetPlants()
    {
        return TARGET_PLANTS;
    }
}
