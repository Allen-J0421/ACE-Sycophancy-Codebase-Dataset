import java.util.List;

/**
 * Abstract base class for weather conditions.
 * Each subclass encapsulates the effect of one weather type on the simulation.
 */
public abstract class WeatherEvent {
    public abstract String getName();
    public abstract void apply(List<Animal> animals, List<Plant> plants);

    protected void resetFlags(List<Animal> animals, List<Plant> plants) {
        for(int i = 0; i < animals.size(); i++) {
            animals.get(i).resetFog();
        }
        for(int i = 0; i < plants.size(); i++) {
            plants.get(i).resetRain();
        }
    }
}
