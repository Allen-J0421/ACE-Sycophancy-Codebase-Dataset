import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Initializing the simulation. It first launches the UI, then builds the appropriate
 * objects to run the simulation desired by the user.
 *
 * IMPORTANT: This is the class that should be called to run the simulation.
 *
 * @version 2022.03.01
 */
public class Initializer
{
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The default color for plant objects.
    private static final Color DEFAULT_PLANT_COLOR = Color.decode("0x006400");
    // Default plant name, for now there is just one type of plant so its name is default, this can be changed as the rest of the code is extendable.
    private static final String DEFAULT_PLANT_NAME = "plant";
    // The default value of if animals' ages must be randomized when thy are created.
    private static final boolean RANDOM_ANIMAL_AGE = true;
    // The names of available climate change scenarios.
    private static final List<String> CLIMATE_CHANGE_SCENARIO_NAMES = List.of("none", "low", "medium", "high");
    // The list of colors available for animal objects.
    private static final List<Color> ANIMAL_COLORS = List.of(
        Color.decode("0xFF1493"),
        Color.decode("0xFFA500"),
        Color.decode("0x007CFF"),
        Color.decode("0x44FF99"),
        Color.decode("0x7F0000"),
        Color.decode("0x00FFFF"),
        Color.decode("0xBECF33"),
        Color.decode("0x483D8B"),
        Color.decode("0x7F007F"),
        Color.decode("0xA020F0"),
        Color.decode("0x7E70CA"),
        Color.decode("0xFF9988"),
        Color.decode("0xFFFF00"),
        Color.decode("0x772D26"),
        Color.decode("0xBD7791"),
        Color.decode("0x808080"),
        Color.decode("0xD5A9F5"),
        Color.decode("0xFFB6C1"),
        Color.decode("0xFFE378"),
        Color.decode("0x00008B"),
        Color.decode("0x808000"),
        Color.decode("0x8FBC8F"),
        Color.decode("0xFF0000"),
        Color.decode("0x008B8B"),
        Color.decode("0xADD8E6")
    );
    // False of the simulation starts during the day, true if it starts during the night.
    private static final boolean DEFAULT_START_TIME = false;
    // To read habitat related data.
    private final HabitatCSVReader habitatReader;
    // To read animal related data.
    private final AnimalCSVReader animalReader;
    // To read plant related data.
    private final PlantCSVReader plantReader;
    // A Random object to handle random behaviours throughout the class.
    private static final Random rand = Randomizer.getRandom();
    // A graphical view of the simulation.
    private SimulatorView view;
    // The plant concentration in the habitat created by the user.
    private double habitatPlantConcentration;
    // The GUIHandler handling the GUI.
    private GUIHandler handler;
    // Tool to alert user about any potential error.
    private final ErrorThrower errorThrower;


    /**
     * Builds an Initializer object and initializes its field.
     */
    public Initializer()
    {
        habitatReader = new HabitatCSVReader();
        animalReader = new AnimalCSVReader();
        plantReader = new PlantCSVReader();
        errorThrower = new ErrorThrower();

        openGUI();
    }

    /**
     * Opens the GUI with the list of available animals and habitats extracted
     * from the files as well as the list of available climate change scenarios.
     */
    private void openGUI()
    {
        List<String> animalChoices = animalReader.getChoicesList();
        List<String> habitatChoices = habitatReader.getChoicesList();
        handler = new GUIHandler(this, animalChoices, habitatChoices, CLIMATE_CHANGE_SCENARIO_NAMES);
    }

