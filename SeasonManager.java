import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages the ordered sequence of seasons, their temperature fluctuations, and
 * seasonal transitions. Extracted from Habitat to give each class a single
 * responsibility: SeasonManager handles the seasonal mechanics, Habitat
 * coordinates when those mechanics are triggered.
 *
 * @version 2022.03.01
 */
public class SeasonManager
{
    // Ordered list of seasons: spring, summer, autumn, winter.
    private final List<Season> seasons;
    // The season currently active in the simulation.
    private Season currentSeason;
    // True when the current season is spring.
    private boolean isSpring;
    // Random number generator for temperature fluctuation.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Build a SeasonManager, initialise the four seasons in order, start at spring,
     * and apply the initial climate-change offset to spring's temperature.
     *
     * @param springValues (int[]) [0] = average temperature, [1] = max change.
     * @param summerValues (int[]) [0] = average temperature, [1] = max change.
     * @param autumnValues (int[]) [0] = average temperature, [1] = max change.
     * @param winterValues (int[]) [0] = average temperature, [1] = max change.
     * @param initialClimateEffect (int) Initial climate-change temperature offset.
     */
    public SeasonManager(int[] springValues, int[] summerValues,
                         int[] autumnValues, int[] winterValues, int initialClimateEffect)
    {
        seasons = new ArrayList<>();
        seasons.add(new Season("spring", springValues[0], springValues[1]));
        seasons.add(new Season("summer", summerValues[0], summerValues[1]));
        seasons.add(new Season("autumn", autumnValues[0], autumnValues[1]));
        seasons.add(new Season("winter", winterValues[0], winterValues[1]));

        currentSeason = seasons.get(0);
        isSpring = true;
        applyClimateEffect(initialClimateEffect);
    }

    /**
     * Advance to the next season in the cycle, update the spring flag, and apply
     * the current climate-change offset to the new season's average temperature.
     * Called by Habitat when the step count crosses a season boundary.
     *
     * @param climateEffect (int) The climate-change temperature offset to apply.
     */
    public void transitionSeason(int climateEffect)
    {
        changeSeason();
        checkIsSpring();
        applyClimateEffect(climateEffect);
    }

    /**
     * Randomly adjust the current season's temperature up or down within its
     * allowed range. Called by Habitat on every simulation step.
     */
    public void randomizeTemperature()
    {
        int randomize = rand.nextInt(2);
        int change = rand.nextInt(currentSeason.getTempChange() + 1);
        Thermometer currentTemp = currentSeason.getCurrentTemp();

        if (randomize == 0 && (getCurrentTemperature() + change) <= currentSeason.getUpperLimitTemp()) {
            currentTemp.incrementTemperature(change);
        } else if (randomize == 1 && (getCurrentTemperature() - change) >= currentSeason.getLowerLimitTemp()) {
            currentTemp.incrementTemperature(-change);
        }
    }

    /**
     * @return (String) The name of the current season.
     */
    public String getCurrentSeasonName()
    {
        return currentSeason.getName();
    }

    /**
     * @return (int) The current temperature of the active season.
     */
    public int getCurrentTemperature()
    {
        return currentSeason.getCurrentTemp().getTemperature();
    }

    /**
     * @return (boolean) True if the current season is spring.
     */
    public boolean getIsSpring()
    {
        return isSpring;
    }

    /**
     * Cycle forward to the next season, wrapping around after winter.
     */
    private void changeSeason()
    {
        int idx = seasons.indexOf(currentSeason);
        currentSeason = seasons.get((idx + 1) % seasons.size());
    }

    /**
     * Update the isSpring flag based on whether the current season is the first
     * season in the list (spring).
     */
    private void checkIsSpring()
    {
        isSpring = currentSeason.getName().equals(seasons.get(0).getName());
    }

    /**
     * Raise the current season's average temperature by the given offset.
     *
     * @param effect (int) The temperature offset to apply.
     */
    private void applyClimateEffect(int effect)
    {
        currentSeason.incAveTemperature(effect);
    }
}
