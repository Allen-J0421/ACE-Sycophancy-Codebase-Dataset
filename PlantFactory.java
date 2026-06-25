/**
 * Plant factory for creating objects that extend the Plant class.
 *
 * @version 1.0
 */
public class PlantFactory
{
    
    /*///////////////////////////////////////////////////////////////
                                STATE
    //////////////////////////////////////////////////////////////*/
    
    private Field field;
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates a Plant Factory.
     * 
     * @param field reference to field to later pass in to plants.
     */
    public PlantFactory(Field field)
    {
        this.field = field;
    }
    
    /**
     * Creates a Plant given an input plant type.
     * 
     * @param plantType the type of the plant.
     * @param location The base location of the newly created plant.
     * @return the created plant.
     */
    public Plant getPlant(String plantType, Location location) {
        if(plantType == null) {
            return null;
        }
        if(plantType.equalsIgnoreCase("GRASS")) {
            return new Grass(true, field, location);
        } else if (plantType.equalsIgnoreCase("SAGE")) {
            return new Sage(true, field, location);
        } else if(plantType.equalsIgnoreCase("SEDGE")) {
            return new Sedge(true, field , location);
        } 
        return null;
    }
}
