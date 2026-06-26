package configuration;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

import simulation.Acacia;
import simulation.Ant;
import simulation.Dingo;
import simulation.Eagle;
import simulation.Emu;
import simulation.Field;
import simulation.Grass;
import simulation.Location;
import simulation.Plant;
import simulation.Rat;
import simulation.Snake;

/**
 * Unified configuration for simulation timing, population, and species tuning.
 */
public final class Configuration
{
    private static final Configuration DEFAULT = new Configuration(
            Simulation.defaults(),
            new Species());

    private final Simulation simulation;
    private final Species species;

    private Configuration(Simulation simulation, Species species) {
        this.simulation = simulation;
        this.species = species;
    }

    /**
     * @return The default configuration.
     */
    public static Configuration defaults() {
        return DEFAULT;
    }

    /**
     * Create a configuration with a different field size, preserving all other settings.
     * @param depth Field depth.
     * @param width Field width.
     * @return A new configuration with the supplied field size.
     */
    public Configuration withFieldSize(int depth, int width) {
        return new Configuration(simulation.withFieldSize(depth, width), species);
    }

    /**
     * @return The simulation settings.
     */
    public Simulation simulation() {
        return simulation;
    }

    /**
     * @return The species tuning registry.
     */
    public Species species() {
        return species;
    }

    /**
     * Identifiers for species-specific prey and food lookup.
     */
    public enum SpeciesId {
        DINGO,
        ANT,
        RAT,
        SNAKE,
        EAGLE,
        EMU,
        ACACIA,
        GRASS
    }

    /**
     * Supported population kinds.
     */
    public enum PopulationKind {
        DINGO,
        ANT,
        SNAKE,
        RAT,
        EAGLE,
        EMU,
        ACACIA,
        GRASS
    }

    /**
     * A single probability-driven population rule.
     */
    public static final class PopulationRule {
        private final double probability;
        private final PopulationKind kind;

        public PopulationRule(double probability, PopulationKind kind) {
            this.probability = probability;
            this.kind = kind;
        }

        public double getProbability() {
            return probability;
        }

        public PopulationKind getKind() {
            return kind;
        }
    }

    /**
     * Simulation-wide settings.
     */
    public static final class Simulation {
        private static final int DEFAULT_WIDTH = 120;
        private static final int DEFAULT_DEPTH = 80;
        private static final int DAY_LENGTH_HOURS = 24;
        private static final int TIME_ADVANCE_INTERVAL_STEPS = 5;
        private static final int WEATHER_INTERVAL_STEPS = 50;
        private static final int DISEASE_INTERVAL_STEPS = 100;
        private static final int STEP_DELAY_MILLIS = 20;

        private final int width;
        private final int depth;
        private final int dayLengthHours;
        private final int timeAdvanceIntervalSteps;
        private final int weatherIntervalSteps;
        private final int diseaseIntervalSteps;
        private final int stepDelayMillis;
        private final List<PopulationRule> populationRules;

        private Simulation(int width, int depth, int dayLengthHours,
                int timeAdvanceIntervalSteps, int weatherIntervalSteps,
                int diseaseIntervalSteps, int stepDelayMillis,
                List<PopulationRule> populationRules) {
            this.width = width;
            this.depth = depth;
            this.dayLengthHours = dayLengthHours;
            this.timeAdvanceIntervalSteps = timeAdvanceIntervalSteps;
            this.weatherIntervalSteps = weatherIntervalSteps;
            this.diseaseIntervalSteps = diseaseIntervalSteps;
            this.stepDelayMillis = stepDelayMillis;
            this.populationRules = populationRules;
        }

        private static Simulation defaults() {
            return new Simulation(
                    DEFAULT_WIDTH,
                    DEFAULT_DEPTH,
                    DAY_LENGTH_HOURS,
                    TIME_ADVANCE_INTERVAL_STEPS,
                    WEATHER_INTERVAL_STEPS,
                    DISEASE_INTERVAL_STEPS,
                    STEP_DELAY_MILLIS,
                    defaultPopulationRules());
        }

        private Simulation withFieldSize(int depth, int width) {
            return new Simulation(
                    width,
                    depth,
                    dayLengthHours,
                    timeAdvanceIntervalSteps,
                    weatherIntervalSteps,
                    diseaseIntervalSteps,
                    stepDelayMillis,
                    populationRules);
        }

        private static List<PopulationRule> defaultPopulationRules() {
            return Collections.unmodifiableList(Arrays.asList(
                    new PopulationRule(0.09, PopulationKind.DINGO),
                    new PopulationRule(0.13, PopulationKind.ANT),
                    new PopulationRule(0.12, PopulationKind.SNAKE),
                    new PopulationRule(0.11, PopulationKind.RAT),
                    new PopulationRule(0.12, PopulationKind.EAGLE),
                    new PopulationRule(0.08, PopulationKind.EMU),
                    new PopulationRule(0.34, PopulationKind.ACACIA),
                    new PopulationRule(0.36, PopulationKind.GRASS)));
        }

        public int getWidth() {
            return width;
        }

        public int getDepth() {
            return depth;
        }

        public int getDayLengthHours() {
            return dayLengthHours;
        }

        public int getTimeAdvanceIntervalSteps() {
            return timeAdvanceIntervalSteps;
        }

        public int getWeatherIntervalSteps() {
            return weatherIntervalSteps;
        }

