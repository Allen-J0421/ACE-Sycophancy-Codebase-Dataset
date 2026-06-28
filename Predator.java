import java.util.Iterator;
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
     *
     */public Predator(Field field, Location location){
        super(field, location);
    }

    /**
     * If food is not found then the animal moves towards the nearest plant.
     * It does not eat the plant, instead stands on top of it.
     * @param probability The probability the animal finds prey.
     * @return Where plant was found, or null if it wasn't.
     */
    protected Location findFood(double probability)
    {
        if (super.findFood(probability) == null){
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object actor = field.getObjectAt(where);
                if(actor != null && actor  instanceof Plants){
                    return where;
                }
            }
        }
        return null;
    }
}
