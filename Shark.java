/**
 * A simple model of a shark.
 *
 * Sharks age, move, eat cod or salmon, consume oxygen, propagate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Shark extends Animal
{
    private static final AnimalProfile PROFILE =
        new AnimalProfile(Shark.class, 150, 6, 0.4, 8, 8, 2, false,
            (field, location) -> new Shark(false, field, location), Cod.class, Salmon.class);

    public Shark(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE);
    }
}
