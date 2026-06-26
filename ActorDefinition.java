import java.awt.Color;
import java.util.Random;

/**
 * Defines how a type of actor is displayed and initially populated.
 */
public class ActorDefinition
{
    private final Class<? extends Actor> actorClass;
    private final double creationProbability;
    private final Color color;
    private final ActorFactory factory;

    public ActorDefinition(Class<? extends Actor> actorClass,
                           double creationProbability,
                           Color color,
                           ActorFactory factory)
    {
        this.actorClass = actorClass;
        this.creationProbability = creationProbability;
        this.color = color;
        this.factory = factory;
    }

    public boolean shouldCreate(Random rand)
    {
        return rand.nextDouble() <= creationProbability;
    }

    public Actor create(Field field, Location location)
    {
        return factory.create(field, location);
    }

    public Class<? extends Actor> getActorClass()
    {
        return actorClass;
    }

    public Color getColor()
    {
        return color;
    }
}

@FunctionalInterface
interface ActorFactory
{
    Actor create(Field field, Location location);
}
