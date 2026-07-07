/**
 * Handles movement logic for animals: falls back to a free adjacent location when
 * no food was found, then either relocates the animal or marks it dead from overcrowding.
 *
 * @version 1.0
 */
public class MovementHandler
{

    /*///////////////////////////////////////////////////////////////
                                 STATE
    //////////////////////////////////////////////////////////////*/

    private final Field field;

    /*///////////////////////////////////////////////////////////////
                              CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a new MovementHandler.
     *
     * @param field The field used to resolve free adjacent locations.
     */
    public MovementHandler(Field field)
    {
        this.field = field;
    }

    /*///////////////////////////////////////////////////////////////
                            MOVEMENT LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Attempts to move an animal to the given location. If that location is null
     * (no food was found), falls back to any free adjacent location. If neither
     * is available the animal dies from overcrowding.
     *
     * @param animal       The animal to move.
     * @param foodLocation The location of consumed food, or null if none was found.
     */
    public void moveOrDie(Animal animal, Location foodLocation)
    {
        Location newLocation = foodLocation;
        if(newLocation == null) {
            newLocation = field.freeAdjacentLocation(animal.getLocation());
        }
        if(newLocation != null) {
            animal.setLocation(newLocation);
        }
        else {
            animal.setDead();
        }
    }
}
