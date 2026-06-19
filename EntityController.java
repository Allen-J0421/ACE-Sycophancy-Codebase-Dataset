public interface EntityController {

	FieldEnvironment getFieldEnvironment();


	void place(Entity entity, Location location);


	void move(Entity entity, Location location);


	void remove(Entity entity);
}
