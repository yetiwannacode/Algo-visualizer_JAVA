package visualizer;
import java.util.*;

public class Engine {
	public static List<Step> generateSteps(Algorithm algo, int[] arr, Integer target){
		switch(algo) {
		case LINEAR_SEARCH: 
			return linearSearchSteps(arr, target==null ? 0: target);
		case BINARY_SEARCH:
			return binarySearchSteps(arr, target==null ? 0: target);
		case BUBBLE_SORT:
			return bubbleSortSteps(arr);
		case SELECTION_SORT:
			return selectionSortSteps(arr);
		default:
			return List.of();
		}
	}
	
	private static List<Step> linearSearchSteps(int[] arr, int target){
		List<Step> steps= new ArrayList<>();
		int comps=0, swaps=0;
		if(arr.length==0) {
			steps.add(new Step(arr, -1, -1, "Edge case: empty array → nothing to search.", comps, swaps));
			return steps;
		}
		steps.add(new Step(arr, -1, -1, "Linear Search: scan from left to right.", comps, swaps));
		for(int i=0; i<arr.length; i++) {
			comps++;
			steps.add(new Step(arr, i, -1, "Compare target "+ target+ "with arr["+ i+ "] ="+ arr[i]+ ".", comps, swaps));
			if(arr[i]== target) {
				steps.add(new Step(arr, i, -1, "Found! target " + target + " == arr[" + i + "]. Stop.", comps, swaps));
                return steps;
			}
		}
		steps.add(new Step(arr, -1, -1,
                "Not found: scanned all elements and none matched target " + target + ".", comps, swaps));
        return steps;
	}
	
	private static List<Step> binarySearchSteps(int []arr, int target){
		List<Step> steps = new ArrayList<>();
        int comps = 0, swaps = 0;
        if (arr.length == 0) {
            steps.add(new Step(arr, -1, -1, "Edge case: empty array → nothing to search.", comps, swaps));
            return steps;
        }
        steps.add(new Step(arr, -1, -1, "Binary Search requires a sorted array. We will repeatedly halve the search space.", comps, swaps));
        int low=0, high= arr.length-1;
        while(low<=high) {
        	int mid= low+ (high-low)/2;
        	comps++;
        	steps.add(new Step(arr, mid, -1, "low="+ low+ ", high="+ high+ ", mid="+ mid+ ". Compare target "+ target+ "with arr[mid]= "+ arr[mid]+ ".", comps, swaps));
        	if(arr[mid]== target) {
        		steps.add(new Step(arr, mid, -1, "Found! target == arr[mid]. Stop.", comps, swaps));
                return steps;
        	}
        	else if(arr[mid]< target) {
        		steps.add(new Step(arr, mid, -1, "arr[mid] < target → discard left half. Set low = mid + 1.", comps, swaps));
                low = mid + 1;
        	}
        	else {
        		steps.add(new Step(arr, mid, -1, "arr[mid] > target → discard right half. Set high = mid - 1.", comps, swaps));
        		high= mid-1;
        	}
        }
        steps.add(new Step(arr, -1, -1, "Not found: low crossed high, target does not exist in array.", comps, swaps));
        return steps;
	}
	
	private static List<Step> bubbleSortSteps(int[] arrInput){
		int[] arr = arrInput.clone();
        List<Step> steps = new ArrayList<>();
        int comps = 0, swaps = 0;
        if (arr.length <= 1) {
            steps.add(new Step(arr, -1, -1, "Edge case: array length ≤ 1 → already sorted.", comps, swaps));
            return steps;
        }
        steps.add(new Step(arr, -1, -1, "Bubble Sort: repeatedly swap adjacent out-of-order pairs. Largest bubbles to the end.", comps, swaps));
        for (int pass = 0; pass < arr.length - 1; pass++) {
            boolean swapped = false;
            steps.add(new Step(arr, -1, -1, "Pass " + (pass + 1) + " begins.", comps, swaps));
            for (int j = 0; j < arr.length - 1 - pass; j++) {
                comps++;
                steps.add(new Step(arr, j, j + 1, "Compare arr[" + j + "]=" + arr[j] + " and arr[" + (j + 1) + "]=" + arr[j + 1] + ".", comps, swaps));
                if (arr[j] > arr[j + 1]) {
                    swaps++;
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    swapped = true;
                    steps.add(new Step(arr, j, j + 1, "Swap because arr[" + j + "] > arr[" + (j + 1) + "].", comps, swaps));
                } 
                else {
                    steps.add(new Step(arr, j, j + 1,
                            "No swap: already in correct order.", comps, swaps));
                }
            }
            if (!swapped) {
                steps.add(new Step(arr, -1, -1,
                        "Early exit: no swaps this pass → array is sorted (best-case O(n)).", comps, swaps));
                break;
            }
        }
        steps.add(new Step(arr, -1, -1, "Done: array is sorted.", comps, swaps));
        return steps;
    }
	
	private static List<Step> selectionSortSteps(int[] arrInput) {
        int[] arr = arrInput.clone();
        List<Step> steps = new ArrayList<>();
        int comps = 0, swaps = 0;
        if (arr.length <= 1) {
            steps.add(new Step(arr, -1, -1, "Edge case: array length ≤ 1 → already sorted.", comps, swaps));
            return steps;
        }
        steps.add(new Step(arr, -1, -1, "Selection Sort: select the minimum from unsorted part and place it at the front.", comps, swaps));
        for (int i = 0; i < arr.length - 1; i++) {
            int minIdx = i;
            steps.add(new Step(arr, i, minIdx, "Position i=" + i + ". Assume min is arr[" + minIdx + "]=" + arr[minIdx] + ".", comps, swaps));
            for (int j = i + 1; j < arr.length; j++) {
                comps++;
                steps.add(new Step(arr, minIdx, j, "Compare current min arr[" + minIdx + "]=" + arr[minIdx] + " with candidate arr[" + j + "]=" + arr[j] + ".", comps, swaps));
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                    steps.add(new Step(arr, i, minIdx,
                            "New min found at index " + minIdx + " (value " + arr[minIdx] + ").", comps, swaps));
                }
            }
            if (minIdx != i) {
                swaps++;
                int tmp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = tmp;
                steps.add(new Step(arr, i, minIdx, "Swap min into position i. Now arr[" + i + "] is fixed.", comps, swaps));
            } 
            else {
                steps.add(new Step(arr, i, -1, "No swap needed: position i already has the minimum.", comps, swaps));
            }
        }
        steps.add(new Step(arr, -1, -1, "Done: array is sorted.", comps, swaps));
        return steps;
    }
	
	public static boolean isSortedNonDecreasing(int[] arr) {
        for (int i = 1; i < arr.length; i++) if (arr[i - 1] > arr[i]) return false;
        return true;
    }
}
