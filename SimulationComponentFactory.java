public interface SimulationComponentFactory {

	FieldEnvironment createFieldEnvironment(int depth, int width);


	EntityController createEntityController(FieldEnvironment fieldEnvironment);


	Climate createClimate(Weather initialWeather);


	FieldPopulator createFieldPopulator();
}
