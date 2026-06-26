/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An immutable snapshot of the simulation's state at a single moment, broadcast
 * to {@link SimulationListener}s when the simulation resets or completes a step.
 * Listeners (such as the GUI) read whatever they need from it instead of being
 * pushed individual updates.
 *
 * @version 2022.03.02
 */
public class SimulationEvent {

    private final int step;
    private final Field field;
    private final int day;
    private final int hour;
    private final Weather weather;
    private final TimeOfDay time;

    /**
     * Create a snapshot of the simulation state.
     *
     * @param step The current step number.
     * @param field The current field.
     * @param day The current day.
     * @param hour The current hour.
     * @param weather The current weather.
     * @param time The current time of day.
     */
    public SimulationEvent(int step, Field field, int day, int hour, Weather weather, TimeOfDay time) {
        this.step = step;
        this.field = field;
        this.day = day;
        this.hour = hour;
        this.weather = weather;
        this.time = time;
    }

    public int getStep() {
        return step;
    }

    public Field getField() {
        return field;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public Weather getWeather() {
        return weather;
    }

    public TimeOfDay getTime() {
        return time;
    }
}
