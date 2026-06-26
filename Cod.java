/**
 * A simple model of a cod.
 * 
 * Cods age, move, eat seaweed, consume oxygen, propogate, 
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Cod extends Animal
{
    // Characteristics shared by all cods (class variables).

    // The age at which a cod can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a cod can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a cod breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;

    // The food value of a cod. In effect, this is the
    // number of steps a cod can go before it has to eat again.
    private static final int SEAWEED_FOOD_VALUE = 13;

    /**
     * Create a new cod. A cod may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the cod will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cod(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, MAX_AGE, SEAWEED_FOOD_VALUE,
              BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE, true, 1,
              (birthField, birthLocation) -> new Cod(false, birthField, birthLocation));
    }

    protected int getFoodValueFor(Animal animal){
        return animal.getFoodValueFrom(this);
    }

    protected int getFoodValueFrom(Seaweed seaweed){
        return SEAWEED_FOOD_VALUE;
    }

    protected boolean isPotentialMateFor(Animal animal){
        return animal.canMateWith(this) && hasDifferentSex(animal);
    }

    protected boolean canMateWith(Cod cod){
        return true;
    }

}
