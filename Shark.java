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
    // The food value of a single rabbit. In effect, this is the
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
        super(randomAge, field, location, MAX_AGE, COD_FOOD_VALUE,
              BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE, false);
    }

    /**
     *  Decide whether two sharks have different sex.
     *  @return true if two sharks have different sex, false otherwise.
     */
    public boolean encounterWithDiffSex(){
        return encounterWithDiffSex(Cod.class, 2);
    }

    protected Animal createYoung(Field field, Location location){
        return new Shark(false, field, location);
    }

    protected int getFoodValue(Object creature){
        if(creature instanceof Cod)
            return COD_FOOD_VALUE;
        if(creature instanceof Salmon)
            return SALMON_FOOD_VALUE;
        return 0;
    }

}
