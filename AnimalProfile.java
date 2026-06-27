/**
 * Immutable configuration for core animal lifecycle values.
 */
public record AnimalProfile(int maxAge, int randomFoodUpperBound,
                            int newbornFoodLevel, int foodValue)
{
}
