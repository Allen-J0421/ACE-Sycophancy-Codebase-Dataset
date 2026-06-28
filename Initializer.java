import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    // The palette supplying colors for animal species.
    private final AnimalColorPalette animalColorPalette;
    // List of species to evolve in the field.
    private List<Species> speciesToEvolveInSimulation;
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
        speciesToEvolveInSimulation = new ArrayList<>();
        habitatReader = new HabitatCSVReader();
        animalReader = new AnimalCSVReader();
        plantReader = new PlantCSVReader();
        animalColorPalette = new AnimalColorPalette();
        errorThrower = new ErrorThrower();

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
        ClimateScenarios chosenClimateChangeScenario = createChosenClimateChangeScenario(scenarioName);
        Habitat simulationHabitat = createHabitat(chosenHabitat, simulatorStepCounter, chosenClimateChangeScenario);
        if (getNumberOfPlants() + getNumberOfAnimals(animalsToCreate) > calculateFieldArea()) {
            errorThrower.throwMessage("Too many animals were added for this habitat, please reduce the number of animals and try again");
            return null;
        }
        view = new SimulatorView(DEFAULT_DEPTH, DEFAULT_WIDTH, handler);
        populateWithAnimals(animalsToCreate, field);
        populateWithPlants(field);
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
     * @param animalsToCreate (HashMap<String, Integer>) The names and number of chosen animals.
     * @param field (Field) The field in which the animals will evolve.
     */
    private void populateWithAnimals(HashMap<String, Integer> animalsToCreate, Field field)
    {
        animalColorPalette.reset();

        for(String animalName : animalsToCreate.keySet()) {
            animalReader.extractDataFor(animalName);
            int numberToCreate = animalsToCreate.get(animalName);
            if (numberToCreate != 0)
            {
                // Create the requested number of this species, then assign it a color.
                for (int i = 0; i < numberToCreate; i++) {
                    Location location = findAvailableLocation(field);
                    speciesToEvolveInSimulation.add(createAnimalFromReaderData(field, location));
                }
                view.setColor(animalReader.getName(), animalColorPalette.nextColor());
            }
        }
    }

    /**
     * Create a single animal of the species currently loaded in the animal reader, placed at the
     * given location. A Predator is created when the species is a predator, otherwise a plain Animal;
     * either way the returned object is an Animal, which keeps the population loop type-agnostic.
     *
     * @param field (Field) The field in which the animal will evolve.
     * @param location (Location) The location at which the animal should be placed.
     * @return (Animal) the created animal (possibly a Predator).
     */
    private Animal createAnimalFromReaderData(Field field, Location location)
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
            int strength = animalReader.getStrength();
            return new Predator(strength, field, location, name, maximumTemperature, minimumTemperature, nutritionalValue, breedingProbability, maxAge, breedingAge, maxLitterSize, RANDOM_ANIMAL_AGE, hibernates, isNocturnal);
        }
        return new Animal(field, location, name, maximumTemperature, minimumTemperature, nutritionalValue, breedingProbability, maxAge, breedingAge, maxLitterSize, RANDOM_ANIMAL_AGE, hibernates, isNocturnal);
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
        if (scenarioName.equals(CLIMATE_CHANGE_SCENARIO_NAMES.get(3))) {
            chosenScenario = ClimateScenarios.SCENARIO4;
        }
        else if (scenarioName.equals(CLIMATE_CHANGE_SCENARIO_NAMES.get(2))) {
            chosenScenario = ClimateScenarios.SCENARIO3;
        }
        else if (scenarioName.equals(CLIMATE_CHANGE_SCENARIO_NAMES.get(1))) {
            chosenScenario = ClimateScenarios.SCENARIO2;
        }
        else{
            chosenScenario = ClimateScenarios.SCENARIO1;
        }
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
    private void populateWithPlants(Field field)
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
            speciesToEvolveInSimulation.add(createdPlant);
        }
        view.setColor(name, DEFAULT_PLANT_COLOR);
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
        for (String animalName : animalsToCreate.keySet()) {
            totalNumber += animalsToCreate.get(animalName);
        }
        return totalNumber;
    }
}