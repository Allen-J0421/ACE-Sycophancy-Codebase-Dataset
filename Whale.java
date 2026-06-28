import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a whale.
 *
 * Whales age, move, eat cod or salmon, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Whale extends Animal
{
    // Characteristics shared by all whales (class variables).

    // The age at which a whale can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a whale can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a whale breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single prey. In effect, this is the
    // number of steps a whale can go before it has to eat again.
    private static final int COD_FOOD_VALUE = 8;
    private static final int SALMON_FOOD_VALUE = 8;

    /**
     * Create a whale. A whale can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the whale will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Whale(boolean randomAge, Field field, Location location)
    {
        super(field, location, randomAge);
    }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getMaxFoodValue() { return COD_FOOD_VALUE; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected boolean requiresMate() { return false; }

    protected Animal createChild(Field field, Location location)
    {
        return new Whale(false, field, location);
    }

    /**
     * Look for Cods and Salmon adjacent to the current location.
     * Only the first live Cod or Salmon is eaten, if the nearby animal is
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
                    if(animal instanceof Cod)
                        foodLevel = COD_FOOD_VALUE;
                    else
                        foodLevel = SALMON_FOOD_VALUE;
                    return loc;
                }
            }
        }
        return null;

    }

    /**
     * Decide whether two whales have different sex.
     * @return true if two whales have different sex, false otherwise.
     */
    public boolean encounterWithDiffSex(){

        List<Location> adjacentLocation = getField().adjacentLocations(getLocation(), 2);

        for(Location loc: adjacentLocation){
            Object creatureAtThisLoc = getField().getObjectAt(loc);
            if(creatureAtThisLoc != null && creatureAtThisLoc instanceof Salmon){
                Salmon salmonAtThisLoc = (Salmon)creatureAtThisLoc;
                if(this.getSex() != salmonAtThisLoc.getSex())
                    return true;
            }
        }
        return false;
    }

}
