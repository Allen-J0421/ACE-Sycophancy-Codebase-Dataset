/**
 * Centralizes the event rules that control time-of-day and weather changes.
 *
 * @version 2022.03.02
 */
public class SimulationRulesEngine
{
    private static final int DAY_NIGHT_CYCLE_LENGTH = 80;
    private static final int DAYLIGHT_DURATION = 55;
    private static final int WEATHER_CHANGE_INTERVAL = 450;

    private final Weather weather;
    private final SimulationEventBus eventBus;
    private TimeOfDay currentTimeOfDay;

    public SimulationRulesEngine(Weather weather, SimulationEventBus eventBus)
    {
        this.weather = weather;
        this.eventBus = eventBus;
        currentTimeOfDay = null;
        registerListeners();
    }

    /**
     * Register bus listeners for environment changes.
     */
    private void registerListeners()
    {
        eventBus.subscribe(SimulationStepStartedEvent.class,
                           new SimulationEventListener() {
                               public void onEvent(SimulationEvent event)
                               {
                                   SimulationStepStartedEvent started =
                                       (SimulationStepStartedEvent) event;
                                   handleStepStarted(started.getStep());
                               }
                           });
        eventBus.subscribe(SimulationResetRequestedEvent.class,
                           new SimulationEventListener() {
                               public void onEvent(SimulationEvent event)
                               {
                                   reset();
                               }
                           });
        eventBus.subscribe(WeatherChangeRequestedEvent.class,
                           new SimulationEventListener() {
                               public void onEvent(SimulationEvent event)
                               {
                                   WeatherChangeRequestedEvent request =
                                       (WeatherChangeRequestedEvent) event;
                                   setWeather(request.getWeatherName());
                               }
                           });
    }

    /**
     * Apply step-based environment events such as periodic weather changes.
     *
     * @param step The simulation step being executed.
     */
    public void handleStepStarted(int step)
    {
        updateTimeOfDay(step);
        if(step % WEATHER_CHANGE_INTERVAL == 0) {
            String previousWeather = weather.getCurrentWeather();
            weather.changeWeather();
            publishWeatherChanged(previousWeather, weather.getCurrentWeather());
        }
    }

    /**
     * Determine whether an organism should act during the current step.
     *
     * @param organism The organism being considered.
     * @param step The current simulation step.
     * @return True if the organism is active this step.
     */
    public boolean canAct(Organism organism, int step)
    {
        return getTimeOfDay(step).matches(organism.isDiurnal());
    }

    /**
     * Return the time-of-day phase for the given step.
     *
     * @param step The current simulation step.
     * @return The time-of-day phase.
     */
    public TimeOfDay getTimeOfDay(int step)
    {
        if (step % DAY_NIGHT_CYCLE_LENGTH <= DAYLIGHT_DURATION) {
            return TimeOfDay.DAY;
        }
        return TimeOfDay.NIGHT;
    }

    /**
     * Return the weather label for the current environment.
     *
     * @return The current weather name.
     */
    public String getCurrentWeather()
    {
        return weather.getCurrentWeather();
    }

    /**
     * Set the weather explicitly.
     *
     * @param weatherName The new weather to apply.
     */
    public void setWeather(String weatherName)
    {
        String previousWeather = weather.getCurrentWeather();
        weather.setWeather(weatherName);
        publishWeatherChanged(previousWeather, weatherName);
    }

    /**
     * Reset the event rules to their initial state.
     */
    public void reset()
    {
        currentTimeOfDay = null;
        updateTimeOfDay(0);
        String previousWeather = weather.getCurrentWeather();
        weather.resetWeather();
        publishWeatherChanged(previousWeather, weather.getCurrentWeather());
    }

    private void updateTimeOfDay(int step)
    {
        TimeOfDay nextTimeOfDay = getTimeOfDay(step);
        if(currentTimeOfDay != nextTimeOfDay) {
            TimeOfDay previousTimeOfDay = currentTimeOfDay;
            currentTimeOfDay = nextTimeOfDay;
            if(previousTimeOfDay != null) {
                eventBus.publish(new TimeOfDayChangedEvent(step, previousTimeOfDay,
                                                           nextTimeOfDay));
            }
        }
    }

    private void publishWeatherChanged(String previousWeather, String nextWeather)
    {
        if(previousWeather == null || !previousWeather.equals(nextWeather)) {
            eventBus.publish(new WeatherChangedEvent(previousWeather, nextWeather));
        }
    }
}
