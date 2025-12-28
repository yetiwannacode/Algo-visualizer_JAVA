package visualizer;

public enum Algorithm {
	LINEAR_SEARCH("Linear Search", "Time: O(n)"),
	BINARY_SEARCH("Binary Search", "Time: O(log n) — requires sorted array"),
    BUBBLE_SORT("Bubble Sort", "Time: O(n^2), Best: O(n) with early-exit"),
    SELECTION_SORT("Selection Sort", "Time: O(n^2)");
	
	public final String displayName;
	public final String complexity;
	Algorithm(String displayName, String complexity){
		this.displayName= displayName;
		this.complexity= complexity;
	}
	
	@Override
	public String toString() {
		return displayName;
	}
}
