package safari;

import java.util.Map;

/**
 * Immutable configuration for one species.
 */
record SpeciesConfig(
    SpeciesType type,
    int breedingAge,
    int maxAge,
    double breedingProbability,
    int maxLitterSize,
    int maxTimeUntilBreedingAgain,
    int initialFoodLevel,
    int randomFoodUpperBound,
    double maxFoodLevel,
    double initialGrowthScale,
    double actGrowthIncrement,
    Map<Weather, Double> foodFindingProbabilities,
    Map<ActorKind, Integer> food
)
{
    SpeciesConfig
    {
        foodFindingProbabilities = Map.copyOf(foodFindingProbabilities);
        food = Map.copyOf(food);
    }

    double foodFindingProbability(Weather weather)
    {
        return foodFindingProbabilities.getOrDefault(weather, 0.0);
    }
}
