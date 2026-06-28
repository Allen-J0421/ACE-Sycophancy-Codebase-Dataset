import java.util.List;
import java.util.Iterator;

/**
 * A plant-eating animal that grazes on Seaweed.
 *
 * Herbivores share the same feeding behaviour and, unlike predators, need an
 * adjacent mate of the opposite sex in order to breed. Concrete herbivores
 * (Cod, Salmon) supply their own life-cycle constants and decide how they
 * recognise a mate via {@link #encounterWithDiffSex}.
 *
 * @version 2022/03/02
 */
public abstract class Herbivore extends Animal
{
    /**
     * Create a herbivore.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge If true, the herbivore will have a random age and food level.
     */
    public Herbivore(Field field, Location location, boolean randomAge)
    {
        super(field, location, randomAge);
    }

    /**
     * Herbivores need an adjacent mate of the opposite sex to breed.
     * @return true.
     */
    protected boolean requiresMate()
    {
        return true;
    }

    /**
     * Look for seaweed adjacent to the current location.
     * Only the first live seaweed is eaten; if a nearby animal is
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
            if(creature instanceof Seaweed) {
                Seaweed seaweed = (Seaweed) creature;
                if(seaweed.isAlive()) {
                    seaweed.setDead();
                    foodLevel = getMaxFoodValue();
                    return loc;
                }
            }
        }
        return null;
    }
}
