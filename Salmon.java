import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a Salmon.
 *
 * Salmons age, move, eat seaweed, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Salmon extends Animal
{
    // Characteristics shared by all salmons (class variables).

    // The age at which a salmon can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a salmon can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a salmon breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 15;

    // The food value of a single salmon. In effect, this is the
    // number of steps a salmon can go before it has to eat again.
    private static final int SEAWEED_FOOD_VALUE = 13;

    /**
     * Create a new salmon. A salmon may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the salmon will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Salmon(boolean randomAge, Field field, Location location)
    {
        super(field, location, randomAge);
    }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getMaxFoodValue() { return SEAWEED_FOOD_VALUE; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected boolean requiresMate() { return true; }

    protected Animal createChild(Field field, Location location)
    {
        return new Salmon(false, field, location);
    }

    /**
     * Look for seaweed adjacent to the current location.
     * Only the first live seaweed is eaten, if the nearby animal is
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
                    foodLevel = SEAWEED_FOOD_VALUE;
                    return loc;
                }
            }
        }
        return null;


    }

    /**
     * Decide if two salmons countered has different sex;
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
