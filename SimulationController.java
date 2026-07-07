import java.util.*;

/**
 * Manages the simulation field, the collections of active animals and plants,
 * and their per-step lifecycle (act, remove dead, add newborns).
 */
public class SimulationController {
    private final SimulationConfig config;
    private final Field field;
    private final List<Animal> animals;
    private final List<Plant> plants;

    public SimulationController(SimulationConfig config) {
        this.config = config;
        this.field = new Field(config.depth, config.width);
        this.animals = new ArrayList<>();
        this.plants = new ArrayList<>();
    }

    public Field getField() { return field; }
    public List<Animal> getAnimals() { return animals; }
    public List<Plant> getPlants() { return plants; }

    /**
     * Clear the animal list and field, then randomly populate with new entities.
     */
    public void reset() {
        animals.clear();
        populate();
    }

    /**
     * Advance all animals by one step: each acts, dead ones are removed, newborns added.
     * @param time the current simulation time of day
     */
    public void stepAnimals(int time) {
        List<Animal> newAnimals = new ArrayList<>();
        animals.forEach(a -> a.act(newAnimals, time));
        animals.removeIf(a -> !a.isAlive());
        animals.addAll(newAnimals);
    }

    /**
     * Advance all plants by one step: each acts, dead ones are removed, newborns added.
     */
    public void stepPlants() {
        List<Plant> newPlants = new ArrayList<>();
        plants.forEach(p -> p.act(newPlants));
        plants.removeIf(p -> !p.isAlive());
        plants.addAll(newPlants);
    }

    /**
     * Randomly populate the field with animals and plants according to config probabilities.
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if(rand.nextDouble() <= config.dingoProbability) {
                    animals.add(new Dingo(true, field, location));
                }
                else if(rand.nextDouble() <= config.antProbability) {
                    animals.add(new Ant(true, field, location));
                }
                else if(rand.nextDouble() <= config.snakeProbability) {
                    animals.add(new Snake(true, field, location));
                }
                else if(rand.nextDouble() <= config.ratProbability) {
                    animals.add(new Rat(true, field, location));
                }
                else if(rand.nextDouble() <= config.eagleProbability) {
                    animals.add(new Eagle(true, field, location));
                }
                else if(rand.nextDouble() <= config.emuProbability) {
                    animals.add(new Emu(true, field, location));
                }
                else if(rand.nextDouble() <= config.acaciaProbability) {
                    plants.add(new Acacia(field, location));
                }
                else if(rand.nextDouble() <= config.grassProbability) {
                    plants.add(new Grass(field, location));
                }
                // else leave the location empty.
            }
        }
    }
}
