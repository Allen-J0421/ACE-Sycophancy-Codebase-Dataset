import java.util.HashMap;
import java.util.Map;

/**
 * Factory responsible for instantiating Animal subclasses.
 * Species are registered in a static Map keyed on their Class; adding a
 * new species requires only one extra line in the REGISTRY block — no
 * changes to createOffspring itself.
 */
public class AnimalFactory
{
    @FunctionalInterface
    private interface OffspringCreator
    {
        Animal create(Field field, Location loc, boolean infected, boolean immune);
    }

    private static final Map<Class<?>, OffspringCreator> REGISTRY = new HashMap<>();

    static
    {
        REGISTRY.put(Lemur.class,   (f, l, inf, imm) -> new Lemur  (false, f, l, inf, imm));
        REGISTRY.put(Giraffe.class, (f, l, inf, imm) -> new Giraffe(false, f, l, inf, imm));
        REGISTRY.put(Zebra.class,   (f, l, inf, imm) -> new Zebra  (false, f, l, inf, imm));
        REGISTRY.put(Cheetah.class, (f, l, inf, imm) -> new Cheetah(false, f, l, inf, imm));
        REGISTRY.put(Lion.class,    (f, l, inf, imm) -> new Lion   (false, f, l, inf, imm));
    }

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
    public static Animal createOffspring(Class<?> classOfAnimal, Field field, Location loc,
                                         boolean infected, boolean immune)
    {
        OffspringCreator creator = REGISTRY.get(classOfAnimal);
        return creator != null ? creator.create(field, loc, infected, immune) : null;
    }
}
