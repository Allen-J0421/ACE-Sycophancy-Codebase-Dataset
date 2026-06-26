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
        super(randomAge, field, location, AnimalSpecies.SALMON);
    }

    protected int getFoodValueFor(Animal animal){
        return animal.getFoodValueFrom(this);
    }

    protected boolean isPotentialMateFor(Animal animal){
        return animal.canMateWith(this) && hasDifferentSex(animal);
    }

}
