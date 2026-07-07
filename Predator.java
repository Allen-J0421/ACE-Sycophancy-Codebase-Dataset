import java.util.ArrayList;

/**
 * A class representing the characteristics of a predator.
 * They are different from other animals because they eat any other animal or
 * predator whose strength is weaker. Movement, food-finding, and reproduction
 * are delegated to strategy objects set in the constructor.
 *
 * @version 2022.03.01
 */
public class Predator extends Animal
{
    // The predator's strength, used when evaluating horde attacks.
    private final int strength;

    /**
     * Create a new predator with given specifications.
     *
     * @param strength (int) the predator's strength
     * @param field (Field) the field where the simulation takes place
     * @param location (Location) the Location at which the predator should appear
     * @param name (String) the predator's name (its species' name)
     * @param maximumTemperature (int) the maximum temperature the predator can survive to
     * @param minimumTemperature (int) the minimum temperature the predator can survive to
     * @param nutritionalValue (int) the predator's nutritional value
     * @param reproductionProbability (double) the probability that the predator reproduces
     * @param maxAge (int) the predator's life expectancy
     * @param breedingAge (int) the age at which predator can start to reproduce
     * @param maxLitterSize (int) the maximum number of children at once
     * @param randomAge (boolean) whether the predator should start with a random age
     * @param hibernates (boolean) whether the predator is able to hibernate
     * @param isNocturnal (boolean) whether the predator is more active at night
     */
    public Predator(int strength, Field field, Location location, String name,
                    int maximumTemperature, int minimumTemperature, int nutritionalValue,
                    double reproductionProbability, int maxAge, int breedingAge,
                    int maxLitterSize, boolean randomAge, boolean hibernates, boolean isNocturnal)
    {
        super(field, location, name, maximumTemperature, minimumTemperature, nutritionalValue,
                reproductionProbability, maxAge, breedingAge, maxLitterSize, randomAge,
                hibernates, isNocturnal);

        this.strength = strength;
        this.foodStrategy = new PredatorFoodStrategy();
        this.reproductionStrategy = new PredatorReproductionStrategy(strength);
    }

    /**
     * Pre-move hook: check whether this predator is being attacked by a horde of
     * rival predators. If the horde overpowers this predator, it dies here and
     * makeMove() returns immediately.
     *
     * @param neighbors (ArrayList<Animal>) Live animals in adjacent cells.
     */
    @Override
    protected void beforeMove(ArrayList<Animal> neighbors)
    {
        checkForAttack(neighbors);
    }

    /**
     * Check if this predator is under attack from a horde of a rival predator
     * species. If the horde's combined strength exceeds this predator's strength,
     * the horde kills and shares this predator's nutritional value.
     *
     * @param neighboringAnimals (ArrayList<Animal>) A list of neighboring animals.
     */
    private void checkForAttack(ArrayList<Animal> neighboringAnimals)
    {
        ArrayList<Predator> hordeMembers = new ArrayList<>();

        for (int i = 0; i < neighboringAnimals.size(); i++) {
            if (neighboringAnimals.get(i) instanceof Predator) {
                Predator neighboringPredator = (Predator) neighboringAnimals.get(i);
                String nameOfInvestigatedHorde = neighboringPredator.getName();

                if (!this.getName().equals(nameOfInvestigatedHorde)) {
                    int totalHordeStrength = neighboringPredator.getStrength();
                    hordeMembers.add(neighboringPredator);

                    for (int j = 0; j < neighboringAnimals.size(); j++) {
                        if (nameOfInvestigatedHorde.equals(neighboringAnimals.get(j).getName())) {
                            Predator predatorObject = (Predator) neighboringAnimals.get(i);
                            totalHordeStrength += predatorObject.getStrength();
                            hordeMembers.add(predatorObject);
                        }
                    }

                    if (totalHordeStrength > strength) {
                        attackedByHorde(hordeMembers);
                        break;
                    } else {
                        hordeMembers.clear();
                    }
                }
            }
        }
    }

    /**
     * This predator is killed by a horde. Its nutritional value is shared equally
     * among the horde members.
     *
     * @param hordeMembers (ArrayList<Predator>) The attacking predators.
     */
    private void attackedByHorde(ArrayList<Predator> hordeMembers)
    {
        int foodLevelAddedToEachHordeMember = this.getNutritionalValue() / hordeMembers.size();
        for (Predator predator : hordeMembers) {
            predator.incrementFoodLevel(foodLevelAddedToEachHordeMember);
        }
        this.setDead();
    }

    /**
     * @return (int) the predator's strength.
     */
    public int getStrength()
    {
        return strength;
    }
}
