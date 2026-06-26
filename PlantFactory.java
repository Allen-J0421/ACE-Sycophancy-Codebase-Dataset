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
        return getPlant(PlantType.fromString(plantType), location);
    }

    /**
     * Creates a Plant given an input plant type.
     *
     * @param plantType the type of the plant.
     * @param location The base location of the newly created plant.
     * @return the created plant.
     */
    public Plant getPlant(PlantType plantType, Location location) {
        if(plantType == null) {
            return null;
        }
        return plantType.createForPopulation(field, location);
    }
}
