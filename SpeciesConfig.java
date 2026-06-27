/**
 * Central repository of tunable parameters for every species in the simulation.
 *
 * <p>This is the <em>single file to edit</em> when adjusting any species' behaviour.
 * No individual entity class needs to change when tuning simulation parameters.
 *
 * <p>Parameters are grouped by entity type using nested static classes. Named
 * constants at the bottom of this class (LION, DEER, etc.) are the single
 * instances consumed by each species constructor and by {@link EntityRegistry}.
 */
public class SpeciesConfig
{
    /**
     * Tunable parameters shared by all animal species.
     */
    public static class AnimalParams
    {
        /** Maximum age before the animal dies of old age. */
        public final int    maxAge;
        /** Minimum age at which the animal may breed. */
        public final int    breedingAge;
        /** Per-step probability of producing offspring when eligible. */
        public final double breedingProbability;
        /** Maximum number of offspring per successful breeding event. */
        public final int    maxLitterSize;
        /** Food energy granted to a predator when this animal is eaten. */
        public final int    foodValue;
        /** Upper bound on food level when the animal is spawned at a random age. */
        public final int    maxFoodLevel;
        /** Starting food level for a newly born (age 0) animal. */
        public final int    initialFoodLevel;

        public AnimalParams(int maxAge, int breedingAge, double breedingProbability,
                            int maxLitterSize, int foodValue,
                            int maxFoodLevel, int initialFoodLevel)
        {
            this.maxAge              = maxAge;
            this.breedingAge         = breedingAge;
            this.breedingProbability = breedingProbability;
            this.maxLitterSize       = maxLitterSize;
            this.foodValue           = foodValue;
            this.maxFoodLevel        = maxFoodLevel;
            this.initialFoodLevel    = initialFoodLevel;
        }
    }

    /**
     * Mouse-specific parameters, extending the core animal params with
     * disease-spread characteristics.
     */
    public static class MouseParams extends AnimalParams
    {
        /** Probability per step that a healthy mouse spontaneously becomes infected. */
        public final double infectProbability;
        /** Probability per step that an infected mouse recovers one infection level. */
        public final double recoverProbability;
        /** Probability per step that an infected mouse deteriorates one infection level. */
        public final double deteriorateProbability;

        public MouseParams(int maxAge, int breedingAge, double breedingProbability,
                           int maxLitterSize, int foodValue,
                           int maxFoodLevel, int initialFoodLevel,
                           double infectProbability, double recoverProbability,
                           double deteriorateProbability)
        {
            super(maxAge, breedingAge, breedingProbability,
                  maxLitterSize, foodValue, maxFoodLevel, initialFoodLevel);
            this.infectProbability      = infectProbability;
            this.recoverProbability     = recoverProbability;
            this.deteriorateProbability = deteriorateProbability;
        }
    }

    /**
     * Grass-specific parameters.
     */
    public static class GrassParams
    {
        /** Maximum size a grass patch may reach. */
        public final int    maxSize;
        /**
         * Growth is {@code rand.nextInt(maxGrowRate) + maxGrowRate} per rainy step,
         * i.e. the patch always grows by at least {@code maxGrowRate}.
         */
        public final int    maxGrowRate;
        /** Per-step probability that a grass patch catches steppe fire. */
        public final double steppFireProbability;

        public GrassParams(int maxSize, int maxGrowRate, double steppFireProbability)
        {
            this.maxSize             = maxSize;
            this.maxGrowRate         = maxGrowRate;
            this.steppFireProbability = steppFireProbability;
        }
    }

    // -------------------------------------------------------------------------
    // Named config instances — edit values here to tune species behaviour.
    //
    // AnimalParams: (maxAge, breedingAge, breedingProbability, maxLitterSize,
    //                foodValue, maxFoodLevel, initialFoodLevel)
    // MouseParams:  same + (infectProbability, recoverProbability, deteriorateProbability)
    // GrassParams:  (maxSize, maxGrowRate, steppFireProbability)
    // -------------------------------------------------------------------------

    public static final AnimalParams LION  = new AnimalParams(225, 10, 0.10,  5, 20, 275,  40);
    public static final AnimalParams DEER  = new AnimalParams(175, 10, 0.10,  5, 50, 250, 250);
    public static final AnimalParams OWL   = new AnimalParams( 75,  5, 0.10,  3, 10,  25,  25);
    public static final MouseParams  MOUSE = new MouseParams (125,  5, 0.25, 10,  5,   5,   5,
                                                              0.01, 0.30, 0.15);
    public static final AnimalParams CAT   = new AnimalParams( 75,  5, 0.15,  3, 10,  15,  15);
    public static final GrassParams  GRASS = new GrassParams ( 50, 20, 0.02);

    private SpeciesConfig() {}
}
