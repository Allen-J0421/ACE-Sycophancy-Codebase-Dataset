/**
 * A simple model of a Salmon.
 *
 * Salmons age, move, eat seaweed, consume oxygen, propagate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Salmon extends Animal
{
    private static final AnimalProfile PROFILE =
        new AnimalProfile(Salmon.class, 50, 4, 0.3, 15, 13, 2, true,
            (field, location) -> new Salmon(false, field, location), Seaweed.class);

    public Salmon(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE);
    }
}
