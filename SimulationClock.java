/**
 * Tracks simulation step and derived time values.
 */
public class SimulationClock {

    private static final int STEPS_PER_HOUR = 5;
    private static final int HOURS_PER_DAY = 24;
    private static final int HOURS_PER_TIME_PERIOD = 4;

    private int step;
    private int day;
    private int hour;
    private TimeOfDay timeOfDay;

    public SimulationClock(TimeOfDay initialTimeOfDay) {
        reset(initialTimeOfDay);
    }

    public void reset(TimeOfDay initialTimeOfDay) {
        step = 0;
        day = 1;
        hour = 0;
        timeOfDay = initialTimeOfDay;
    }

    public void advanceStep() {
        step++;
        day = step / (STEPS_PER_HOUR * HOURS_PER_DAY) + 1;
        hour = (step / STEPS_PER_HOUR) % HOURS_PER_DAY + 1;
    }

    public boolean isWeatherUpdateDue() {
        return step % STEPS_PER_HOUR == 0;
    }

    public void advanceTimeOfDayIfDue() {
        if (isWeatherUpdateDue() && hour % HOURS_PER_TIME_PERIOD == 0) {
            timeOfDay = timeOfDay.next();
        }
    }

    public int getStep() {
        return step;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }
}
