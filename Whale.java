import java.util.List;

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
    // The food value of each prey animal.
    private static final int FOOD_VALUE = 8;

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
        super(field, location);
        initAge(randomAge);
        initFoodLevel(randomAge);
    }

    public int getMaxAge() { return MAX_AGE; }
    public int getBreedingAge() { return BREEDING_AGE; }
    public double getBreedingProbability() { return BREEDING_PROBABILITY; }
    public int getMaxLitterSize() { return MAX_LITTER_SIZE; }
    public int getMaxFoodValue() { return FOOD_VALUE; }
    public Animal createOffspring(Field field, Location location) { return new Whale(false, field, location); }

    protected int tryEat(Object creature) {
        if (creature instanceof Cod || creature instanceof Salmon) {
            Animal prey = (Animal) creature;
            if (prey.isAlive()) {
                prey.setDead();
                return FOOD_VALUE;
            }
        }
        return -1;
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
