/**
 * Owns the live animal and plant populations in the simulation.
 */
public class SimulationPopulation
{
    private final ActorPopulation<Animal> animals;
    private final ActorPopulation<Plant> plants;

    public SimulationPopulation()
    {
        animals = new ActorPopulation<>(Animal.class);
        plants = new ActorPopulation<>(Plant.class);
    }

    public void addAnimal(Animal animal)
    {
        animals.add(animal);
    }

    public void addPlant(Plant plant)
    {
        plants.add(plant);
    }

    public void clear()
    {
        animals.clear();
        plants.clear();
    }

    public void infectInitialAnimals(int count)
    {
        int infectedCount = Math.min(count, animals.size());
        for (int i = 0; i < infectedCount; i++) {
            animals.get(i).setInfectionTimestamp(0);
        }
    }

    public void simulateStep(Weather weather, DayState dayState)
    {
        plants.simulateStep(weather, dayState);
        animals.simulateStep(weather, dayState);
    }
}
