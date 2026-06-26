/**
 * Implementation of a grass plant, the lifecycle of grass consists of spreading, growing and getting eaten
 *
 * @version 1.0
 */
public class Grass extends SpeciesPlant
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(PlantSpecies.GRASS, randomAge, field, location);
    }
}
