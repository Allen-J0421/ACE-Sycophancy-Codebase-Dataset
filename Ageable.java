/**
 * Mixin for organisms whose lifecycle advances over time.
 *
 * @version 26/02/2022
 */
public interface Ageable extends OrganismContext
{
    void applyAgeProgression();

    default boolean shouldDieFromAge()
    {
        return false;
    }

    default void ageOneStep()
    {
        applyAgeProgression();
        if(shouldDieFromAge()) {
            markDead();
        }
    }
}
