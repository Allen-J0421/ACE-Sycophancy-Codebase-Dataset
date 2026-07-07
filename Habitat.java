/**
 * Coordinates the simulation's climate state by driving SeasonManager at the
 * appropriate step boundaries and compounding the climate-change scenario each year.
 * Seasonal mechanics (transitions, temperature fluctuation) live in SeasonManager.
 *
 * @version 2022.02.28
 */
public class Habitat
{
    // The number of steps before the season advances.
    private static final int SEASON_CHANGE = 50;
    // Tracks simulation steps to determine year and season boundaries.
    private final SimulationStep simStep;
    // Applies a compounding temperature offset each year.
    private final ClimateScenario changeScenario;
    // Manages season ordering, transitions, and temperature fluctuation.
    private final SeasonManager seasonManager;

    /**
     * Initialise the Habitat: build the SeasonManager with the four season
     * configurations and the initial climate-change offset.
     *
     * @param simStep (SimulationStep) Step counter shared with the simulation.
     * @param changeScenario (ClimateScenario) The climate change scenario to apply.
     * @param spring (int[]) [0] = average temperature, [1] = max change.
     * @param summer (int[]) [0] = average temperature, [1] = max change.
     * @param autumn (int[]) [0] = average temperature, [1] = max change.
     * @param winter (int[]) [0] = average temperature, [1] = max change.
     */
    public Habitat(SimulationStep simStep, ClimateScenario changeScenario,
                   int[] spring, int[] summer, int[] autumn, int[] winter)
    {
        this.simStep = simStep;
        this.changeScenario = changeScenario;
        this.seasonManager = new SeasonManager(spring, summer, autumn, winter,
                changeScenario.getClimateChangeEffect());
    }

    /**
     * @return (String) The name of the current season.
     */
    public String getCurrentSeason()
    {
        return seasonManager.getCurrentSeasonName();
    }

    /**
     * @return (int) The current temperature of the active season.
     */
    public int getCurrentTemperature()
    {
        return seasonManager.getCurrentTemperature();
    }

    /**
     * @return (boolean) True if the current season is spring.
     */
    public boolean getIsSpring()
    {
        return seasonManager.getIsSpring();
    }

    /**
     * @return (boolean) True if a full year's worth of steps has just completed.
     */
    public boolean yearPassed()
    {
        int step = simStep.getCurrentStep();
        return step != 0 && (step + 1) % (SEASON_CHANGE * 4) == 0;
    }

    /**
     * Advance the habitat by one simulation step:
     * 1) Compound the climate-change effect if a year has just passed.
     * 2) Transition to the next season every SEASON_CHANGE steps.
     * 3) Fluctuate the current temperature randomly.
     */
    public void habitatStep()
    {
        int step = simStep.getCurrentStep();

        if (yearPassed()) {
            changeScenario.doClimateChange();
        }

        if (step != 0 && step % SEASON_CHANGE == 0) {
            seasonManager.transitionSeason(changeScenario.getClimateChangeEffect());
        }

        seasonManager.randomizeTemperature();
    }
}
