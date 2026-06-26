package savannah.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import savannah.config.SimulationConfig;
import savannah.model.Animal;
import savannah.model.Field;
import savannah.model.LivingOrganism;
import savannah.model.Location;
import savannah.model.Plant;
import savannah.model.Randomizer;
import savannah.model.SpeciesType;
import savannah.model.Time;
import savannah.model.Weather;

/**
 * Owns the simulation state and advances the world one step at a time.
 */
public class SimulationEngine
{
    private final SimulationContext context;
    private final List<LivingOrganism> animals;
    private final List<LivingOrganism> plants;

    public SimulationEngine(int depth, int width, SimulationConfig config)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = config.defaultDepth;
            width = config.defaultWidth;
        }

        animals = new ArrayList<>();
        plants = new ArrayList<>();
        context = new SimulationContext(new Field(depth, width), config);
        context.setEngine(this);
        reset();
    }

    public Field getField()
    {
        return context.getField();
    }

    public int getDepth()
    {
        return context.getField().getDepth();
    }

    public int getWidth()
    {
        return context.getField().getWidth();
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
        Field field = context.getField();
        field.clear();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                plants.add(SpeciesType.PLANT.createPlant(context, true, location));

                Animal animal = createInitialAnimal(location, rand);
                if (animal != null) {
                    animals.add(animal);
                }
            }
        }
    }

    private Animal createInitialAnimal(Location location, Random rand)
    {
        savannah.config.SimulationConfig config = context.getConfig();
        if (rand.nextDouble() <= config.lionCreationProbability) {
            return SpeciesType.LION.createAnimal(context, location, true, false, false);
        }
        if (rand.nextDouble() <= config.cheetahCreationProbability) {
            return SpeciesType.CHEETAH.createAnimal(context, location, true, false, false);
        }
        if (rand.nextDouble() <= config.zebraCreationProbability) {
            return SpeciesType.ZEBRA.createAnimal(context, location, true, false, false);
        }
        if (rand.nextDouble() <= config.giraffeCreationProbability) {
            return SpeciesType.GIRAFFE.createAnimal(context, location, true, false, false);
        }
        if (rand.nextDouble() <= config.lemurCreationProbability) {
            return SpeciesType.LEMUR.createAnimal(context, location, true, false, false);
        }

        return null;
    }
}
