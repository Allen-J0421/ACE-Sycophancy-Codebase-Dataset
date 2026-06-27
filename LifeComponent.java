/**
 * Component that tracks whether an entity is alive.
 */
public final class LifeComponent implements EntityComponent
{
    private boolean alive;

    public LifeComponent()
    {
        alive = true;
    }

    public boolean isAlive()
    {
        return alive;
    }

    public boolean kill()
    {
        boolean wasAlive = alive;
        alive = false;
        return wasAlive;
    }
}
