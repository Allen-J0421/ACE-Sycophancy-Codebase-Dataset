import java.util.List;
import java.util.Iterator;

/**
 * A carnivorous animal that hunts other animals (Cod and Salmon).
 *
 * Predators share the same feeding behaviour and, unlike herbivores, breed on
 * reaching breeding age without needing to find a mate. Concrete predators
 * (Shark, Whale) supply only their own life-cycle constants.
 *
 * @version 2022/03/02
 */
public abstract class Predator extends Animal
{
    /**
     * Create a predator.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge If true, the predator will have a random age and food level.
     */
    public Predator(Field field, Location location, boolean randomAge)
    {
        super(field, location, randomAge);
    }

    /**
     * Predators breed on reaching breeding age; they do not seek a mate.
     * @return false.
     */
    protected boolean requiresMate()
    {
        return false;
    }

    /**
     * Look for Cod and Salmon adjacent to the current location.
     * Only the first live Cod or Salmon is eaten; if a nearby animal is
     * infected, then this animal also may be infected.
     * @param disease disease.
     * @param step int current step.
     * @return Where food was found, or null if it wasn't.
     */
    public Location search(Disease disease, int step){
        Field field = getField();
        //trying to find food.
        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location loc = it.next();
            Object creature = field.getObjectAt(loc);
            //If nearby animal is infected,then it has the probability to be infected as well
            if(creature instanceof Animal){
                Animal animal = (Animal)creature;
                if(animal.getIsInfected()){
                    makeInfected(disease, step);
                }
            }
            // if food is found, set the food death.
            if(creature instanceof Cod || creature instanceof Salmon) {
                Animal animal = (Animal) creature;
                if(animal.isAlive()) {
                    animal.setDead();
                    foodLevel = getMaxFoodValue();
                    return loc;
                }
            }
        }
        return null;
    }
}
