/**
 * A simple model of a coyote.
 * Coyotes age, move, eat prey,contract diseases and die.
 * @version 2022.03.3
 */
public class Coyote extends Animal
{
    // Characteristics shared by all coyotes (class variables).
    private static final AnimalTraits TRAITS = new AnimalTraits(
            15,
            150,
            0.4,
            2,
            15,
            5,
            Deer.class,
            Mouse.class);

    /**
     * Create a coyote. A coyote can be created as a new-born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the coyote will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the coyote.
     */
    public Coyote(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex, TRAITS);
        this.isNocturnal = true;
    }

    /**
     * Create a coyote with a custom movement strategy.
     */
    public Coyote(boolean randomAge, Field field, Location location, Gender sex, MovementStrategy movementStrategy)
    {
        super(field, location, randomAge, sex, TRAITS, movementStrategy);
        this.isNocturnal = true;
    }

    /**
     * Create a coyote with a custom reproduction strategy.
     */
    public Coyote(boolean randomAge, Field field, Location location, Gender sex, ReproductionStrategy reproductionStrategy)
    {
        super(field, location, randomAge, sex, TRAITS, reproductionStrategy);
        this.isNocturnal = true;
    }

    /**
     * Create a newborn coyote.
     */
    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Coyote(false, field, location, sex);
    }

}
