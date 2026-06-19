public interface EntityHandler<T extends Entity> {

	Class<T> getEntityType();


	void place(FieldEnvironment fieldEnvironment, T entity, Location location);


	void clear(FieldEnvironment fieldEnvironment, Location location);
}
