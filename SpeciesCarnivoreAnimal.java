/**
 * Carnivore implementation backed by shared species metadata.
 */
public abstract class SpeciesCarnivoreAnimal extends CarnivoreAnimal
{
    private final AnimalSpecies species;

    public SpeciesCarnivoreAnimal(
        AnimalSpecies species,
        boolean randomAge,
        Field field,
        Location location,
        Gender gender
    ) {
        super(randomAge, field, location, gender, species.getBaseHungerLevel(), species.getMaxAge());
        this.species = species;
    }

    @Override
    protected final int getMaxAge()
    {
        return species.getMaxAge();
    }

    @Override
    protected final int getBreedingAge()
    {
        return species.getBreedingAge();
    }

    @Override
    protected final double getBreedingProbability(Weather weather)
    {
        return species.getBreedingProbability(weather);
    }

    @Override
    protected final int getMaxLitterSize()
    {
        return species.getMaxLitterSize();
    }

    @Override
    protected boolean canAct(Weather weather, DayState dayState)
    {
        return species.canActDuring(dayState);
    }

    @Override
    public final int getFeedingValue()
    {
        return species.getFeedingValue();
    }

    @Override
    protected final AnimalSpecies getSpecies()
    {
        return species;
    }
}
