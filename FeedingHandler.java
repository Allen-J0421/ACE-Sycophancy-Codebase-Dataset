import java.util.Iterator;
import java.util.List;
/**
 * Handles food-finding and consumption logic for both carnivores and herbivores.
 *
 * @version 1.0
 */
public class FeedingHandler
{

    /*///////////////////////////////////////////////////////////////
                                 STATE
    //////////////////////////////////////////////////////////////*/

    private final Field field;

    /*///////////////////////////////////////////////////////////////
                              CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a new FeedingHandler.
     *
     * @param field The field in which food is searched for.
     */
    public FeedingHandler(Field field)
    {
        this.field = field;
    }

    /*///////////////////////////////////////////////////////////////
                            FEEDING LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Searches adjacent locations for a prey animal belonging to the given diet list.
     * On success, kills the prey and replaces the eater's food level with the prey's feeding value.
     *
     * @param eater The animal searching for food.
     * @param preys The diet list of acceptable prey classes.
     * @return The location of the consumed prey, or null if none found.
     */
    public Location findAnimalFood(Animal eater, List<Class<? extends Animal>> preys)
    {
        List<Location> adjacent = field.adjacentLocations(eater.getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object obj = field.getObjectAt(where);
            if(obj instanceof Animal) {
                Animal prey = (Animal) obj;
                if(preys.contains(prey.getClass())) {
                    if(prey.isAlive()) {
                        prey.setDead();
                        eater.foodLevel = prey.getFeedingValue();
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Searches adjacent terrain for a plant belonging to the given diet list.
     * On success, kills the plant and adds its feeding value to the eater's food level.
     *
     * @param eater The animal searching for food.
     * @param targetPlants The diet list of acceptable plant classes.
     * @return The location of the consumed plant, or null if none found.
     */
    public Location findPlantFood(Animal eater, List<Class<? extends Plant>> targetPlants)
    {
        List<Location> adjacent = field.adjacentLocations(eater.getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Plant plant = field.getPlantAt(where);
            if(plant != null && targetPlants.contains(plant.getClass())) {
                if(plant.isAlive()) {
                    eater.foodLevel += plant.getFeedingValue();
                    plant.setDead();
                    return where;
                }
            }
        }
        return null;
    }
}
