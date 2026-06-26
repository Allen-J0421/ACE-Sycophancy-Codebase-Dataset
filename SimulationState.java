import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable snapshot of the current simulation state.
 */
public class SimulationState
{
    private final int step;
    private final WeatherType weather;
    private final String time;
    private final Field field;
    private final Map<Class<?>, Integer> populationCounts;
    private final int diseasedPopulation;
    private final boolean viable;

    private SimulationState(int step,
            WeatherType weather,
            String time,
            Field field,
            Map<Class<?>, Integer> populationCounts,
            int diseasedPopulation,
            boolean viable)
    {
        this.step = step;
        this.weather = weather;
        this.time = time;
        this.field = field;
        this.populationCounts = Collections.unmodifiableMap(populationCounts);
        this.diseasedPopulation = diseasedPopulation;
        this.viable = viable;
    }

    public static SimulationState capture(int step, Environment environment, Field field)
    {
        DiseaseService diseaseService = field.getDiseaseService();
        Map<Class<?>, Integer> populationCounts = new LinkedHashMap<>();
        for(Class<?> actorClass : SimulationInfo.ALL_ACTORS) {
            populationCounts.put(actorClass, 0);
        }

        int diseasedPopulation = 0;
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object actor = field.getObjectAt(row, col);
                if(actor != null) {
                    populationCounts.merge(actor.getClass(), 1, Integer::sum);
                    if(actor instanceof Organism organism && diseaseService.isDiseased(organism)) {
                        diseasedPopulation++;
                    }
                }
            }
        }

        int livingSpecies = 0;
        for(int count : populationCounts.values()) {
            if(count > 0) {
                livingSpecies++;
            }
        }

        return new SimulationState(
                step,
                environment.getWeather().getCurrentWeather(),
                environment.getTime().getCurrentTimeString(),
                field,
                populationCounts,
                diseasedPopulation,
                livingSpecies > 2
        );
    }

    public int getStep()
    {
        return step;
    }

    public WeatherType getWeather()
    {
        return weather;
    }

    public String getTime()
    {
        return time;
    }

    public Field getField()
    {
        return field;
    }

    public int getPopulationCount(Class<?> actorClass)
    {
        return populationCounts.getOrDefault(actorClass, 0);
    }

    public Map<Class<?>, Integer> getPopulationCounts()
    {
        return populationCounts;
    }

    public int getDiseasedPopulation()
    {
        return diseasedPopulation;
    }

    public boolean isViable()
    {
        return viable;
    }
}
