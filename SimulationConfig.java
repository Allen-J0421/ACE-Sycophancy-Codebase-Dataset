package savannah.config;

/**
 * Centralized configuration for the simulation.
 */
public final class SimulationConfig
{
    public static final SimulationConfig DEFAULT = new SimulationConfig();

    public final int defaultWidth = 270;
    public final int defaultDepth = 180;
    public final int viewScalingFactor = 6;

    public final double lionCreationProbability = 0.0125;
    public final double cheetahCreationProbability = 0.0125;
    public final double zebraCreationProbability = 0.08;
    public final double giraffeCreationProbability = 0.08;
    public final double lemurCreationProbability = 0.081;

    public final double diseaseProbability = 0.00000015;
    public final double diseaseSpreadProbability = 0.80;
    public final double deathFromInfectionProbability = 0.13;
    public final double immuneProbability = 0.05;
    public final double immuneLossProbability = immuneProbability / 15.0;
    public final double overcrowdingDeathProbability = 0.3;
    public final double offspringImmuneInheritanceProbability = 0.15;
    public final double offspringLoseImmunityProbability = 0.9;

    public final int timeOffset = 12;
    public final int hoursPerDay = 24;
    public final int dayStartHour = 6;
    public final int nightStartHour = 18;

    public final int minimumWeatherDuration = 27;
    public final int weatherDurationVariance = 20;
    public final double sunnyPlantSpreadProbability = 0.2;
    public final double rainyPlantSpreadProbability = 0.14;
    public final double foggyPlantSpreadProbability = 0.05;
    public final double cloudyPlantSpreadProbability = 0.1;
    public final double clearPlantSpreadProbability = 0.08;

    public final SpeciesConfig lion = new SpeciesConfig(
        0.80, 80, 1400, 0.65, 2, 50, 35, 0.80, true, 0.0, 1.0 / 5.5
    );
    public final SpeciesConfig cheetah = new SpeciesConfig(
        0.60, 60, 1000, 0.70, 3, 40, 16, 0.80, true, 0.0, 1.0 / 5.5
    );
    public final SpeciesConfig zebra = new SpeciesConfig(
        0.0, 25, 200, 0.45, 3, 25, 13, 0.75, false, 0.25, 0.0
    );
    public final SpeciesConfig giraffe = new SpeciesConfig(
        0.0, 30, 500, 0.65, 2, 20, 15, 0.75, false, 0.25, 0.0
    );
    public final SpeciesConfig lemur = new SpeciesConfig(
        0.0, 15, 60, 0.40, 5, 7, 10, 0.75, false, 0.50, 0.0
    );

    public final PlantConfig plant = new PlantConfig(
        0.05, 0.52, 3, 0.10, 1.0
    );

    private SimulationConfig()
    {
    }

    /**
     * Configuration for a species.
     */
    public static final class SpeciesConfig
    {
        public final double preyCatchingProbability;
        public final int breedingAge;
        public final int maxAge;
        public final double breedingProbability;
        public final int maxLitterSize;
        public final int maxFoodLevel;
        public final int foodValue;
        public final double movementProbability;
        public final boolean useRandomNewbornFoodLevel;
        public final double newbornFoodLevelFraction;
        public final double randomNewbornFoodLevelMaxFraction;

        private SpeciesConfig(
            double preyCatchingProbability,
            int breedingAge,
            int maxAge,
            double breedingProbability,
            int maxLitterSize,
            int maxFoodLevel,
            int foodValue,
            double movementProbability,
            boolean useRandomNewbornFoodLevel,
            double newbornFoodLevelFraction,
            double randomNewbornFoodLevelMaxFraction)
        {
            this.preyCatchingProbability = preyCatchingProbability;
            this.breedingAge = breedingAge;
            this.maxAge = maxAge;
            this.breedingProbability = breedingProbability;
            this.maxLitterSize = maxLitterSize;
            this.maxFoodLevel = maxFoodLevel;
            this.foodValue = foodValue;
            this.movementProbability = movementProbability;
            this.useRandomNewbornFoodLevel = useRandomNewbornFoodLevel;
            this.newbornFoodLevelFraction = newbornFoodLevelFraction;
            this.randomNewbornFoodLevelMaxFraction = randomNewbornFoodLevelMaxFraction;
        }
    }

    /**
     * Configuration for plants.
     */
    public static final class PlantConfig
    {
        public final double growthRate;
        public final double percentageEaten;
        public final int foodValue;
        public final double minimumRandomHealthFraction;
        public final double newbornHealthFraction;

        private PlantConfig(
            double growthRate,
            double percentageEaten,
            int foodValue,
            double minimumRandomHealthFraction,
            double newbornHealthFraction)
        {
            this.growthRate = growthRate;
            this.percentageEaten = percentageEaten;
            this.foodValue = foodValue;
            this.minimumRandomHealthFraction = minimumRandomHealthFraction;
            this.newbornHealthFraction = newbornHealthFraction;
        }
    }
}
