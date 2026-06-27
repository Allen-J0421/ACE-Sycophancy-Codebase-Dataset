/**
 * Event types for simulation lifecycle, state, and environment changes.
 *
 * @version 2022.03.02
 */
abstract class SimulationStateEvent implements SimulationEvent
{
    private final SimulationState state;

    protected SimulationStateEvent(SimulationState state)
    {
        this.state = state;
    }

    public SimulationState getState()
    {
        return state;
    }
}

final class SimulationStepStartedEvent implements SimulationEvent
{
    private final int step;

    public SimulationStepStartedEvent(int step)
    {
        this.step = step;
    }

    public int getStep()
    {
        return step;
    }
}

final class SimulationStepCompletedEvent extends SimulationStateEvent
{
    public SimulationStepCompletedEvent(SimulationState state)
    {
        super(state);
    }
}

final class SimulationResetRequestedEvent implements SimulationEvent
{
}

final class SimulationResetCompletedEvent extends SimulationStateEvent
{
    public SimulationResetCompletedEvent(SimulationState state)
    {
        super(state);
    }
}

final class WeatherChangeRequestedEvent implements SimulationEvent
{
    private final String weatherName;

    public WeatherChangeRequestedEvent(String weatherName)
    {
        this.weatherName = weatherName;
    }

    public String getWeatherName()
    {
        return weatherName;
    }
}

final class WeatherChangedEvent implements SimulationEvent
{
    private final String previousWeather;
    private final String currentWeather;

    public WeatherChangedEvent(String previousWeather, String currentWeather)
    {
        this.previousWeather = previousWeather;
        this.currentWeather = currentWeather;
    }

    public String getPreviousWeather()
    {
        return previousWeather;
    }

    public String getCurrentWeather()
    {
        return currentWeather;
    }
}

final class TimeOfDayChangedEvent implements SimulationEvent
{
    private final int step;
    private final TimeOfDay previousTimeOfDay;
    private final TimeOfDay currentTimeOfDay;

    public TimeOfDayChangedEvent(int step, TimeOfDay previousTimeOfDay,
                                 TimeOfDay currentTimeOfDay)
    {
        this.step = step;
        this.previousTimeOfDay = previousTimeOfDay;
        this.currentTimeOfDay = currentTimeOfDay;
    }

    public int getStep()
    {
        return step;
    }

    public TimeOfDay getPreviousTimeOfDay()
    {
        return previousTimeOfDay;
    }

    public TimeOfDay getCurrentTimeOfDay()
    {
        return currentTimeOfDay;
    }
}

enum BirthSource
{
    INITIAL_POPULATION,
    REPRODUCTION
}

final class OrganismBornEvent implements SimulationEvent
{
    private final Species species;
    private final Location location;
    private final BirthSource birthSource;

    public OrganismBornEvent(Species species, Location location,
                             BirthSource birthSource)
    {
        this.species = species;
        this.location = location;
        this.birthSource = birthSource;
    }

    public Species getSpecies()
    {
        return species;
    }

    public Location getLocation()
    {
        return location;
    }

    public BirthSource getBirthSource()
    {
        return birthSource;
    }
}

final class OrganismDiedEvent implements SimulationEvent
{
    private final Species species;
    private final Location location;

    public OrganismDiedEvent(Species species, Location location)
    {
        this.species = species;
        this.location = location;
    }

    public Species getSpecies()
    {
        return species;
    }

    public Location getLocation()
    {
        return location;
    }
}
