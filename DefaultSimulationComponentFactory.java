public class DefaultSimulationComponentFactory implements SimulationComponentFactory {

	@Override
	public FieldEnvironment createFieldEnvironment(int depth, int width) {
		return new Field(depth, width);
	}


	@Override
	public EntityController createEntityController(FieldEnvironment fieldEnvironment) {
		return new FieldEntityController(fieldEnvironment);
	}


	@Override
	public Climate createClimate(Weather initialWeather) {
		return new Climate(initialWeather);
	}


	@Override
	public FieldPopulator createFieldPopulator() {
		return new FieldPopulator();
	}
}
