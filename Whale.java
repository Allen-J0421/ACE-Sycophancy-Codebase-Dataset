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
    // The food value of a single Cod. In effect, this is the
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
        super(randomAge, field, location, MAX_AGE, COD_FOOD_VALUE,
              BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE, false);
    }

    /**
     * Decide whether two whales have different sex.
     * @return true if two whales have different sex, false otherwise.
     */
    public boolean encounterWithDiffSex(){
        return encounterWithDiffSex(2);
    }

    protected Animal createYoung(Field field, Location location){
        return new Whale(false, field, location);
    }

    protected int getFoodValueFrom(Cod cod){
        return COD_FOOD_VALUE;
    }

    protected int getFoodValueFrom(Salmon salmon){
        return SALMON_FOOD_VALUE;
    }

    protected boolean canMateWith(Salmon salmon){
        return true;
    }

}
