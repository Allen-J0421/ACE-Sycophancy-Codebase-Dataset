import java.util.List;

/**
 * A simple model of a mouse.
 * Mice age, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Mouse extends Animal
{
    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field field, Location location)
    {
        super(SpeciesRegistry.get("Mouse"), randomAge, field, location);
    }

    /**
     * Look for grass adjacent to the current location, eating it only if the
     * hunt succeeds.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(List<String> preyList)
    {
        if (huntSucceeds()) {
            super.findFood(getPrey());
        }
        return null;
    }

    /**
     * Create a new-born mouse.
     */
    protected Animal createOffspring(Field field, Location location)
    {
        return new Mouse(false, field, location);
    }
}
