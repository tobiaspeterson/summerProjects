import java.util.ArrayList;
import java.util.Arrays;

public class BinaryArraySorting {

    public static ArrayList<Integer> performSorting(int[] unSortedArray) {

        int arraySize = unSortedArray.length;
        int newValue;
        int minIndex;
        int maxIndex;

        ArrayList<Integer> sortedList = new ArrayList<Integer>();
        sortedList.add(unSortedArray[0]);

        for (int i = 1; i < arraySize; i++) {
            newValue = unSortedArray[i];
            minIndex = 0;
            maxIndex = sortedList.size() - 1;
            int addToIndex = findIndexNewVal(sortedList, maxIndex, minIndex, newValue);
            sortedList.add(addToIndex, newValue);
            //Let row below show if you wish to see each element get sorted one by one
            //System.out.println(Arrays.toString(sortedList.toArray()));
        }
        return sortedList;
    }

    public static int findIndexNewVal(ArrayList<Integer> sorted, int max, int min, int newVal){
        if (max > min){
            int mid = min + (max - min)/2;

            if (sorted.get(mid) == newVal){
                return mid;
            }
            if (sorted.get(mid) > newVal){
                return findIndexNewVal(sorted, mid-1, min, newVal);
            }
            else {
                return findIndexNewVal(sorted, max, mid+1, newVal);
            }
        }

        else {
            int mid = min;

            if (sorted.get(mid) >= newVal){
                return mid;
            }
            else {
                return mid+1;
            }
        }
    }
}
