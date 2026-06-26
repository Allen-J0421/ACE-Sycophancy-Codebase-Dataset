/**
 * A simple model of a eagle.
 * Eagles age, move,contract diseases, eat prey, and die.
 *
 * @version 2022.03.02
 */
public class Eagle extends Animal
{
     // Characteristics shared by all eagles (class variables).
    private static final AnimalTraits TRAITS = new AnimalTraits(
            15,
            150,
            0.4,
            2,
            14,
            5,
            Deer.class,
            Coyote.class,
            Mouse.class);

    /**
     * Create a eagle. A eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the eagle.
     */
    public Eagle(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex, TRAITS);
        this.isNocturnal = false;
    }

    /**
     * Create an eagle with a custom movement strategy.
     */
    public Eagle(boolean randomAge, Field field, Location location, Gender sex, MovementStrategy movementStrategy)
    {
        super(field, location, randomAge, sex, TRAITS, movementStrategy);
        this.isNocturnal = false;
    }

    /**
     * Create an eagle with a custom reproduction strategy.
     */
    public Eagle(boolean randomAge, Field field, Location location, Gender sex, ReproductionStrategy reproductionStrategy)
    {
        super(field, location, randomAge, sex, TRAITS, reproductionStrategy);
        this.isNocturnal = false;
    }

    
    /**
     * Create a newborn eagle.
     */
    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Eagle(false, field, location, sex);
    }

    /**
     * Eagles do not die from overcrowding because they fly above other actors.
     */
    @Override
    protected boolean diesFromOvercrowding()
    {
        return false;
    }


    /**
     * Additional functionality that doesn't allow eagles to find food while it's raining
     * @param environment The environment that the eagle resides in. 
     * @return Location Where food was found, or null if it wasn't.
     */
    @Override
    protected Location findFood(Environment environment)
    {
        if(environment.getWeather().getCurrentWeather() != WeatherType.RAINING) {
            return super.findFood(environment);
        }
        return null;
    }

}
