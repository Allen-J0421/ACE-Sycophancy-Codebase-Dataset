/**
 * Plant implementation backed by shared species metadata.
 */
public abstract class SpeciesPlant extends Plant
{
    private final PlantSpecies species;

    public SpeciesPlant(PlantSpecies species, boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, species.getMaxAge());
        this.species = species;
    }

    @Override
    protected final PlantSpecies getSpecies()
    {
        return species;
    }

    @Override
    protected final int getMaxAge()
    {
        return species.getMaxAge();
    }

    @Override
    protected final double getSpreadProbability(Weather weather)
    {
        return species.getSpreadProbability(weather);
    }
}
