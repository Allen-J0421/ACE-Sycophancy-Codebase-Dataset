import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Centralizes disease creation, infection state, and transmission rules.
 */
public class DiseaseService
{
    private static final double RANDOM_CONTRACTION_RATE = 0.002;

    private final RandomProvider randomProvider;
    private final Map<Organism, Disease> infections;

    public DiseaseService(RandomProvider randomProvider)
    {
        this.randomProvider = randomProvider;
        this.infections = new IdentityHashMap<>();
    }

    public void initializeOrganism(Organism organism)
    {
        if(organism instanceof Animal) {
            maybeInfectAtCreation(organism);
        }
    }

    public void reset()
    {
        infections.clear();
    }

    public void unregister(Organism organism)
    {
        infections.remove(organism);
    }

    public boolean isDiseased(Organism organism)
    {
        return infections.containsKey(organism);
    }

    public Disease getDisease(Organism organism)
    {
        return infections.get(organism);
    }

    public void infect(Organism organism, Disease disease)
    {
        infections.put(organism, disease);
    }

    public boolean processPreAct(Animal animal)
    {
        if(!animal.isAlive()) {
            return false;
        }

        if(shouldKillByPropagation(animal)) {
            kill(animal);
            return false;
        }

        maybeInfectAtCreation(animal);
        return animal.isAlive();
    }

    public void processPostAct(Animal animal)
    {
        if(animal.isAlive() && shouldKillByLethality(animal)) {
            kill(animal);
        }
    }

    public void applyAdjacentExposure(Animal animal, List<Location> adjacentLocations, Field field)
    {
        for(Location location : adjacentLocations) {
            Object nearby = field.getObjectAt(location);
            if(nearby instanceof Organism organism
                    && !(nearby instanceof Hunter)
                    && isDiseased(organism)
                    && getDisease(organism).getDiseaseType() != DiseaseType.CONTACT
                    && succeeds(getDisease(organism).getPropagationRate())) {
                infect(animal, getDisease(organism));
                break;
            }
        }
    }

    public void applyFoodborneExposure(Animal animal, Organism food)
    {
        if(isDiseased(food)
                && getDisease(food).getDiseaseType() == DiseaseType.FOODBORNE
                && succeeds(getDisease(food).getPropagationRate())) {
            infect(animal, getDisease(food));
        }
    }

    public void applySexualExposure(Animal animal, Animal mate)
    {
        if(isDiseased(mate) && getDisease(mate).getDiseaseType() == DiseaseType.SEXUAL) {
            infect(animal, getDisease(mate));
        }
    }

    public Disease createRandomDisease()
    {
        return new Disease(randomProvider);
    }

    private void maybeInfectAtCreation(Organism organism)
    {
        if(!isDiseased(organism) && succeeds(RANDOM_CONTRACTION_RATE)) {
            infect(organism, createRandomDisease());
        }
    }

    private boolean shouldKillByPropagation(Animal animal)
    {
        return isDiseased(animal) && succeeds(getDisease(animal).getPropagationRate());
    }

    private boolean shouldKillByLethality(Animal animal)
    {
        return isDiseased(animal) && succeeds(getDisease(animal).getLethalityRate());
    }

    private boolean succeeds(double threshold)
    {
        Random rand = randomProvider.getRandom();
        return threshold <= rand.nextDouble();
    }

    private void kill(Organism organism)
    {
        organism.setDead();
        unregister(organism);
    }
}
