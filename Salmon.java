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
        super(randomAge, field, location, MAX_AGE, SEAWEED_FOOD_VALUE,
              BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE, true);
    }

    
    /**
     * Decide if two salmons countered has different sex;
     */
    public boolean encounterWithDiffSex(){
        return encounterWithDiffSex(2);
    }

    protected Animal createYoung(Field field, Location location){
        return new Salmon(false, field, location);
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

    protected boolean canMateWith(Salmon salmon){
        return true;
    }

}
