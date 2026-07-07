import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FloodWeather extends WeatherEvent {
    public String getName() { return "flood"; }

    public void apply(List<Animal> animals, List<Plant> plants) {
        resetFlags(animals, plants);
        List<Animal> randomRatsAnts = new ArrayList<>();
        for(int i = 0; i < animals.size(); i++) {
            if(animals.get(i) instanceof Ant || animals.get(i) instanceof Rat) {
                randomRatsAnts.add(animals.get(i));
            }
        }
        Collections.shuffle(randomRatsAnts);
        for(int i = 0; i < (randomRatsAnts.size() / 5); i++) {
            animals.get(i).setDead();
        }
    }
}
