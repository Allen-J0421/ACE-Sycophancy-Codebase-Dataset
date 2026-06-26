/**
 * Generic factory for creating actors from explicit species definitions.
 */
public class ActorFactory<T extends Actor, S extends SpawnableActorType<T>>
{
    private final Field field;

    public ActorFactory(Field field)
    {
        this.field = field;
    }

    public T create(S species, Location location)
    {
        if(species == null) {
            return null;
        }
        return species.createRandom(field, location);
    }
}
