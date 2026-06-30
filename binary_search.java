class BinarySearch {
    public static void main(String args[]) {
        Integer arr[] = { 2, 3, 4, 10, 40 };
        Integer x = 10;
        int result = SearchUtils.binarySearch(arr, x);
        if (result == -1)
            System.out.println("Element is not present in array");
        else
            System.out.println("Element is present at index " + result);
    }
}
