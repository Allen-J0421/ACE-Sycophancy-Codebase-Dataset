import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Immutable configuration for the simulation.
 */
public final class SimulationConfig
{
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

    /**
     * Create a default configuration.
     * @return The default simulation configuration.
     */
    public static SimulationConfig defaultConfig() {
        return new SimulationConfig(
                DEFAULT_WIDTH,
                DEFAULT_DEPTH,
                DAY_LENGTH_HOURS,
                TIME_ADVANCE_INTERVAL_STEPS,
                WEATHER_INTERVAL_STEPS,
                DISEASE_INTERVAL_STEPS,
                STEP_DELAY_MILLIS,
                defaultPopulationRules());
    }

    /**
     * Create a configuration with a different field size, preserving all other settings.
     * @param width Field width.
     * @param depth Field depth.
     * @return A new configuration with the supplied field size.
     */
    public SimulationConfig withFieldSize(int depth, int width) {
        return new SimulationConfig(
                width,
                depth,
                dayLengthHours,
                timeAdvanceIntervalSteps,
                weatherIntervalSteps,
                diseaseIntervalSteps,
                stepDelayMillis,
                populationRules);
    }

    private SimulationConfig(int width, int depth, int dayLengthHours,
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

    /**
     * Create the default population registry.
     * @return The ordered population rules.
     */
    private static List<PopulationRule> defaultPopulationRules() {
        return Collections.unmodifiableList(Arrays.asList(
                new PopulationRule(0.09, (field, location, animals, plants) ->
                        animals.add(new Dingo(true, field, location))),
                new PopulationRule(0.13, (field, location, animals, plants) ->
                        animals.add(new Ant(true, field, location))),
                new PopulationRule(0.12, (field, location, animals, plants) ->
                        animals.add(new Snake(true, field, location))),
                new PopulationRule(0.11, (field, location, animals, plants) ->
                        animals.add(new Rat(true, field, location))),
                new PopulationRule(0.12, (field, location, animals, plants) ->
                        animals.add(new Eagle(true, field, location))),
                new PopulationRule(0.08, (field, location, animals, plants) ->
                        animals.add(new Emu(true, field, location))),
                new PopulationRule(0.34, (field, location, animals, plants) ->
                        plants.add(new Acacia(field, location))),
                new PopulationRule(0.36, (field, location, animals, plants) ->
                        plants.add(new Grass(field, location)))));
    }

    /**
     * @return The configured field width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return The configured field depth.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @return The number of steps after which the time advances by one hour.
     */
    public int getTimeAdvanceIntervalSteps() {
        return timeAdvanceIntervalSteps;
    }

    /**
     * @return The number of simulation steps per day.
     */
    public int getDayLengthHours() {
        return dayLengthHours;
    }

    /**
     * @return The number of steps between weather changes.
     */
    public int getWeatherIntervalSteps() {
        return weatherIntervalSteps;
    }

    /**
     * @return The number of steps between disease events.
     */
    public int getDiseaseIntervalSteps() {
        return diseaseIntervalSteps;
    }

    /**
     * @return The step delay in milliseconds.
     */
    public int getStepDelayMillis() {
        return stepDelayMillis;
    }

    /**
     * @return The ordered population rules.
     */
    public List<PopulationRule> getPopulationRules() {
        return populationRules;
    }

    /**
     * A single probability-driven population rule.
     */
    public static final class PopulationRule {
        private final double probability;
        private final PopulationFactory factory;

        PopulationRule(double probability, PopulationFactory factory) {
            this.probability = probability;
            this.factory = factory;
        }

        boolean tryPopulate(Random rand, Field field, int row, int col,
                List<Animal> animals, List<Plant> plants) {
            if (rand.nextDouble() <= probability) {
                factory.create(field, new Location(row, col), animals, plants);
                return true;
            }
            return false;
        }
    }

    /**
     * Factory for creating an entity at a field location.
     */
    @FunctionalInterface
    public interface PopulationFactory {
        void create(Field field, Location location, List<Animal> animals, List<Plant> plants);
    }
}
