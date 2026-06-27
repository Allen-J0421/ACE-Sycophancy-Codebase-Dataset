/**
 * A simple model of a shark.
 *
 * Sharks age, move, eat cod or salmon, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Shark extends ForagingAnimal
{
    private static final ForagingBehavior BEHAVIOR = new ForagingBehavior(
        6, 150, 0.4, 8, 8, 1, Cod.class, 2, false, Cod.class, Salmon.class);

    public Shark(boolean randomAge, Field field, Location location)
    {
        super(BEHAVIOR, randomAge, field, location);
    }

    @Override
    protected Creature createOffspring(Field field, Location location)
    {
        return new Shark(false, field, location);
    }
}
