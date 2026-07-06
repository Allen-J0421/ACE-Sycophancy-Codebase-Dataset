import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Manages one simulation step and all environmental state: the entity list,
 * the field, the step counter, the clock, and the weather cycle.
 * The view is not referenced here; callers read state via the provided getters.
 *
 * @version 2022.03.02
 */
public class SimulationController
{
    // Simulation step frequency at which the environmental clock ticks.
    private static final int STEPS_PER_HOUR = 5;
    // Number of hours in a simulated day.
    private static final int HOURS_PER_DAY = 24;
    // Number of steps in a simulated day.
    private static final int STEPS_PER_DAY = 120;
    // Number of hours per time-of-day period.
    private static final int HOURS_PER_TIME_PERIOD = 4;

    // All living entities in the simulation.
    private List<Entity> organisms;
    // The field containing all organisms.
    private Field field;
    // Number of steps taken since the last reset.
    private int step;
    // Current simulated hour within the day (1–24).
    private int hour;
    // Current time-of-day period.
    private TimeOfDay currentTime;
    // Current weather state.
    private Weather currentWeather;

    /**
     * Create a controller for a field of the given dimensions.
     *
     * @param depth The depth of the simulation field.
     * @param width The width of the simulation field.
     */
    public SimulationController(int depth, int width)
    {
        organisms = new ArrayList<>();
        field = new Field(depth, width);
    }

    /**
     * Advance the simulation by one step: let every organism act, remove the
     * dead, add newborns, then tick the weather and clock.
     */
    public void advance()
    {
        step++;

        List<Entity> newOrganisms = new ArrayList<>();
        for (Iterator<Entity> it = organisms.iterator(); it.hasNext(); ) {
            Entity entity = it.next();
            Organism organism = (Organism) entity;
            organism.act(newOrganisms, currentWeather, currentTime);
            if (organism.isRemoved()) {
                it.remove();
            }
        }
        organisms.addAll(newOrganisms);

        hour = (step / STEPS_PER_HOUR) % HOURS_PER_DAY + 1;

        if (step % STEPS_PER_HOUR == 0) {
            currentWeather.generate();
        }

        if ((hour % HOURS_PER_TIME_PERIOD == 0) && (step % STEPS_PER_HOUR == 0)) {
            currentTime = currentTime.next();
        }
    }

    /**
     * Reset all simulation state to its initial values and repopulate the field.
     *
     * @param populator The populator used to seed the field with organisms.
     */
    public void reset(FieldPopulator populator)
    {
        step = 0;
        hour = 1;
        currentTime = TimeOfDay.SUNRISE;
        currentWeather = new Weather(WeatherType.SUN);
        organisms.clear();
        field.clear();
        populator.populate(organisms, field);
    }

    /**
     * @return The number of steps taken since the last reset.
     */
    public int getStep()
    {
        return step;
    }

    /**
     * @return The current simulated hour within the day (1–24).
     */
    public int getHour()
    {
        return hour;
    }

    /**
     * @return The current simulated day number (1-based).
     */
    public int getDay()
    {
        return step / STEPS_PER_DAY + 1;
    }

    /**
     * @return The current time-of-day period.
     */
    public TimeOfDay getCurrentTime()
    {
        return currentTime;
    }

    /**
     * @return The current weather state.
     */
    public Weather getCurrentWeather()
    {
        return currentWeather;
    }

    /**
     * @return The field containing all organisms.
     */
    public Field getField()
    {
        return field;
    }
}
