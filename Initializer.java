import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private static final ArrayList<String> CLIMATE_CHANGE_SCENARIO_NAMES = new ArrayList<>(Arrays.asList("none", "low", "medium", "high"));
    // False of the simulation starts during the day, true if it starts during the night.
    private static final boolean DEFAULT_START_TIME = false;
    // The list of colors available for animal objects.
    private ArrayList<Color> listOfColorsForAnimals;
    // The index of the next color from the list used for an animal.
    private int idxOfColorToUseNext;
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
    private ErrorThrower errorThrower;


    /**
     * Builds an Initializer object and initializes its field.
     */
    public Initializer()
    {
        habitatReader = new HabitatCSVReader();
        animalReader = new AnimalCSVReader();
        plantReader = new PlantCSVReader();
        listOfColorsForAnimals = new ArrayList<>();
        errorThrower = new ErrorThrower();

        populateAnimalColors();
        idxOfColorToUseNext = 0;

        openGUI();
    }

    /**
     * Opens the GUI with the list of available animals and habitats extracted
     * from the files as well as the list of available climate change scenarios.
     */
    private void openGUI()
    {
        ArrayList<String> animalChoices = animalReader.getChoicesList();
        ArrayList<String> habitatChoices = habitatReader.getChoicesList();
        handler = new GUIHandler(this, animalChoices, habitatChoices, CLIMATE_CHANGE_SCENARIO_NAMES);
    }

    /**
     * Create a simulator with the animals, habitat, and climate change
     * scenario asked by the user as well as all other necessary objects
     * for the simulation to run. The created Simulator is returned.
     *
     * @param chosenHabitat (String) The name of the habitat chosen by the user.
     * @param animalsToCreate (HashMap<String, Integer>) Keys are the animals' names and values are the number of each animal we need to create.
     * @param scenarioName (String) The name of the climate change scenario to implement in the simulation.
     * @return (Simulator) The created simulator.
     */
    public Simulator initializeSimulation(String chosenHabitat, HashMap<String, Integer> animalsToCreate, String scenarioName)
    {
        SimulationStep simulatorStepCounter = new SimulationStep();
        Field field = new Field(DEFAULT_DEPTH, DEFAULT_WIDTH);
        Habitat simulationHabitat = createHabitat(chosenHabitat, simulatorStepCounter, createChosenClimateChangeScenario(scenarioName));
        if (simulationHabitat == null) {
            return null;
        }

        if (!validatePopulationCapacity(animalsToCreate)) {
            return null;
        }

        List<Species> speciesToEvolveInSimulation = new ArrayList<>();
        view = new SimulatorView(DEFAULT_DEPTH, DEFAULT_WIDTH, handler);
        populateWithAnimals(speciesToEvolveInSimulation, animalsToCreate, field);
        populateWithPlants(speciesToEvolveInSimulation, field);
        Time timeObject = new Time(simulatorStepCounter, DEFAULT_START_TIME);
        return new Simulator(simulationHabitat, timeObject, speciesToEvolveInSimulation, field, simulatorStepCounter, view);
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
        if (habitatName == null) {
            errorThrower.throwMessage("Habitat name was not specified successfully.");
            return null;
        }

        habitatReader.extractDataFor(habitatName);
        habitatPlantConcentration = habitatReader.getPlantConcentration();
        return new Habitat(simulatorStepCounter, climateChangeScenario, habitatReader.getSpringTemperatures(), habitatReader.getSummerTemperatures(), habitatReader.getAutumnTemperatures(), habitatReader.getWinterTemperatures());
    }

    /**
     * Populate the simulation with the chosen animals.
     * First reading the data relating to each animal,
     * then creating and adding to the list of species the right number of each animal.
     *
     * @param animalsToCreate (HashMap<String, Integer>) The names and number of chosen animals.
     * @param field (Field) The field in which the animals will evolve.
     */
    private void populateWithAnimals(List<Species> speciesToPopulate, HashMap<String, Integer> animalsToCreate, Field field)
    {
        idxOfColorToUseNext = 0;

        for (Map.Entry<String, Integer> animalEntry : animalsToCreate.entrySet()) {
            animalReader.extractDataFor(animalEntry.getKey());
            int numberToCreate = animalEntry.getValue();
            if (numberToCreate <= 0) {
                continue;
            }

            String speciesName = animalReader.getName();
            addAnimalsForTemplateSpecies(speciesToPopulate, field, numberToCreate);
            view.setColor(speciesName, listOfColorsForAnimals.get(idxOfColorToUseNext));
            idxOfColorToUseNext++;
        }
    }

    /**
     * Create the requested number of animals or predators for the currently selected template species.
     *
     * @param speciesToPopulate the simulation species list.
     * @param field the simulation field.
     * @param numberToCreate the number of individuals to create.
     */
    private void addAnimalsForTemplateSpecies(List<Species> speciesToPopulate, Field field, int numberToCreate)
    {
        for (int i = 0; i < numberToCreate; i++) {
            Species createdSpecies = createAnimalFromTemplate(field);
            speciesToPopulate.add(createdSpecies);
        }
    }

    /**
     * Create one animal or predator instance from the currently loaded animal template.
     *
     * @param field the simulation field.
     * @return the created species instance.
     */
    private Species createAnimalFromTemplate(Field field)
    {
        Location location = findAvailableLocation(field);
        if (animalReader.isPredator()) {
            return createPredator(field, location);
        }
        return createAnimal(field, location);
    }

    /**
     * Create an animal from the currently loaded template.
     *
     * @param field the simulation field.
     * @param location the location where the animal will be placed.
     * @return the created animal.
     */
    private Animal createAnimal(Field field, Location location)
    {
        return new Animal(field, location, animalReader.getName(), animalReader.getMaximumTemperature(), animalReader.getMinimumTemperature(), animalReader.getNutritionalValue(), animalReader.getBreedingProbability(), animalReader.getMaximumAge(), animalReader.getBreedingAge(), animalReader.getMaxLitterSize(), RANDOM_ANIMAL_AGE, animalReader.canHibernate(), animalReader.isNocturnal());
    }

    /**
     * Create a predator from the currently loaded template.
     *
     * @param field the simulation field.
     * @param location the location where the predator will be placed.
     * @return the created predator.
     */
    private Predator createPredator(Field field, Location location)
    {
        return new Predator(animalReader.getStrength(), field, location, animalReader.getName(), animalReader.getMaximumTemperature(), animalReader.getMinimumTemperature(), animalReader.getNutritionalValue(), animalReader.getBreedingProbability(), animalReader.getMaximumAge(), animalReader.getBreedingAge(), animalReader.getMaxLitterSize(), RANDOM_ANIMAL_AGE, animalReader.canHibernate(), animalReader.isNocturnal());
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
        if (scenarioName == null) {
            return ClimateScenarios.SCENARIO1;
        }

        switch (scenarioName) {
            case "high":
                return ClimateScenarios.SCENARIO4;
            case "medium":
                return ClimateScenarios.SCENARIO3;
            case "low":
                return ClimateScenarios.SCENARIO2;
            default:
                return ClimateScenarios.SCENARIO1;
        }
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
        int randomWidth = rand.nextInt(DEFAULT_WIDTH);
        int randomDepth = rand.nextInt(DEFAULT_DEPTH);
        while (field.getObjectAt(randomDepth,randomWidth) != null) {
            randomWidth = rand.nextInt(DEFAULT_WIDTH);
            randomDepth = rand.nextInt(DEFAULT_DEPTH);
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
    private void populateWithPlants(List<Species> speciesToPopulate, Field field)
    {
        plantReader.extractDataFor(DEFAULT_PLANT_NAME);
        String name = plantReader.getName();
        int maximumTemperature = plantReader.getMaximumTemperature();
        int minimumTemperature = plantReader.getMinimumTemperature();
        int nutritionalValue = plantReader.getNutritionalValue();
        double reproductionProbability = plantReader.getReproductionProbability();
        int maxHealth = plantReader.getMaxHealth();
        for (int i = 0; i< getNumberOfPlants(); i++) {
            speciesToPopulate.add(createPlant(field, findAvailableLocation(field), name, maximumTemperature, minimumTemperature, nutritionalValue, reproductionProbability, maxHealth));
        }
        view.setColor(name, DEFAULT_PLANT_COLOR);
    }

    /**
     * Create a plant from the currently loaded template.
     *
     * @param field the simulation field.
     * @param location the location where the plant will be placed.
     * @param name the plant name.
     * @param maximumTemperature the maximum survivable temperature.
     * @param minimumTemperature the minimum survivable temperature.
     * @param nutritionalValue the nutritional value.
     * @param reproductionProbability the reproduction probability.
     * @param maxHealth the maximum plant health.
     * @return the created plant.
     */
    private Plant createPlant(Field field, Location location, String name, int maximumTemperature, int minimumTemperature, int nutritionalValue, double reproductionProbability, int maxHealth)
    {
        return new Plant(field, location, name, maximumTemperature, minimumTemperature, nutritionalValue, reproductionProbability, maxHealth);
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
    private int getNumberOfAnimals(HashMap<String, Integer> animalsToCreate)
    {
        int totalNumber = 0;
        for (Integer count : animalsToCreate.values()) {
            totalNumber += count;
        }
        return totalNumber;
    }

    /**
     * Validate that the requested population fits into the available field area.
     *
     * @param animalsToCreate the requested animal counts.
     */
    private boolean validatePopulationCapacity(HashMap<String, Integer> animalsToCreate)
    {
        if (getNumberOfPlants() + getNumberOfAnimals(animalsToCreate) > calculateFieldArea()) {
            errorThrower.throwMessage("Too many animals were added for this habitat, please reduce the number of animals and try again");
            return false;
        }
        return true;
    }

    /**
     * Populate the list of colors to use for animals with 25 colors. 25 animals can therefore be created
     * , other colors be added to implement more animal choices.
     */
    private void populateAnimalColors()
    {
        listOfColorsForAnimals.add(Color.decode("0xFF1493"));
        listOfColorsForAnimals.add(Color.decode("0xFFA500"));
        listOfColorsForAnimals.add(Color.decode("0x007CFF"));
        listOfColorsForAnimals.add(Color.decode("0x44FF99"));
        listOfColorsForAnimals.add(Color.decode("0x7F0000"));
        listOfColorsForAnimals.add(Color.decode("0x00FFFF"));
        listOfColorsForAnimals.add(Color.decode("0xBECF33"));
        listOfColorsForAnimals.add(Color.decode("0x483D8B"));
        listOfColorsForAnimals.add(Color.decode("0x7F007F"));
        listOfColorsForAnimals.add(Color.decode("0xA020F0"));
        listOfColorsForAnimals.add(Color.decode("0x7E70CA"));
        listOfColorsForAnimals.add(Color.decode("0xFF9988"));
        listOfColorsForAnimals.add(Color.decode("0xFFFF00"));
        listOfColorsForAnimals.add(Color.decode("0x772D26"));
        listOfColorsForAnimals.add(Color.decode("0xBD7791"));
        listOfColorsForAnimals.add(Color.decode("0x808080"));
        listOfColorsForAnimals.add(Color.decode("0xD5A9F5"));
        listOfColorsForAnimals.add(Color.decode("0xFFB6C1"));
        listOfColorsForAnimals.add(Color.decode("0xFFE378"));
        listOfColorsForAnimals.add(Color.decode("0x00008B"));
        listOfColorsForAnimals.add(Color.decode("0x808000"));
        listOfColorsForAnimals.add(Color.decode("0x8FBC8F"));
        listOfColorsForAnimals.add(Color.decode("0xFF0000"));
        listOfColorsForAnimals.add(Color.decode("0x008B8B"));
        listOfColorsForAnimals.add(Color.decode("0xADD8E6"));
    }
}
