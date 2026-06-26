/**
 * A simple model of a wolf.
 * Wolves age, move, eat prey,contract diseases and die.
 *
 * @version 2022.03.02
 */
public class Wolf extends Animal
{
    // Characteristics shared by all wolves (class variables).
    private static final AnimalTraits TRAITS = new AnimalTraits(
            15,
            150,
            0.3,
            2,
            15,
            6,
            Deer.class,
            Mouse.class,
            Coyote.class);

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the wolf.
     */
    public Wolf(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex, TRAITS);
        this.isNocturnal = true;
    }

    /**
     * Create a wolf with a custom movement strategy.
     */
    public Wolf(boolean randomAge, Field field, Location location, Gender sex, MovementStrategy movementStrategy)
    {
        super(field, location, randomAge, sex, TRAITS, movementStrategy);
        this.isNocturnal = true;
    }

    /**
     * Create a newborn wolf.
     */
    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Wolf(false, field, location, sex);
    }



}
