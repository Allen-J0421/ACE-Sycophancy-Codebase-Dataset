import java.util.List;

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
    // number of steps a fox can go before it has to eat again.
    private static final int SEAWEED_FOOD_VALUE = 13;

    // Individual characteristics (instance fields).


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
        super(field, location);
        initAge(randomAge);
        initFoodLevel(randomAge);
    }

    public boolean requiresMate() { return true; }
    public int getMaxAge() { return MAX_AGE; }
    public int getBreedingAge() { return BREEDING_AGE; }
    public double getBreedingProbability() { return BREEDING_PROBABILITY; }
    public int getMaxLitterSize() { return MAX_LITTER_SIZE; }
    public int getMaxFoodValue() { return SEAWEED_FOOD_VALUE; }
    public Animal createOffspring(Field field, Location location) { return new Salmon(false, field, location); }

    protected int tryEat(Object creature) {
        if (creature instanceof Seaweed) {
            Seaweed seaweed = (Seaweed) creature;
            if (seaweed.isAlive()) {
                seaweed.setDead();
                return SEAWEED_FOOD_VALUE;
            }
        }
        return -1;
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
