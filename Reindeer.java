import java.util.EnumSet;
import java.util.Set;
/**
 * A minimalist implementation of a Reindeer, a reindeer can only eat plants and not animals,
 * only the act method is unique to the reindeer.
 *
 * @version 1.0
 */
public class Reindeer extends SpeciesHerbivoreAnimal
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    private static final Set<PlantSpecies> TARGET_PLANTS =
        EnumSet.of(PlantSpecies.GRASS, PlantSpecies.SAGE, PlantSpecies.SEDGE);
    
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
        super(AnimalSpecies.REINDEER, randomAge, field, location, gender);
    }
    
    /*///////////////////////////////////////////////////////////////
                            ANIMAL BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/   
    
    /**
     * Method in charge of the reindeer's action's during a step. During a step,
     * a reindeer will age, increase in hunger, seek to mate as well as look for food.
     * 
     * @param newReindeers the new reeindeers to be born in case the reindeer succesfully mates
     */
    protected Set<PlantSpecies> getTargetPlants()
    {
        return TARGET_PLANTS;
    }
}
