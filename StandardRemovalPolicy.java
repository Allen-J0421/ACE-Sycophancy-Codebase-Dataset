/**
 * Default removal behavior for prey death.
 */
public final class StandardRemovalPolicy implements RemovalPolicy
{
    @Override
    public void remove(Organism prey)
    {
        if(prey.isAlive()) {
            prey.setDead();
        }
    }
}
