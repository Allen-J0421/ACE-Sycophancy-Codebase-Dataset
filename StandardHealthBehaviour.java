/**
 * Standard biological health model shared by all animal species.
 * Implements the infection/immunity state machine that was previously
 * embedded in {@link Animal#act}.
 *
 * <p>Probability constants are the simulation-wide values established
 * in the original code; they are uniform across all species.</p>
 */
public class StandardHealthBehaviour implements HealthBehaviour
{
    private static final double DISEASE_PROBABILITY              = 0.00000015;
    private static final double DISEASE_SPREAD_PROBABILITY       = 0.80;
    private static final double DEATH_FROM_INFECTION_PROBABILITY = 0.13;
    private static final double IMMUNE_PROBABILITY               = 0.05;

    private boolean infected;
    private boolean immune;

    /**
     * Create a health component with the given initial state.
     *
     * @param infected Whether the animal starts infected.
     * @param immune   Whether the animal starts immune.
     */
    public StandardHealthBehaviour(boolean infected, boolean immune)
    {
        this.infected = infected;
        this.immune   = immune;
    }

    /**
     * {@inheritDoc}
     *
     * <p>If infected and not immune: may die or gain immunity this step.
     * Otherwise: immunity may wane. Preserves the exact RNG-consumption
     * order of the original {@code Animal.act()} infection block.</p>
     */
    @Override
    public boolean progressDisease(SimRandom rand)
    {
        if (!immune && infected)
        {
            if (rand.nextDouble() <= DEATH_FROM_INFECTION_PROBABILITY)
            {
                return true;
            }
            else if (rand.nextDouble() <= IMMUNE_PROBABILITY)
            {
                immune   = true;
                infected = false;
            }
        }
        else
        {
            if (rand.nextDouble() <= IMMUNE_PROBABILITY / 15)
            {
                immune = false;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>{@code self.surroundingsInfected()} is called only when the
     * animal is susceptible, matching the original short-circuit behaviour
     * and preserving RNG order.</p>
     */
    @Override
    public void tryAcquireDisease(Animal self, Field field, SimRandom rand)
    {
        if (!immune && !infected)
        {
            if (self.surroundingsInfected() && rand.nextDouble() <= DISEASE_SPREAD_PROBABILITY)
            {
                infected = true;
            }
            else if (rand.nextDouble() <= DISEASE_PROBABILITY)
            {
                infected = true;
            }
        }
    }

    @Override
    public void tryInfectFrom(boolean sourceIsInfected, SimRandom rand)
    {
        if (sourceIsInfected && !immune && rand.nextDouble() <= DISEASE_SPREAD_PROBABILITY)
        {
            infected = true;
        }
    }

    @Override public boolean isInfected() { return infected; }
    @Override public boolean isImmune()   { return immune; }
}
