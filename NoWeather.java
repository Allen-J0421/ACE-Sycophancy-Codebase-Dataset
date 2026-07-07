import java.util.List;

public class NoWeather extends WeatherEvent {
    public String getName() { return "none"; }

    public void apply(List<Animal> animals, List<Plant> plants) {
        // No effect.
    }
}
