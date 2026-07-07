/**
 * Factory responsible for instantiating Animal subclasses.
 * Centralises the mapping from animal type to constructor call so
 * that Animal does not need to know about every concrete species.
 */
public class AnimalFactory
{
    /**
     * Create a newborn animal of the given type and place it in the field.
     *
     * @param classOfAnimal The runtime class of the species to create.
     * @param field         The field the offspring will inhabit.
     * @param loc           The location to place the offspring.
     * @param infected      Whether the offspring starts infected.
     * @param immune        Whether the offspring starts immune.
     * @return A new Animal instance, or null if the type is unrecognised.
     */
    public static Animal createOffspring(Class classOfAnimal, Field field, Location loc,
                                         boolean infected, boolean immune)
    {
        if (classOfAnimal.equals(Lemur.class))
        {
            return new Lemur(false, field, loc, infected, immune);
        }
        else if (classOfAnimal.equals(Giraffe.class))
        {
            return new Giraffe(false, field, loc, infected, immune);
        }
        else if (classOfAnimal.equals(Zebra.class))
        {
            return new Zebra(false, field, loc, infected, immune);
        }
        else if (classOfAnimal.equals(Cheetah.class))
        {
            return new Cheetah(false, field, loc, infected, immune);
        }
        else if (classOfAnimal.equals(Lion.class))
        {
            return new Lion(false, field, loc, infected, immune);
        }
        return null;
    }
}
