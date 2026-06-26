/**
 * A simple model of a deer.
 * Deers age, move, breed,contract diseases, eat plants and die.
 *
 * @version 2022.03.02
 */
public class Deer extends Animal
{
    // Characteristics shared by all deers (class variables).
    private static final AnimalTraits TRAITS = new AnimalTraits(
            5,
            40,
            0.5,
            2,
            9,
            4,
            Grass.class);

    /**
     * Create a new deer. A deer may be created with age
     * zero (a new born) or with a random age.
     * @param randomAge If true, the deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the deer.
     */
    public Deer(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex, TRAITS);
        this.isNocturnal = false;
    }

    /**
     * Create a deer with a custom movement strategy.
     */
    public Deer(boolean randomAge, Field field, Location location, Gender sex, MovementStrategy movementStrategy)
    {
        super(field, location, randomAge, sex, TRAITS, movementStrategy);
        this.isNocturnal = false;
    }


    /**
     * Makes the deer stay awake regardless of the time of the day
     * Overrides the method in the Animal class that uses the isNocturnal field to determine if it is awake
     */
    @Override
    public boolean isAwake(Environment environment) {
        return true;
    }

    /**
     * Create a newborn deer.
     */
    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Deer(false, field, location, sex);
    }
}
