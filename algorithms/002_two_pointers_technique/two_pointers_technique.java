import java.util.Arrays;

class TwoPointers {

    static boolean twoSum(int[] arr, int target){

        int left = 0, right = arr.length - 1;

        while (left < right) {
            int sum = arr[left] + arr[right];

            if (sum == target)
                return true;
            else if (sum < target)
                left++;
            else
                right--;
        }

        return false;
    }

    public static void main(String[] args){
        int[] arr = {-3, -1, 0, 1, 2 };
        int target = -2;

        if (twoSum(arr, target)) {
            System.out.println("true");
        }
        else {
            System.out.println("false");
        }
    }
}
