import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        //edit length and integers of the array as desired
        int[] unSortedArray = {2, 3, 1, 100, 542, 10000, 50, 11, 2500, 7000, 1289, 127, 128, 190, 1900, 9};

        System.out.println("The unsorted array is: " + Arrays.toString(unSortedArray));

        BinaryArraySorting binarySorter = new BinaryArraySorting();
        ArrayList<Integer> sortedArray = binarySorter.performSorting(unSortedArray);

        System.out.println("This is your sorted array: " + sortedArray);
    }



}
