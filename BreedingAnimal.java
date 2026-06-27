import java.util.List;

/**
 * Shared breeding behavior for animals that create offspring.
 */
public abstract class BreedingAnimal extends Animal
{
    /**
     * Create a breeding animal.
     */
    protected BreedingAnimal(Field field, Location location)
    {
        super(field, location);
    }

    /**
     * Try to add offspring for this step.
     */
    protected final void breed(List<Organism> newOrganisms)
    {
        if(canBreedThisStep()) {
            addOffspring(newOrganisms,
                         calculateBirths(getBreedingAge(), getBreedingProbability(), getMaxLitterSize()),
                         this::createYoung);
        }
    }

    /**
     * @return The breeding age for this species.
     */
    protected abstract int getBreedingAge();

    /**
     * @return The breeding probability for this species.
     */
    protected abstract double getBreedingProbability();

    /**
     * @return The maximum litter size for this species.
     */
    protected abstract int getMaxLitterSize();

    /**
     * Create a newborn organism of this species.
     */
    protected abstract Animal createYoung(Field field, Location location);

    /**
     * Determine whether this species requires a nearby mate to breed.
     */
    protected boolean requiresMate()
    {
        return false;
    }

    /**
     * @return The mate search radius when a mate is required.
     */
    protected int getMateSearchRadius()
    {
        return 1;
    }

    /**
     * Determine whether this animal can breed this step.
     */
    private boolean canBreedThisStep()
    {
        if(!requiresMate()) {
            return true;
        }
        return hasAdjacentMate(getClass(), getMateSearchRadius());
    }
}
