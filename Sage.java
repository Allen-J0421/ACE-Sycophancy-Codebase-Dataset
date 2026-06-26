/**
 * Implementation of a sage plant, sage's lifecycle consists of spreading, growing and getting eaten
 *
 * @version 1.0
 */
public class Sage extends SpeciesPlant
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    public Sage(boolean randomAge, Field field, Location location)
    {
        super(PlantSpecies.SAGE, randomAge, field, location);
    }
}
