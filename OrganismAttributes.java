import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Shared lifecycle settings for an organism species.
 *
 * @version 2022.03.02
 */
public class OrganismAttributes
{
    private final Species species;
    private final boolean diurnal;
    private final int breedingAge;
    private final int maxAge;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final OrganismFactory factory;
    private final BreedingStrategy breedingStrategy;
    private final RelocationStrategy relocationStrategy;
    private final List<OrganismActionCommand> actionCommands;

    public OrganismAttributes(Species species, boolean diurnal, int breedingAge,
                              int maxAge, double breedingProbability,
                              int maxLitterSize, OrganismFactory factory,
                              BreedingStrategy breedingStrategy,
                              RelocationStrategy relocationStrategy,
                              OrganismActionCommand... actionCommands)
    {
        this.species = species;
        this.diurnal = diurnal;
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.factory = factory;
        this.breedingStrategy = breedingStrategy;
        this.relocationStrategy = relocationStrategy;
        this.actionCommands = Collections.unmodifiableList(Arrays.asList(actionCommands));
    }

    public Species getSpecies()
    {
        return species;
    }

    public boolean isDiurnal()
    {
        return diurnal;
    }

    public int getBreedingAge()
    {
        return breedingAge;
    }

    public int getMaxAge()
    {
        return maxAge;
    }

    public double getBreedingProbability()
    {
        return breedingProbability;
    }

    public int getMaxLitterSize()
    {
        return maxLitterSize;
    }

    public Organism create(boolean randomAge, Field field, Location location,
                           SimulationEventBus eventBus)
    {
        return factory.create(randomAge, field, location, eventBus);
    }

    public BreedingStrategy getBreedingStrategy()
    {
        return breedingStrategy;
    }

    public RelocationStrategy getRelocationStrategy()
    {
        return relocationStrategy;
    }

    public List<OrganismActionCommand> getActionCommands()
    {
        return actionCommands;
    }
}