        public int getDiseaseIntervalSteps() {
            return diseaseIntervalSteps;
        }

        public int getStepDelayMillis() {
            return stepDelayMillis;
        }

        public List<PopulationRule> getPopulationRules() {
            return populationRules;
        }
    }

    /**
     * Species-specific tuning registry.
     */
    public static final class Species {
        private final AnimalTuning dingo;
        private final AnimalTuning ant;
        private final AnimalTuning rat;
        private final AnimalTuning snake;
        private final AnimalTuning eagle;
        private final AnimalTuning emu;
        private final PlantTuning acacia;
        private final PlantTuning grass;

        public Species() {
            dingo = animal(
                    50, 700, 0.04, 3, 100,
                    time -> time >= 8 && time <= 24,
                    foodMap(Arrays.asList(entry(SpeciesId.SNAKE, 100))));

            ant = animal(
                    20, 400, 0.32, 14, 60,
                    time -> time >= 4 && time <= 20,
                    foodMap(Arrays.asList(
                            entry(SpeciesId.ACACIA, 60),
                            entry(SpeciesId.GRASS, 60))));

            rat = animal(
                    25, 600, 0.31, 15, 100,
                    time -> time >= 0 && time <= 18,
                    foodMap(Arrays.asList(entry(SpeciesId.ANT, 100))));

            snake = animal(
                    30, 700, 0.33, 11, 100,
                    time -> time >= 5 && time <= 23,
                    foodMap(Arrays.asList(entry(SpeciesId.RAT, 100))));

            eagle = animal(
                    50, 700, 0.10, 11, 60,
                    time -> time >= 6 && time <= 22,
                    foodMap(Arrays.asList(
                            entry(SpeciesId.SNAKE, 60),
                            entry(SpeciesId.RAT, 40))));

            emu = animal(
                    30, 600, 0.17, 7, 60,
                    time -> time <= 9 || time >= 21,
                    foodMap(Arrays.asList(entry(SpeciesId.GRASS, 60))));

            acacia = plant(0.71, 9);
            grass = plant(0.77, 9);
        }

        public AnimalTuning dingo() {
            return dingo;
        }

        public AnimalTuning ant() {
            return ant;
        }

        public AnimalTuning rat() {
            return rat;
        }

        public AnimalTuning snake() {
            return snake;
        }

        public AnimalTuning eagle() {
            return eagle;
        }

        public AnimalTuning emu() {
            return emu;
        }

        public PlantTuning acacia() {
            return acacia;
        }

        public PlantTuning grass() {
            return grass;
        }
    }

    /**
     * Tuning parameters for a species of animal.
     */
    public static final class AnimalTuning {
        private final int breedingAge;
        private final int maxAge;
        private final double breedingProbability;
        private final int maxLitterSize;
        private final int newbornFoodLevel;
        private final IntPredicate activeHours;
        private final Map<SpeciesId, Integer> foodValues;

        private AnimalTuning(int breedingAge, int maxAge, double breedingProbability,
                int maxLitterSize, int newbornFoodLevel, IntPredicate activeHours,
                Map<SpeciesId, Integer> foodValues) {
            this.breedingAge = breedingAge;
            this.maxAge = maxAge;
            this.breedingProbability = breedingProbability;
            this.maxLitterSize = maxLitterSize;
            this.newbornFoodLevel = newbornFoodLevel;
            this.activeHours = activeHours;
            this.foodValues = foodValues;
        }

        public int getBreedingAge() {
            return breedingAge;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public double getBreedingProbability() {
            return breedingProbability;
        }

        public int getMaxLitterSize() {
            return maxLitterSize;
        }

        public int getNewbornFoodLevel() {
            return newbornFoodLevel;
        }

        public boolean isActive(int time) {
            return activeHours.test(time);
        }

        public int foodValueFor(SpeciesId preyId) {
            Integer value = foodValues.get(preyId);
            return value == null ? 0 : value.intValue();
        }
    }

    /**
     * Tuning parameters for a species of plant.
     */
    public static final class PlantTuning {
        private final double reproducingProbability;
        private final int maxOffspringSize;

        private PlantTuning(double reproducingProbability, int maxOffspringSize) {
            this.reproducingProbability = reproducingProbability;
            this.maxOffspringSize = maxOffspringSize;
        }

        public double getReproducingProbability() {
            return reproducingProbability;
        }

        public int getMaxOffspringSize() {
            return maxOffspringSize;
        }
    }

    private static AnimalTuning animal(int breedingAge, int maxAge, double breedingProbability,
            int maxLitterSize, int newbornFoodLevel, IntPredicate activeHours,
            Map<SpeciesId, Integer> foodValues) {
        return new AnimalTuning(breedingAge, maxAge, breedingProbability, maxLitterSize,
                newbornFoodLevel, activeHours, foodValues);
    }

    private static PlantTuning plant(double reproducingProbability, int maxOffspringSize) {
        return new PlantTuning(reproducingProbability, maxOffspringSize);
    }

    private static Map<SpeciesId, Integer> foodMap(List<Map.Entry<SpeciesId, Integer>> entries) {
        Map<SpeciesId, Integer> map = new HashMap<>();
        for (Map.Entry<SpeciesId, Integer> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map.Entry<SpeciesId, Integer> entry(SpeciesId species, int value) {
        return new AbstractMap.SimpleImmutableEntry<>(species, Integer.valueOf(value));
    }
}
