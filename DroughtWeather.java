import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DroughtWeather extends WeatherEvent {
    public String getName() { return "drought"; }

    public void apply(List<Animal> animals, List<Plant> plants) {
        resetFlags(animals, plants);
        List<Plant> randomPlants = new ArrayList<>();
        for(int i = 0; i < plants.size(); i++) {
            randomPlants.add(plants.get(i));
        }
        Collections.shuffle(randomPlants);
        for(int i = 0; i < (plants.size() / 5); i++) {
            plants.remove(plants.get(i));
        }
    }
}
