/**
 * A simple model of a whale.
 *
 * Whales age, move, eat cod or salmon, consume oxygen, propagate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Whale extends Animal
{
    private static final AnimalProfile PROFILE =
        new AnimalProfile(Whale.class, 150, 6, 0.2, 8, 8, 2, false,
            (field, location) -> new Whale(false, field, location), Cod.class, Salmon.class);

    public Whale(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE);
    }
}
