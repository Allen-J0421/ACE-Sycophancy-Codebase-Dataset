/**
 * Immutable snapshot of simulation state for rendering.
 */
public class SimulationSnapshot
{
    private final int step;
    private final Field field;
    private final boolean atDayTime;
    private final Weather weather;
    private final double oxygenLevel;
    private final int diseaseDeaths;
    private final PopulationSummary populationSummary;

    public SimulationSnapshot(int step, Field field, boolean atDayTime, Weather weather,
                              double oxygenLevel, int diseaseDeaths,
                              PopulationSummary populationSummary)
    {
        this.step = step;
        this.field = field;
        this.atDayTime = atDayTime;
        this.weather = weather;
        this.oxygenLevel = oxygenLevel;
        this.diseaseDeaths = diseaseDeaths;
        this.populationSummary = populationSummary;
    }

    public int getStep()
    {
        return step;
    }

    public Field getField()
    {
        return field;
    }

    public boolean isAtDayTime()
    {
        return atDayTime;
    }

    public Weather getWeather()
    {
        return weather;
    }

    public double getOxygenLevel()
    {
        return oxygenLevel;
    }

    public int getDiseaseDeaths()
    {
        return diseaseDeaths;
    }

    public PopulationSummary getPopulationSummary()
    {
        return populationSummary;
    }

    public boolean isViable()
    {
        return populationSummary.isViable();
    }
}