    /**
     * Create a simulator with the animals, habitat, and climate change
     * scenario asked by the user as well as all other necessary objects
     * for the simulation to run. The created Simulator is returned.
     *
     * @param chosenHabitat (String) The name of the habitat chosen by the user.
     * @param animalsToCreate (Map<String, Integer>) Keys are the animals' names and values are the number of each animal we need to create.
     * @param scenarioName (String) The name of the climate change scenario to implement in the simulation.
     * @return (Simulator) The created simulator.
     */
    public Simulator initializeSimulation(String chosenHabitat, Map<String, Integer> animalsToCreate, String scenarioName)
    {
        List<Species> speciesToEvolveInSimulation = new ArrayList<>();
        SimulationStep simulatorStepCounter = new SimulationStep();
        Field field = new Field(DEFAULT_DEPTH, DEFAULT_WIDTH);
        ClimateScenarios chosenClimateChangeScenario = createChosenClimateChangeScenario(scenarioName);
        Habitat simulationHabitat = createHabitat(chosenHabitat, simulatorStepCounter, chosenClimateChangeScenario);
        if (getNumberOfPlants() + getNumberOfAnimals(animalsToCreate) > calculateFieldArea()) {
            errorThrower.throwMessage("Too many animals were added for this habitat, please reduce the number of animals and try again");
            return null;
        }
        view = new SimulatorView(DEFAULT_DEPTH, DEFAULT_WIDTH, handler);
        populateWithAnimals(animalsToCreate, field, speciesToEvolveInSimulation);
        populateWithPlants(field, speciesToEvolveInSimulation);
        Time timeObject = new Time(simulatorStepCounter, DEFAULT_START_TIME);
        return new Simulator(simulationHabitat, timeObject ,speciesToEvolveInSimulation, field, simulatorStepCounter, view);
    }

    /**
     * Read data for the chosen habitat and create a habitat object appropriately.
     *
     * @param habitatName (String) The name of the chosen habitat.
     * @param simulatorStepCounter (SimulationStep) The created SimulationStep object for this simulation to be handed to the Habitat object.
     * @param climateChangeScenario (ClimateScenarios) The created ClimateScenarios enum to be handed to the Habitat object.
     * @return (Habitat) the created Habitat object.
     */
    private Habitat createHabitat (String habitatName, SimulationStep simulatorStepCounter, ClimateScenarios climateChangeScenario)
    {
        if (habitatName != null) {
            habitatReader.extractDataFor(habitatName);
            Habitat chosenHabitat = new Habitat(simulatorStepCounter, climateChangeScenario ,habitatReader.getSpringTemperatures(), habitatReader.getSummerTemperatures(), habitatReader.getAutumnTemperatures(), habitatReader.getWinterTemperatures());
            habitatPlantConcentration = habitatReader.getPlantConcentration();
            return chosenHabitat;
        }
        else {
            errorThrower.throwMessage("Habitat name was not specified successfully.");
            return null;
        }
    }

    /**
     * Populate the simulation with the chosen animals.
     * First reading the data relating to each animal,
     * then creating and adding to the list of species the right number of each animal.
     *
     * @param animalsToCreate (Map<String, Integer>) The names and number of chosen animals.
     * @param field (Field) The field in which the animals will evolve.
     */
    private void populateWithAnimals(Map<String, Integer> animalsToCreate, Field field, List<Species> speciesInSimulation)
    {
        int colorIndex = 0;

        for (Map.Entry<String, Integer> animalEntry : animalsToCreate.entrySet()) {
            String animalName = animalEntry.getKey();
            int animalsToSpawn = animalEntry.getValue();
            if (animalsToSpawn != 0)
            {
                animalReader.extractDataFor(animalName);
                String name = animalReader.getName();
                addAnimalsOfType(field, animalsToSpawn, speciesInSimulation);

                view.setColor(name, ANIMAL_COLORS.get(colorIndex));
                colorIndex++;
            }
        }
    }

    /**
     * Create the chosen climate change scenario. Scenarios are pre-defined in the enum ClimateScenarios
     * as their functions can be changed to better approximate the real scenarios projected by the GIEC.
     *
     * @param scenarioName (String) The name of the scenario chosen by the user.
     * @return (ClimateScenarios) The created ClimateScenarios enum.
     */
    private ClimateScenarios createChosenClimateChangeScenario(String scenarioName)
    {
        ClimateScenarios chosenScenario;
        switch (scenarioName) {
            case "high":
                chosenScenario = ClimateScenarios.SCENARIO4;
                break;
            case "medium":
                chosenScenario = ClimateScenarios.SCENARIO3;
                break;
            case "low":
                chosenScenario = ClimateScenarios.SCENARIO2;
                break;
            default:
                chosenScenario = ClimateScenarios.SCENARIO1;
        }
        chosenScenario.resetClimateChange();
        return chosenScenario;
    }

