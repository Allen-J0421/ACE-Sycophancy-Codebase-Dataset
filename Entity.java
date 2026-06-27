import java.util.HashMap;
import java.util.Map;

/**
 * Base type for component-backed simulation entities.
 */
public abstract class Entity
{
    private final Map<Class<? extends EntityComponent>, EntityComponent> components;

    protected Entity()
    {
        components = new HashMap<>();
    }

    protected final <T extends EntityComponent> T registerComponent(T component)
    {
        components.put(component.getClass(), component);
        return component;
    }

    protected final <T extends EntityComponent> T getComponent(Class<T> componentType)
    {
        EntityComponent component = components.get(componentType);
        if(component == null) {
            return null;
        }
        return componentType.cast(component);
    }
}
