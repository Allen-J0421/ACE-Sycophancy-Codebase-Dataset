/**
 * A simple model of a mouse.
 * Mice can age, move, contract diseases, eat plants and die. 
 *
 * @version 2022.03.02
 */
public class Mouse extends Animal
{
    // Characteristics shared by all Mice (class variables).
    private static final AnimalTraits TRAITS = new AnimalTraits(
            5,
            40,
            0.5,
            4,
            9,
            2,
            Grass.class);

    
    /**
     * Create a new Mouse. A Mouse may be created with age
     * zero (a new born) or with a random age.
     * @param randomAge If true, the Mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the mouse.
     */
    public Mouse(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex, TRAITS);
        this.isNocturnal = false;
    }


    /**
     * Create a newborn mouse.
     */
    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Mouse(false, field, location, sex);
    }


}