    /**
     * Find an available location for an object to be created in the simulation field.
     * The simulation is chosen at random and is changed if the randomly selected cell
     * already contains an object.
     *
     * @param field (Field) The simulation's field.
     * @return (Location) the available location found.
     */
    private Location findAvailableLocation(Field field)
    {
        int randomWidth = rand.nextInt(field.getWidth());
        int randomDepth = rand.nextInt(field.getDepth());
        while (field.getObjectAt(randomDepth,randomWidth) != null) {
            randomWidth = rand.nextInt(field.getWidth());
            randomDepth = rand.nextInt(field.getDepth());
        }
        return new Location(randomDepth, randomWidth);
    }

    /**
     * Calculate the area of the field to know the number of plants that should be created.
     *
     * @return (int) The field's area.
     */
    private int calculateFieldArea()
    {
        return DEFAULT_WIDTH * DEFAULT_DEPTH;
    }

    /**
     * Populate the simulation with the appropriate number of plants.
     *
     * @param field (Field) The simulation's field.
     */
    private void populateWithPlants(Field field, List<Species> speciesInSimulation)
    {
        Location freeLocationToPlacePlant;
        plantReader.extractDataFor(DEFAULT_PLANT_NAME);
        String name = plantReader.getName();
        int maximumTemperature = plantReader.getMaximumTemperature();
        int minimumTemperature = plantReader.getMinimumTemperature();
        int nutritionalValue = plantReader.getNutritionalValue();
        double reproductionProbability = plantReader.getReproductionProbability();
        int maxHealth = plantReader.getMaxHealth();
        for (int i = 0; i< getNumberOfPlants(); i++) {
            freeLocationToPlacePlant = findAvailableLocation(field);
            Plant createdPlant = new Plant(field, freeLocationToPlacePlant, name, maximumTemperature, minimumTemperature, nutritionalValue, reproductionProbability, maxHealth);
            speciesInSimulation.add(createdPlant);
        }
        view.setColor(name, DEFAULT_PLANT_COLOR);
    }

    private void addAnimalsOfType(Field field, int animalCount, List<Species> speciesInSimulation)
    {
        for (int i = 0; i < animalCount; i++) {
            Location location = findAvailableLocation(field);
            speciesInSimulation.add(createAnimal(field, location));
        }
    }

    private Animal createAnimal(Field field, Location location)
    {
        String name = animalReader.getName();
        int maximumTemperature = animalReader.getMaximumTemperature();
        int minimumTemperature = animalReader.getMinimumTemperature();
        int maxAge = animalReader.getMaximumAge();
        int breedingAge = animalReader.getBreedingAge();
        double breedingProbability = animalReader.getBreedingProbability();
        int maxLitterSize = animalReader.getMaxLitterSize();
        int nutritionalValue = animalReader.getNutritionalValue();
        boolean hibernates = animalReader.canHibernate();
        boolean isNocturnal = animalReader.isNocturnal();

        if (animalReader.isPredator()) {
            return new Predator(
                animalReader.getStrength(),
                field,
                location,
                name,
                maximumTemperature,
                minimumTemperature,
                nutritionalValue,
                breedingProbability,
                maxAge,
                breedingAge,
                maxLitterSize,
                RANDOM_ANIMAL_AGE,
                hibernates,
                isNocturnal
            );
        }

        return new Animal(
            field,
            location,
            name,
            maximumTemperature,
            minimumTemperature,
            nutritionalValue,
            breedingProbability,
            maxAge,
            breedingAge,
            maxLitterSize,
            RANDOM_ANIMAL_AGE,
            hibernates,
            isNocturnal
        );
    }

    /**
     * Calculate the total number of plants to be created.
     *
     * @return (int) the number of plants.
     */
    private int getNumberOfPlants()
    {
        int fieldArea = calculateFieldArea();
        return (int)(fieldArea * habitatPlantConcentration);
    }

    /**
     * Calculate the total number of animals to be created.
     *
     * @return (int) the number of animals.
     */
    private int getNumberOfAnimals(Map<String, Integer> animalsToCreate)
    {
        int totalNumber = 0;
        for (String animalName : animalsToCreate.keySet()) {
            totalNumber += animalsToCreate.get(animalName);
        }
        return totalNumber;
    }

}
