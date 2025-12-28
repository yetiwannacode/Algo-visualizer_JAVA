package visualizer;
import java.util.Arrays;

public class Step {
	public final int[] snapshot;      // array state at this step
    public final int highlightA;      // index to highlight
    public final int highlightB;      // second index to highlight (or -1)
    public final String message;      // explanation of this step
    public final int comparisons;
    public final int swaps;
	public Step(int[] arr, int a, int b, String msg, int comparisons, int swaps) {
		super();
		this.snapshot = Arrays.copyOf(arr, arr.length);
		this.highlightA = a;
		this.highlightB = b;
		this.message = msg;
		this.comparisons = comparisons;
		this.swaps = swaps;
	}
}
