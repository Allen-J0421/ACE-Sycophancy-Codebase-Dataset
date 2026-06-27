/**
 * A simple model of a Salmon.
 *
 * Salmons age, move, eat seaweed, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Salmon extends ForagingAnimal
{
    private static final ForagingBehavior BEHAVIOR = new ForagingBehavior(
        4, 50, 0.3, 15, 13, 1, Salmon.class, 2, true, Seaweed.class);

    public Salmon(boolean randomAge, Field field, Location location)
    {
        super(BEHAVIOR, randomAge, field, location);
    }

    @Override
    protected Creature createOffspring(Field field, Location location)
    {
        return new Salmon(false, field, location);
    }
}
