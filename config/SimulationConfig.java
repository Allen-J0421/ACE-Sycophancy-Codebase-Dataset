package config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Externalised simulation parameters, loaded from a {@code .properties} file so
 * they can be tuned without recompiling. The class is deliberately generic — it
 * deals only in names, numbers, and ordered weighting rules, and knows nothing
 * about concrete species — so the composition root maps the parsed names onto
 * real factories.
 *
 * <p>Expected keys (all optional; anything omitted falls back to the built-in
 * defaults, which reproduce the original hardcoded setup):
 * <pre>
 *   field.depth=200
 *   field.width=320
 *   plant.order=Flower            # ordered weighted plant rules
 *   plant.Flower.probability=0.07
 *   plant.default=Grass           # fallback when no plant rule wins
 *   animal.order=Bird,Mouse,Duck,Wolf,Bear   # cascade priority order
 *   animal.Bird.probability=0.07
 *   ...
 * </pre>
 */
public final class SimulationConfig {

	/** A species name paired with the probability it is chosen for a cell. */
	public record SpeciesWeight(String name, double probability) {
	}

	private static final int DEFAULT_DEPTH = 200;
	private static final int DEFAULT_WIDTH = 320;
	private static final String DEFAULT_PLANT = "Grass";
	private static final List<SpeciesWeight> DEFAULT_PLANT_RULES =
			List.of(new SpeciesWeight("Flower", 0.07));
	private static final List<SpeciesWeight> DEFAULT_ANIMAL_RULES = List.of(
			new SpeciesWeight("Bird", 0.07),
			new SpeciesWeight("Mouse", 0.07),
			new SpeciesWeight("Duck", 0.07),
			new SpeciesWeight("Wolf", 0.03),
			new SpeciesWeight("Bear", 0.03));

	private final int fieldDepth;
	private final int fieldWidth;
	private final List<SpeciesWeight> plantRules;
	private final String defaultPlant;
	private final List<SpeciesWeight> animalRules;


	private SimulationConfig(int fieldDepth, int fieldWidth, List<SpeciesWeight> plantRules,
			String defaultPlant, List<SpeciesWeight> animalRules) {
		this.fieldDepth = fieldDepth;
		this.fieldWidth = fieldWidth;
		this.plantRules = List.copyOf(plantRules);
		this.defaultPlant = defaultPlant;
		this.animalRules = List.copyOf(animalRules);
	}


	/** Load configuration from a properties file. */
	public static SimulationConfig load(Path path) throws IOException {
		Properties props = new Properties();
		try (InputStream in = Files.newInputStream(path)) {
			props.load(in);
		}
		return fromProperties(props);
	}


	/** The built-in configuration, matching the original hardcoded simulation. */
	public static SimulationConfig defaults() {
		return fromProperties(new Properties());
	}


	/** Parse a {@link Properties} table, filling any missing key with its default. */
	public static SimulationConfig fromProperties(Properties props) {
		return new SimulationConfig(
				intValue(props, "field.depth", DEFAULT_DEPTH),
				intValue(props, "field.width", DEFAULT_WIDTH),
				rules(props, "plant", DEFAULT_PLANT_RULES),
				props.getProperty("plant.default", DEFAULT_PLANT).trim(),
				rules(props, "animal", DEFAULT_ANIMAL_RULES));
	}


	public int fieldDepth() {
		return fieldDepth;
	}

	public int fieldWidth() {
		return fieldWidth;
	}

	public List<SpeciesWeight> plantRules() {
		return plantRules;
	}

	public String defaultPlant() {
		return defaultPlant;
	}

	public List<SpeciesWeight> animalRules() {
		return animalRules;
	}


	/**
	 * Read the ordered, weighted rules for a category ("plant" / "animal").
	 * If no {@code <prefix>.order} key is present the defaults are used; otherwise
	 * every listed species must supply a {@code <prefix>.<name>.probability}.
	 */
	private static List<SpeciesWeight> rules(Properties props, String prefix, List<SpeciesWeight> defaults) {
		String order = props.getProperty(prefix + ".order");
		if (order == null || order.isBlank()) {
			return defaults;
		}
		List<SpeciesWeight> rules = new ArrayList<>();
		for (String raw : order.split(",")) {
			String name = raw.trim();
			if (name.isEmpty()) {
				continue;
			}
			String key = prefix + "." + name + ".probability";
			String value = props.getProperty(key);
			if (value == null) {
				throw new IllegalArgumentException("Missing required property: " + key);
			}
			rules.add(new SpeciesWeight(name, doubleValue(key, value)));
		}
		return rules;
	}


	private static int intValue(Properties props, String key, int fallback) {
		String value = props.getProperty(key);
		if (value == null) {
			return fallback;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid integer for " + key + ": '" + value + "'");
		}
	}


	private static double doubleValue(String key, String value) {
		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid number for " + key + ": '" + value + "'");
		}
	}
}
