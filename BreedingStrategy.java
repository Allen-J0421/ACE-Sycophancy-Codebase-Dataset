/**
 * Strategy controlling whether an organism is eligible to breed.
 *
 * @version 2022.03.02
 */
public interface BreedingStrategy
{
    boolean canBreed(Organism organism);
}
