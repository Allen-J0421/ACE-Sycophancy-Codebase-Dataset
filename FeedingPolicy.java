/**
 * Strategy for applying prey-consumption side effects.
 */
public interface FeedingPolicy
{
    void feed(Animal consumer, Organism prey);
}
