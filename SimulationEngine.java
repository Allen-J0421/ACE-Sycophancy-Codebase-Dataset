import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Controls the time-stepped simulation: owns the organism lists and the
 * field, advances simulation state each step, and repopulates on reset.
 * All GUI interaction is handled externally by {@link Simulator}.
 */
public class SimulationEngine
{
    // The probability that a given species will be created in any grid position.
    private static final double LION_CREATION_PROBABILITY    = 0.0125;
    private static final double CHEETAH_CREATION_PROBABILITY = 0.0125;
    private static final double ZEBRA_CREATION_PROBABILITY   = 0.08;
    private static final double GIRAFFE_CREATION_PROBABILITY = 0.08;
    private static final double LEMUR_CREATION_PROBABILITY   = 0.081;

    private final List<LivingOrganism> animals;
    private final List<LivingOrganism> plants;
    private final Field field;

    /**
     * Create an engine for a field of the given dimensions.
     *
     * @param depth Depth of the field.
     * @param width Width of the field.
     */
    public SimulationEngine(int depth, int width)
    {
        animals = new ArrayList<>();
        plants  = new ArrayList<>();
        field   = new Field(depth, width);
    }

    /** @return The field managed by this engine. */
    public Field getField()
    {
        return field;
    }

    /**
     * Clear the organism lists and repopulate the field from scratch.
     * Time step is reset externally before calling this.
     */
    public void reset()
    {
        animals.clear();
        plants.clear();
        populate();
    }

    /**
     * Advance the simulation by one step: update time and weather, let
     * every organism act, then merge newly born organisms into the lists.
     * The caller is responsible for updating the view afterwards.
     */
    public void simulateOneStep()
    {
        Time.incrementStep();
        Weather.updateWeather();

        List<LivingOrganism> newAnimals = new ArrayList<>();
        List<LivingOrganism> newPlants  = new ArrayList<>();

        for (Iterator<LivingOrganism> it = animals.iterator(); it.hasNext(); )
        {
            Animal animal = (Animal) it.next();
            if (animal != null)
            {
                animal.act(newAnimals);
                if (!animal.isAlive())
                {
                    it.remove();
                }
            }
        }

        for (Iterator<LivingOrganism> it = plants.iterator(); it.hasNext(); )
        {
            Plant plant = (Plant) it.next();
            if (plant != null)
            {
                plant.act(newPlants);
                if (!plant.isAlive())
                {
                    it.remove();
                }
            }
        }

        animals.addAll(newAnimals);
        plants.addAll(newPlants);
    }

    /**
     * Randomly populate the field with predators, prey, and plants.
     */
    private void populate()
    {
        SimRandom rand = Randomizer.getRandom();
        field.clear();

        for (int row = 0; row < field.getDepth(); row++)
        {
            for (int col = 0; col < field.getWidth(); col++)
            {
                Location location = new Location(row, col);

                Plant plant = new Plant(true, field, location);
                plants.add(plant);

                Animal animal = null;

                if (rand.nextDouble() <= LION_CREATION_PROBABILITY)
                {
                    animal = new Lion(true, field, location, false, false);
                }
                else if (rand.nextDouble() <= CHEETAH_CREATION_PROBABILITY)
                {
                    animal = new Cheetah(true, field, location, false, false);
                }
                else if (rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY)
                {
                    animal = new Zebra(true, field, location, false, false);
                }
                else if (rand.nextDouble() <= GIRAFFE_CREATION_PROBABILITY)
                {
                    animal = new Giraffe(true, field, location, false, false);
                }
                else if (rand.nextDouble() <= LEMUR_CREATION_PROBABILITY)
                {
                    animal = new Lemur(true, field, location, false, false);
                }

                if (animal != null)
                {
                    animals.add(animal);
                }
            }
        }
    }
}
