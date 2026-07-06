import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Stateless helper that encapsulates the birth-count computation so that
 * organisms do not need to own this logic themselves.
 *
 * @version 2022.03.02
 */
public class ReproductiveStrategy {

    private static final Random rand = Randomizer.getRandom();

    private ReproductiveStrategy() {}

    /**
     * Computes the number of offspring produced this step.
     *
     * @param profile  The breeding parameters for the reproducing organism.
     * @param canBreed Whether the organism currently meets eligibility conditions.
     * @return The number of offspring to spawn (0 if conditions are not met).
     */
    public static int computeBirths(BreedingProfile profile, boolean canBreed) {
        if (canBreed && rand.nextDouble() <= profile.getBreedingProbability()) {
            return rand.nextInt(profile.getMaxLitterSize()) + 1;
        }
        return 0;
    }
}
