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
    // The food value of each prey animal.
    private static final int FOOD_VALUE = 8;

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
        super(field, location);

        initAge(randomAge);
        initFoodLevel(randomAge);

    }

    public int getMaxAge() { return MAX_AGE; }
    public int getBreedingAge() { return BREEDING_AGE; }
    public double getBreedingProbability() { return BREEDING_PROBABILITY; }
    public int getMaxLitterSize() { return MAX_LITTER_SIZE; }
    public int getMaxFoodValue() { return FOOD_VALUE; }
    public Animal createOffspring(Field field, Location location) { return new Shark(false, field, location); }

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
