/**
 * Factory for creating any animal species given an AnimalType.
 *
 * @version 2.0
 */
public class AnimalFactory
{
    private final Field field;

    public AnimalFactory(Field field)
    {
        this.field = field;
    }

    /**
     * Creates and returns an animal of the given type at the given location.
     *
     * @param type The species to create.
     * @param location The initial location of the animal.
     * @return the newly created animal.
     */
    public Animal getAnimal(AnimalType type, Location location)
    {
        Gender gender = Utils.getRandomEnumValue(Gender.class);
        switch(type) {
            case FOX:       return new CarnivoreFox(true, field, location, gender);
            case BEAR:      return new Bear(true, field, location, gender);
            case WOLVERINE: return new Wolverine(true, field, location, gender);
            case SHEEP:     return new Sheep(true, field, location, gender);
            case REINDEER:  return new Reindeer(true, field, location, gender);
            default: throw new IllegalArgumentException("Unknown animal type: " + type);
        }
    }
}
