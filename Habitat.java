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
    // holds the habitat's seasons
    private List<Season> seasons;
    // the number of steps before the season changes
    private static final int SEASON_CHANGE = 50;
    // hold the current season
    private Season currentSeason;
    // keep track of the simulation steps.
    private SimulationStep simStep;
    // hold a climate change scenario
    private ClimateScenarios changeScenario;
    // true if the current season is Spring
    private boolean isSpring;
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

        // Season initialisations
        initialiseSeasons(spring, summer, autumn, winter);
        currentSeason = seasons.get(0);   // the simulation always starts with spring
        isSpring = true;
        climateChangeEffect(); // do the climate change effect on the first season
    }

    /**
     * @return (String) The current season as a String
     */
    public String getCurrentSeason()
    {
        return currentSeason.getName();
    }

    /**
     * @return (int) The current temperature of the current season as an int
     */
    public int getCurrentTemperature()
    {
        return currentSeason.getCurrentTemperature();
    }

    /**
     * @return (boolean) true if the current season is spring, false otherwise.
     */
    public boolean getIsSpring()
    {
        return isSpring;
    }

    /**
     * @return (boolean) true if a year has passed in the simulation, false otherwise
     */
    public boolean yearPassed()
    {
        int step = simStep.getCurrentStep();
        // step+1 because step starts with 0
        return step != 0 && (step+1) % (SEASON_CHANGE * 4) == 0;
    }

    /**
     * @return (boolean) true if the season changed on this simulation step, false otherwise
     */
    public boolean seasonJustChanged()
    {
        int step = simStep.getCurrentStep();
        return step != 0 && step % SEASON_CHANGE == 0;
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
        if(yearPassed())
        {
            changeScenario.doClimateChange();
        }

        // 2) & 3)
        if(step != 0 && step % SEASON_CHANGE == 0)
        {
            changeSeason();
            checkIsSpring();
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
        Season spring = new Season("spring", springValues[0], springValues[1]);
        Season summer = new Season("summer", summerValues[0], summerValues[1]);
        Season autumn = new Season("autumn", autumnValues[0], autumnValues[1]);
        Season winter = new Season("winter", winterValues[0], winterValues[1]);

        // Initialise seasons and fill it
        seasons = new ArrayList<>();
        seasons.add(spring);
        seasons.add(summer);
        seasons.add(autumn);
        seasons.add(winter);
    }

    /**
     * Change the current season according to the predefined order of seasons
     */
    private void changeSeason()
    {
        int seasonIndx = seasons.indexOf(currentSeason);

        if(seasonIndx == seasons.size() -1) {
            currentSeason = seasons.get(0);
        }
        else {
            currentSeason = seasons.get(seasonIndx + 1);
        }
    }

    /**
     * Make isSpring true or false depending on the current season.
     */
    private void checkIsSpring()
    {
        isSpring = currentSeason.getName().equals(seasons.get(0).getName());
    }

    /**
     * Change the current season's temperature randomly according to the season's tempChange field
     */
    private void randomizeTemperature()
    {
        int randomize = rand.nextInt(2);
        int change = rand.nextInt(currentSeason.getTempChange() + 1);

        // make sure that the temperature doesn't go beyond the temperature upper limit
        if (randomize == 0 && (getCurrentTemperature() + change) <= currentSeason.getUpperLimitTemp()) {
            currentSeason.incrementCurrentTemperature(change);
        }
        // make sure that the temperature doesn't go below the temperature lower limit
        else if (randomize == 1 && (getCurrentTemperature() - change) >= currentSeason.getLowerLimitTemp()) {
            currentSeason.incrementCurrentTemperature(-change);
        }
    }

    /**
     * Increase the season's average temperature by the climate change scenario's concreteChange
     */
    private void climateChangeEffect()
    {
        currentSeason.incAveTemperature(changeScenario.getClimateChangeEffect());
    }
}