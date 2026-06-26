/**
 * Mutable simulation state that owns time progression and weather state.
 */
public class SimulationState implements SimulationContext {

    private final SimulationClock clock;
    private Weather weather;

    public SimulationState(WeatherType initialWeather, TimeOfDay initialTimeOfDay) {
        this.clock = new SimulationClock(initialTimeOfDay);
        this.weather = new Weather(initialWeather);
    }

    public void reset(WeatherType initialWeather, TimeOfDay initialTimeOfDay) {
        clock.reset(initialTimeOfDay);
        weather = new Weather(initialWeather);
    }

    public void advanceStep() {
        clock.advanceStep();
    }

    public void advanceEnvironment() {
        if (clock.isWeatherUpdateDue()) {
            weather.advance();
        }
        clock.advanceTimeOfDayIfDue();
    }

    public int getStep() {
        return clock.getStep();
    }

    public int getDay() {
        return clock.getDay();
    }

    public int getHour() {
        return clock.getHour();
    }

    @Override
    public Weather getWeather() {
        return weather;
    }

    @Override
    public TimeOfDay getTimeOfDay() {
        return clock.getTimeOfDay();
    }
}
