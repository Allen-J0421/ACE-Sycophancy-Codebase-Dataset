/**
 * A simple model of a cod.
 *
 * Cods age, move, eat seaweed, consume oxygen, propagate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Cod extends Animal
{
    private static final AnimalProfile PROFILE =
        new AnimalProfile(Cod.class, 50, 6, 0.3, 10, 13, 1, true,
            (field, location) -> new Cod(false, field, location), Seaweed.class);

    public Cod(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE);
    }
}
