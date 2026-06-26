/**
 * Implementation of a sedge plant, sedge's lifecycle consists of spreading, growing and getting eaten
 *
 * @version 1.0
 */
public class Sedge extends SpeciesPlant
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    public Sedge(boolean randomAge, Field field, Location location)
    {
        super(PlantSpecies.SEDGE, randomAge, field, location);
    }
}
