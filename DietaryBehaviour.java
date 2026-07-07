/**
 * Strategy interface for an animal's foraging behaviour.
 * Encapsulates what constitutes food and how it is found and consumed.
 */
public interface DietaryBehaviour
{
    /**
     * Search the cells adjacent to {@code self} for food and eat it if found.
     *
     * @param self  The animal that is foraging.
     * @param field The field being searched.
     * @param rand  The shared random generator.
     * @return The location where food was found, or null.
     */
    Location findFood(Animal self, Field field, SimRandom rand);
}
