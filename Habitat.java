import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represent a habitat of the simulation.
 * Keep track of its seasons and temperature.
 *
 * @version 2022.02.28
 */
public class Habitat
{
    private static final int SPRING_INDEX = 0;
    private static final int SEASONS_IN_YEAR = 4;
    private static final int RANDOM_TEMPERATURE_DIRECTIONS = 2;
    // holds the habitat's seasons
    private final List<Season> seasons;
    // the number of steps before the season changes
    private static final int SEASON_CHANGE = 50;
    // hold the index of the current season
    private int currentSeasonIndex;
    // keep track of the simulation steps.
    private final SimulationStep simStep;
    // hold a climate change scenario
    private final ClimateScenarios changeScenario;
    // A random number generator
    private static final Random rand = Randomizer.getRandom();

    /**
     * Initialise the Habitat fields and fill the
     * seasons Hash map
     *
     * @param simStep (SimulatorStep) A SimulationStep object to keep track of the steps
     * @param changeScenario (ClimateScenarios) A climate change scenario
     * @param spring (int[]) An integer array with two elements: [0]= spring aveTemperature, [1] = spring tempChange
     * @param summer (int[]) An  integer array with two elements: [0]= summer aveTemperature, [1] = summer tempChange
     * @param autumn (int[]) An  integer array with two elements: [0]= autumn aveTemperature, [1] = autumn tempChange
     * @param winter (int[]) An  integer array with two elements: [0]= winter aveTemperature, [1] = winter tempChange
     */
    public Habitat(SimulationStep simStep, ClimateScenarios changeScenario, int[] spring, int[] summer, int[] autumn, int[] winter)
    {
        this.simStep = simStep;
        this.changeScenario = changeScenario;
        this.seasons = new ArrayList<>();

        // Season initialisations
        initialiseSeasons(spring, summer, autumn, winter);
        currentSeasonIndex = SPRING_INDEX;   // the simulation always starts with spring
        climateChangeEffect(); // do the climate change effect on the first season
    }

    /**
     * @return (String) The current season as a String
     */
    public String getCurrentSeason()
    {
        return getCurrentSeasonState().getName();
    }

    /**
     * @return (int) The current temperature of the current season as an int
     */
    public int getCurrentTemperature()
    {
        return getCurrentSeasonState().getCurrentTemperature();
    }

    /**
     * @return (boolean) true if the current season is spring, false otherwise.
     */
    public boolean getIsSpring()
    {
        return currentSeasonIndex == SPRING_INDEX;
    }

    /**
     * @return (boolean) true if a year has passed in the simulation, false otherwise
     */
    public boolean yearPassed()
    {
        int step = simStep.getCurrentStep();
        // step+1 because step starts with 0
        return step != 0 && (step + 1) % (SEASON_CHANGE * SEASONS_IN_YEAR) == 0;
    }

    /**
     * Method to be called on every step of the simulation for habitat conditions to be updated.
     *
     * 1) Increase the climate change effect if a year has passed
     * 2) Change the season if 'SEASON_CHANGE' steps have passed.
     * 3) Do the climate change effect on the new season
     * 4) Change the temperature each step
     */
    public void habitatStep()
    {
        int step = simStep.getCurrentStep();

        // 1)
        if (yearPassed()) {
            changeScenario.doClimateChange();
        }

        // 2) & 3)
        if (shouldAdvanceSeason(step)) {
            changeSeason();
            climateChangeEffect();
        }

        // 4)
        randomizeTemperature();
    }

    /**
     * Create and initialise the appropriate Season objects with the given values. Add them to the seasons List with the
     * order: spring, summer, autumn, winter.
     *
     * @param springValues (int[]) An integer array with two elements: [0]= spring aveTemperature, [1] = spring tempChange
     * @param summerValues (int[]) An integer array with two elements: [0]= summer aveTemperature, [1] = summer tempChange
     * @param autumnValues (int[]) An integer array with two elements: [0]= autumn aveTemperature, [1] = autumn tempChange
     * @param winterValues (int[]) An integer array with two elements: [0]= winter aveTemperature, [1] = winter tempChange
     */
    private void initialiseSeasons(int[] springValues, int[] summerValues, int[] autumnValues, int[] winterValues)
    {
        seasons.add(new Season("spring", springValues[0], springValues[1]));
        seasons.add(new Season("summer", summerValues[0], summerValues[1]));
        seasons.add(new Season("autumn", autumnValues[0], autumnValues[1]));
        seasons.add(new Season("winter", winterValues[0], winterValues[1]));
    }

    /**
     * Change the current season according to the predefined order of seasons
     */
    private void changeSeason()
    {
        currentSeasonIndex = (currentSeasonIndex + 1) % seasons.size();
    }

    /**
     * Change the current season's temperature randomly according to the season's tempChange field
     */
    private void randomizeTemperature()
    {
        Season currentSeason = getCurrentSeasonState();
        int change = rand.nextInt(currentSeason.getTempChange() + 1);
        int delta = rand.nextInt(RANDOM_TEMPERATURE_DIRECTIONS) == 0 ? change : -change;

        if (currentSeason.canChangeCurrentTemperatureBy(delta)) {
            currentSeason.changeCurrentTemperature(delta);
        }
    }

    /**
     * Increase the season's average temperature by the climate change scenario's concreteChange
     */
    private void climateChangeEffect()
    {
        getCurrentSeasonState().incAveTemperature(changeScenario.getClimateChangeEffect());
    }

    private Season getCurrentSeasonState()
    {
        return seasons.get(currentSeasonIndex);
    }

    private boolean shouldAdvanceSeason(int step)
    {
        return step != 0 && step % SEASON_CHANGE == 0;
    }
}
