import java.util.List;

/**
 * A class representing additional characteristics for predators.
 * Inlucdes an additional method for carnivores which allow them to step
 * on the grass without eating it.
 *
 * @version 2022.03.01
 */
public abstract class Predator extends Animal
{
    /**
     * Create a new predator at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Predator(Field field, Location location){
        super(field, location);
    }

    /**
     * Hunts for prey in adjacent cells. If prey is found, returns its location.
     * If no prey is found, returns the location of the nearest plant to stand on (without eating it).
     * @param probability The probability the animal finds prey.
     * @return Where to move, or null if nowhere suitable was found.
     */
    protected Location findFood(double probability)
    {
        Location preyLocation = super.findFood(probability);
        if(preyLocation != null) {
            return preyLocation;
        }
        Field field = getField();
        for(Location where : field.adjacentLocations(getLocation())) {
            if(field.getObjectAt(where) instanceof Plants){
                return where;
            }
        }
        return null;
    }
}
