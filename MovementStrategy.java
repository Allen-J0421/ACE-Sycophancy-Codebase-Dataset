/**
 * Strategy for moving an animal during its action step.
 */
public interface MovementStrategy
{
    void move(Animal animal, Environment environment);
}
