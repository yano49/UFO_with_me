package sort;
// Sortable interface
interface Sortable {
    void sort(int[] arr);
}

// BubbleSort class implementing Sortable interface
class BubbleSort implements Sortable {
    @Override
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] < arr[j + 1]) { //change index
                    // Swap arr[j] and arr[j+1]
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
}

// SelectionSort class implementing Sortable interface
class SelectionSort implements Sortable {
    @Override
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] > arr[minIndex]) { //change index
                    minIndex = j;
                }
            }
            // Swap the found minimum element with the element at i
            int temp = arr[minIndex];
            arr[minIndex] = arr[i];
            arr[i] = temp;
        }
    }
}

// Main class to test the implementations
public class Main {
    public static void main(String[] args) {
        int[] arrayToSort = {5, 2, 8, 1, 7, 3};

        // Using BubbleSort
        Sortable bubbleSort = new BubbleSort();
        int[] bubbleSortedArray = arrayToSort.clone();
        bubbleSort.sort(bubbleSortedArray);
        System.out.println("Sorted array using BubbleSort: " + java.util.Arrays.toString(bubbleSortedArray));

        // Using SelectionSort
        Sortable selectionSort = new SelectionSort();
        int[] selectionSortedArray = arrayToSort.clone();
        selectionSort.sort(selectionSortedArray);
        System.out.println("Sorted array using SelectionSort: " + java.util.Arrays.toString(selectionSortedArray));

        System.out.println("Fuck you");
    }
}
