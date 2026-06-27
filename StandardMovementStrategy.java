/**
 * The default movement strategy for animals.
 *
 * Priority order:
 *   1. Seek water when critically thirsty (waterLevel below threshold).
 *   2. Seek food when hungry (foodLevel below threshold).
 *   3. Move toward a mate.
 *   4. Wander to any free adjacent cell.
 *
 * Returns null only when every adjacent cell is occupied, causing the caller
 * to mark the animal as dead due to overcrowding.
 *
 * @version 2022.03.01
 */
public class StandardMovementStrategy implements MovementStrategy
{
    @Override
    public Location selectDestination(Animal animal)
    {
        if(animal.getWaterLevel() < SimulationConfiguration.WATER_URGENCY_THRESHOLD) {
            Location loc = animal.findWater();
            if(loc != null) return loc;
        }
        if(animal.getFoodLevel() < SimulationConfiguration.FOOD_URGENCY_THRESHOLD) {
            Location loc = animal.findFood(animal.getPrey());
            if(loc != null) return loc;
        }
        Location loc = animal.findMate();
        if(loc != null) return loc;
        return animal.getField().freeAdjacentLocation(animal.getLocation());
    }
}
