/**
 * Factory for creating any plant species given a PlantType.
 *
 * @version 2.0
 */
public class PlantFactory
{
    private final Field field;

    public PlantFactory(Field field)
    {
        this.field = field;
    }

    /**
     * Creates and returns a plant of the given type at the given location.
     *
     * @param type The species to create.
     * @param location The initial location of the plant.
     * @return the newly created plant.
     */
    public Plant getPlant(PlantType type, Location location)
    {
        switch(type) {
            case GRASS: return new Grass(true, field, location);
            case SAGE:  return new Sage(true, field, location);
            case SEDGE: return new Sedge(true, field, location);
            default: throw new IllegalArgumentException("Unknown plant type: " + type);
        }
    }
}
