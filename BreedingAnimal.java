import java.util.List;

/**
 * Shared breeding behavior for animals that create offspring.
 */
public abstract class BreedingAnimal extends Animal
{
    private final BreedingProfile breedingProfile;

    /**
     * Create a breeding animal.
     */
    protected BreedingAnimal(boolean randomAge, Field field, Location location,
                             AnimalProfile animalProfile, BreedingProfile breedingProfile)
    {
        super(randomAge, field, location, animalProfile);
        this.breedingProfile = breedingProfile;
    }

    /**
     * Try to add offspring for this step.
     */
    protected final void breed(List<Organism> newOrganisms)
    {
        if(canBreedThisStep()) {
            addOffspring(newOrganisms,
                         calculateBirths(breedingProfile.breedingAge(),
                                         breedingProfile.breedingProbability(),
                                         breedingProfile.maxLitterSize()),
                         this::createYoung);
        }
    }

    /**
     * Create a newborn organism of this species.
     */
    protected abstract Animal createYoung(Field field, Location location);

    /**
     * Standard breeding animals only breed during the alive-step phase.
     */
    @Override
    protected void handleAliveStep(List<Organism> newOrganisms, SimulationStep step)
    {
        breed(newOrganisms);
    }

    /**
     * Determine whether this animal can breed this step.
     */
    private boolean canBreedThisStep()
    {
        if(!breedingProfile.requiresMate()) {
            return true;
        }
        return hasAdjacentMate(getClass(), breedingProfile.mateSearchRadius());
    }
}
