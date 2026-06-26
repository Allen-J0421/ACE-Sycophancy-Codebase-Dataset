/**
 * Mixin for organisms whose lifetime is age-limited.
 *
 * @version 26/02/2022
 */
public interface Ageable extends OrganismContext
{
    AgeState getAgeState();

    default void ageOneStep()
    {
        AgeState ageState = getAgeState();
        ageState.setAge(ageState.getAge() + 1);

        if(ageState.getAge() > ageState.getMaxAge()) {
            markDead();
        }
    }
}
