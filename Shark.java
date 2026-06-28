import java.util.List;
import java.util.Iterator;

/**
 ** A simple model of a shark.
 *
 * Sharks age, move, eat cod or salmon, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Shark extends Animal
{
    // Characteristics shared by all sharkes (class variables).

    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a shark can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single prey. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static final int COD_FOOD_VALUE = 8;
    private static final int SALMON_FOOD_VALUE = 8;

    /**
     * Create a shark. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location)
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
        return new Shark(false, field, location);
    }

    /**
     * Look for Cod and Salmon adjacent to the current location.
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
     *  Decide whether two sharks have different sex.
     *  @return true if two sharks have different sex, false otherwise.
     */
    public boolean encounterWithDiffSex(){

        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 2);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animalAtThisLoc = field.getObjectAt(where);
            if(animalAtThisLoc != null && animalAtThisLoc instanceof Cod){
                Cod codAtThisLoc = (Cod)animalAtThisLoc;
                if(this.getSex() != codAtThisLoc.getSex())
                    return true;
            }
        }
        return false;
    }

}
