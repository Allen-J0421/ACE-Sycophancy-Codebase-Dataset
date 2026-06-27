/**
 * Central repository for every tunable constant in the simulation.
 *
 * All magic numbers previously scattered across Simulator, PopulationManager,
 * species classes, Weather, and Disease subclasses are defined here so that
 * adjusting the simulation requires editing only this file.
 *
 * This is a utility class and cannot be instantiated.
 *
 * @version 2022.03.01
 */
public final class SimulationConfiguration
{
    private SimulationConfiguration() {}

    // -------------------------------------------------------------------------
    // Grid
    // -------------------------------------------------------------------------

    public static final int GRID_WIDTH = 150;
    public static final int GRID_DEPTH = 120;

    // -------------------------------------------------------------------------
    // Initial population creation probabilities
    // -------------------------------------------------------------------------

    public static final double HYENA_CREATION_PROBABILITY     = 0.02;
    public static final double LION_CREATION_PROBABILITY      = 0.02;
    public static final double GAZELLE_CREATION_PROBABILITY   = 0.02;
    public static final double MOUSE_CREATION_PROBABILITY     = 0.02;
    public static final double FENNECFOX_CREATION_PROBABILITY = 0.02;
    public static final double GRASS_CREATION_PROBABILITY     = 0.2;
    public static final double LAKE_CREATION_PROBABILITY      = 0.005;

    // -------------------------------------------------------------------------
    // Movement urgency thresholds (StandardMovementStrategy)
    // -------------------------------------------------------------------------

    /** Water level below which seeking water takes top priority. */
    public static final int WATER_URGENCY_THRESHOLD = 3;
    /** Food level below which seeking food takes priority over finding a mate. */
    public static final int FOOD_URGENCY_THRESHOLD  = 8;

    // -------------------------------------------------------------------------
    // Weather – occurrence probabilities
    // -------------------------------------------------------------------------

    public static final double RAIN_PROBABILITY     = 0.12;
    public static final double FOG_PROBABILITY      = 0.005;
    public static final double HEATWAVE_PROBABILITY = 0.0001;

    // -------------------------------------------------------------------------
    // Weather – event durations (steps)
    // -------------------------------------------------------------------------

    public static final int RAIN_DURATION      = 3;
    public static final int FOG_DURATION       = 1;
    public static final int HEATWAVE_DURATION  = 1;

    // -------------------------------------------------------------------------
    // Weather – effect magnitudes
    // -------------------------------------------------------------------------

    /** Water bonus added to organisms during rain. */
    public static final int RAIN_WATER_BONUS         = 5;
    /** Volume bonus added to water sources during rain. */
    public static final int RAIN_VOLUME_BONUS        = 10;
    /** Water bonus added to organisms during fog. */
    public static final int FOG_WATER_BONUS          = 2;
    /** Volume bonus added to water sources during fog. */
    public static final int FOG_VOLUME_BONUS         = 2;
    /** Divisor applied to water/volume during a heatwave. */
    public static final int HEATWAVE_WATER_DIVISOR   = 2;

    // -------------------------------------------------------------------------
    // Lion
    // -------------------------------------------------------------------------

    public static final int    LION_BREEDING_AGE         = 15;
    public static final int    LION_MAX_AGE              = 200;
    public static final double LION_BREEDING_PROBABILITY = 0.5;
    public static final int    LION_MAX_LITTER_SIZE      = 4;
    public static final double LION_HUNT_PROBABILITY     = 0.65;
    public static final int    LION_FOOD_VALUE           = 15;

    // -------------------------------------------------------------------------
    // Hyena
    // -------------------------------------------------------------------------

    public static final int    HYENA_BREEDING_AGE         = 10;
    public static final int    HYENA_MAX_AGE              = 150;
    public static final double HYENA_BREEDING_PROBABILITY = 0.60;
    public static final int    HYENA_MAX_LITTER_SIZE      = 2;
    public static final double HYENA_HUNT_PROBABILITY     = 0.63;
    public static final int    HYENA_FOOD_VALUE           = 15;

    // -------------------------------------------------------------------------
    // Gazelle
    // -------------------------------------------------------------------------

    public static final int    GAZELLE_BREEDING_AGE         = 10;
    public static final int    GAZELLE_MAX_AGE              = 45;
    public static final double GAZELLE_BREEDING_PROBABILITY = 0.5;
    public static final int    GAZELLE_MAX_LITTER_SIZE      = 4;
    public static final int    GAZELLE_FOOD_VALUE           = 20;

    // -------------------------------------------------------------------------
    // Mouse
    // -------------------------------------------------------------------------

    public static final int    MOUSE_BREEDING_AGE         = 4;
    public static final int    MOUSE_MAX_AGE              = 40;
    public static final double MOUSE_BREEDING_PROBABILITY = 0.2;
    public static final int    MOUSE_MAX_LITTER_SIZE      = 4;
    public static final double MOUSE_HUNT_PROBABILITY     = 0.7;
    public static final int    MOUSE_FOOD_VALUE           = 10;

    // -------------------------------------------------------------------------
    // FennecFox
    // -------------------------------------------------------------------------

    public static final int    FENNECFOX_BREEDING_AGE         = 12;
    public static final int    FENNECFOX_MAX_AGE              = 100;
    public static final double FENNECFOX_BREEDING_PROBABILITY = 0.5;
    public static final int    FENNECFOX_MAX_LITTER_SIZE      = 2;
    public static final double FENNECFOX_HUNT_PROBABILITY     = 0.6;
    public static final int    FENNECFOX_FOOD_VALUE           = 12;

    // -------------------------------------------------------------------------
    // Grass
    // -------------------------------------------------------------------------

    public static final int    GRASS_MAX_AGE                     = 20;
    public static final double GRASS_REPRODUCTION_PROBABILITY    = 0.44;
    public static final int    GRASS_MAX_LITTER_SIZE             = 6;
    public static final int    GRASS_FOOD_VALUE                  = 10;
    /** Minimum water level at which grass is able to reproduce. */
    public static final int    GRASS_WATER_REPRODUCTION_THRESHOLD = 9;

    // -------------------------------------------------------------------------
    // Lake
    // -------------------------------------------------------------------------

    /** Water restored to an organism per drink from a lake. */
    public static final int LAKE_WATER_VALUE = 15;

    // -------------------------------------------------------------------------
    // Disease – Covid
    // -------------------------------------------------------------------------

    public static final double   COVID_PROBABILITY        = 0.000001;
    public static final double   COVID_INFECTIOUSNESS     = 0.005;
    public static final int      COVID_DURATION           = 8;
    public static final String[] COVID_AFFECTED_SPECIES   = {"Gazelle", "Lion", "Hyena"};

    // -------------------------------------------------------------------------
    // Disease – Leptospirosis
    // -------------------------------------------------------------------------

    public static final double   LEPTOSPIROSIS_PROBABILITY      = 0.0000008;
    public static final double   LEPTOSPIROSIS_INFECTIOUSNESS   = 0.001;
    public static final int      LEPTOSPIROSIS_DURATION         = 10;
    public static final String[] LEPTOSPIROSIS_AFFECTED_SPECIES = {"Lion", "Hyena", "Mouse", "Gazelle", "Lake"};
}
