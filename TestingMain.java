import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to start the simulation in order to test different probabilities in the Simulation
 * The contents of this class were largely adapted from GeeksForGeeks:
 * https://www.geeksforgeeks.org/print-all-possible-combinations-of-r-elements-in-a-given-array-of-size-n/
 *
 * NOTE: This class has not been fully optimized and will consume several gigabytes of memory.
 * Please make sure to optimize in a way that memory is occasionally cleared before using this class
 *
 * @version 2022.03.2
 */
public class TestingMain {
    private static List<Double[]> allCombs = new ArrayList<>();

    public static void main(String[] args) {

        tester();
        Integer watch = SimulationInfo.HIGHEST_STEPS;
        Simulator tempSim;
        RandomProvider randomProvider = new RandomProvider();
        for(Double[] probs : allCombs){
            tempSim = new Simulator(randomProvider, probs[0], probs[1], probs[2], probs[3], probs[4], probs[5], probs[6]);
            tempSim.runLongSimulation();
            tempSim.simulateOneStep();
            tempSim = null;
        }
        System.out.println("HIGHEST STEPS: " + SimulationInfo.HIGHEST_STEPS + " FOR " + SimulationInfo.HIGHEST_STEP_PROBS.toString());
    }


    public static void tester(){
        Double[] dubs = new Double[15];
        Double[] combs = new Double[7];

        int c = 0;
        for(double i=0.05; i <= 0.8; i += 0.05){
            dubs[c] = i;
            c++;
        }
        int r = 7;
        int n = dubs.length;
        printCombination(dubs, n, r);
    }
    static void combinationUtil(Double arr[], Double data[], int start,
                                int end, int index, int r)
    {
        // Current combination is ready to be printed, print it
        if (index == r)
        {
            Double[] res = new Double[r];
//            System.out.print("[");
            for (int j=0; j<r; j++){
//                System.out.print(data[j]+", ");
                res[j] = data[j];
            }
            if (res != null){
                allCombs.add(res);
            }
//            System.out.print("]");
            System.out.println("");
            return;
        }
        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }

    static void printCombination(Double arr[], int n, int r)
    {
        // A temporary array to store all combination one by one
        Double data[]=new Double[r];

        // Print all combination using temporary array 'data[]'
        combinationUtil(arr, data, 0, n-1, 0, r);
    }
}
