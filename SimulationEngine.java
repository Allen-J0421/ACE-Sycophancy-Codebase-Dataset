package savannah.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import savannah.config.SimulationConfig;
import savannah.model.Animal;
import savannah.model.Cheetah;
import savannah.model.Field;
import savannah.model.Giraffe;
import savannah.model.Lemur;
import savannah.model.Lion;
import savannah.model.LivingOrganism;
import savannah.model.Location;
import savannah.model.Plant;
import savannah.model.Randomizer;
import savannah.model.Time;
import savannah.model.Weather;
import savannah.model.Zebra;

/**
 * Owns the simulation state and advances the world one step at a time.
 */
public class SimulationEngine
{
    private final SimulationConfig config;
    private final List<LivingOrganism> animals;
    private final List<LivingOrganism> plants;
    private final Field field;

    public SimulationEngine(int depth, int width, SimulationConfig config)
    {
        this.config = config;

        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = config.defaultDepth;
            width = config.defaultWidth;
        }

        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);
        reset();
    }

    public Field getField()
    {
        return field;
    }

    public int getDepth()
    {
        return field.getDepth();
    }

    public int getWidth()
    {
        return field.getWidth();
    }

    public void reset()
    {
        Time.resetStep();
        animals.clear();
        plants.clear();
        populate();
    }

    public void step()
    {
        Time.incrementStep();
        Weather.updateWeather();

        List<LivingOrganism> newAnimals = new ArrayList<>();
        List<LivingOrganism> newPlants = new ArrayList<>();

        for (Iterator<LivingOrganism> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = (Animal) it.next();
            animal.act(newAnimals);

            if (!animal.isAlive()) {
                it.remove();
            }
        }

        for (Iterator<LivingOrganism> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = (Plant) it.next();
            plant.act(newPlants);

            if (!plant.isAlive()) {
                it.remove();
            }
        }

        animals.addAll(newAnimals);
        plants.addAll(newPlants);
    }

    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                plants.add(new Plant(true, field, location, config));

                Animal animal = createInitialAnimal(location, rand);
                if (animal != null) {
                    animals.add(animal);
                }
            }
        }
    }

    private Animal createInitialAnimal(Location location, Random rand)
    {
        if (rand.nextDouble() <= config.lionCreationProbability) {
            return new Lion(true, field, location, false, false, config);
        }
        if (rand.nextDouble() <= config.cheetahCreationProbability) {
            return new Cheetah(true, field, location, false, false, config);
        }
        if (rand.nextDouble() <= config.zebraCreationProbability) {
            return new Zebra(true, field, location, false, false, config);
        }
        if (rand.nextDouble() <= config.giraffeCreationProbability) {
            return new Giraffe(true, field, location, false, false, config);
        }
        if (rand.nextDouble() <= config.lemurCreationProbability) {
            return new Lemur(true, field, location, false, false, config);
        }

        return null;
    }
}
