package visualizer;
import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.List;

public class Main extends JFrame{
	// UI state
    private int[] currentArray = new int[20];
    private List<Step> steps = List.of();
    private int stepIndex = 0;

    // Controls
    private final JComboBox<Algorithm> algoBox = new JComboBox<>(Algorithm.values());
    private final JTextField targetField = new JTextField("25", 6);

    private final JCheckBox sortedForBinary = new JCheckBox("Generate Sorted (for Binary Search)", true);

    private final JSlider sizeSlider = new JSlider(5, 60, 20);
    private final JSlider speedSlider = new JSlider(1, 200, 40);

    private final JButton generateBtn = new JButton("Generate");
    private final JButton loadArrayBtn = new JButton("Load Array");
    private final JButton startBtn = new JButton("Start");
    private final JButton stepBtn = new JButton("Step");
    private final JButton resetBtn = new JButton("Reset");
    private final JButton explainBtn = new JButton("Explain Complexity");

    // Custom array input
    private final JTextField customArrayField = new JTextField(
            "10, 4, 7, 2, 9, 1, 8, 3, 6, 5", 30
    );

    // Output
    private final JLabel complexityLabel = new JLabel(" ");
    private final JLabel complexityMiniLabel = new JLabel(" "); // Best/Avg/Worst + Space
    private final JLabel countersLabel = new JLabel("Comparisons: 0 | Swaps: 0");
    private final JTextArea narrationArea = new JTextArea(9, 40);

    // Visual panel
    private final ArrayPanel arrayPanel = new ArrayPanel();

    // Timer for animation
    private Timer timer;

    public Main() {
        super("Algorithm Visualizer (Java) — Step-by-step Tracing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Top: controls
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridy = 0;

        c.gridx = 0; top.add(new JLabel("Algorithm:"), c);
        c.gridx = 1; top.add(algoBox, c);

        c.gridx = 2; top.add(new JLabel("Target (search):"), c);
        c.gridx = 3; top.add(targetField, c);

        c.gridx = 4; top.add(sortedForBinary, c);

        c.gridy = 1;
        c.gridx = 0; top.add(new JLabel("Array Size:"), c);
        c.gridx = 1; sizeSlider.setMajorTickSpacing(10); sizeSlider.setPaintTicks(true); top.add(sizeSlider, c);

        c.gridx = 2; top.add(new JLabel("Speed:"), c);
        c.gridx = 3; speedSlider.setMajorTickSpacing(50); speedSlider.setPaintTicks(true); top.add(speedSlider, c);

        c.gridx = 4;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(generateBtn);
        buttons.add(loadArrayBtn);
        buttons.add(startBtn);
        buttons.add(stepBtn);
        buttons.add(resetBtn);
        top.add(buttons, c);

        // --- Custom array row
        c.gridy = 2;
        c.gridx = 0; top.add(new JLabel("Custom Array:"), c);
        c.gridx = 1; c.gridwidth = 3;
        top.add(customArrayField, c);
        c.gridwidth = 1;
        c.gridx = 4;
        top.add(explainBtn, c);

        add(top, BorderLayout.NORTH);

        // --- Center: visual
        add(arrayPanel, BorderLayout.CENTER);

        // --- Bottom: complexity + counters + narration
        narrationArea.setLineWrap(true);
        narrationArea.setWrapStyleWord(true);
        narrationArea.setEditable(false);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));

        JPanel info = new JPanel(new GridLayout(3, 1));
        info.add(complexityLabel);
        info.add(complexityMiniLabel);
        info.add(countersLabel);

        bottom.add(info, BorderLayout.NORTH);
        bottom.add(new JScrollPane(narrationArea), BorderLayout.CENTER);

        add(bottom, BorderLayout.SOUTH);

        // Actions
        generateBtn.addActionListener(e -> generateArray());
        loadArrayBtn.addActionListener(e -> loadCustomArray());
        resetBtn.addActionListener(e -> resetSteps());
        stepBtn.addActionListener(e -> doOneStep());
        startBtn.addActionListener(e -> toggleStartStop());
        explainBtn.addActionListener(e -> showComplexityExplainer());
        algoBox.addActionListener(e -> {
            updateComplexityText();
            // helpful UX: if binary search selected, remind about sorted requirement
            Algorithm a = (Algorithm) algoBox.getSelectedItem();
            if (a == Algorithm.BINARY_SEARCH) {
                narrationArea.append("\n(Binary Search reminder) Array must be sorted.\n");
                narrationArea.setCaretPosition(narrationArea.getDocument().getLength());
            }
        });

        // Init
        generateArray();
        updateComplexityText();

        setSize(1100, 760);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateComplexityText() {
        Algorithm algo = (Algorithm) algoBox.getSelectedItem();
        if (algo == null) return;
        complexityLabel.setText(" " + algo.displayName + " — " + algo.complexity);
        complexityMiniLabel.setText(" " + getMiniComplexity(algo));
    }

    private String getMiniComplexity(Algorithm algo) {
        switch (algo) {
            case LINEAR_SEARCH:
                return "Best: O(1) | Avg: O(n) | Worst: O(n) | Space: O(1)";
            case BINARY_SEARCH:
                return "Best: O(1) | Avg: O(log n) | Worst: O(log n) | Space: O(1)";
            case BUBBLE_SORT:
                return "Best: O(n) (early exit) | Avg/Worst: O(n^2) | Space: O(1)";
            case SELECTION_SORT:
                return "Best/Avg/Worst: O(n^2) | Space: O(1)";
            default:
                return "";
        }
    }

    private void generateArray() {
        int n = sizeSlider.getValue();
        currentArray = new int[n];

        Algorithm algo = (Algorithm) algoBox.getSelectedItem();
        boolean makeSorted = sortedForBinary.isSelected() && algo == Algorithm.BINARY_SEARCH;

        Random r = new Random();
        for (int i = 0; i < n; i++) currentArray[i] = 5 + r.nextInt(95);

        if (makeSorted) {
            java.util.Arrays.sort(currentArray);
        }

        resetSteps();
        narrationArea.setText("Generated a " + (makeSorted ? "sorted" : "random") + " array of size " + n + ".\n");
        arrayPanel.setState(currentArray, -1, -1);
    }

    // Custom array loading
    private void loadCustomArray() {
        String text = customArrayField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Custom array is empty. Enter numbers like: 5, 2, 9, 1");
            return;
        }

        try {
            int[] arr = parseArray(text);
            if (arr.length == 0) {
                JOptionPane.showMessageDialog(this, "No numbers found. Try: 5, 2, 9, 1");
                return;
            }

            currentArray = arr;
            sizeSlider.setValue(Math.min(Math.max(arr.length, 5), 60)); // keep slider consistent-ish
            resetSteps();

            Algorithm algo = (Algorithm) algoBox.getSelectedItem();
            if (algo == Algorithm.BINARY_SEARCH && !Engine.isSortedNonDecreasing(currentArray)) {
                narrationArea.setText("Loaded custom array (NOT sorted).\n");
                narrationArea.append("Binary Search requires sorted array. Either sort it yourself or change algorithm.\n");
            } else {
                narrationArea.setText("Loaded custom array of size " + currentArray.length + ".\n");
            }

            arrayPanel.setState(currentArray, -1, -1);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid custom array.\n\nExamples:\n  5, 2, 9, 1\n  5 2 9 1\n\nError: " + ex.getMessage());
        }
    }

    private int[] parseArray(String text) {
        String normalized = text.replaceAll("[,]+", " ");
        String[] parts = normalized.trim().split("\\s+");

        java.util.ArrayList<Integer> vals = new java.util.ArrayList<>();
        for (String p : parts) {
            if (p.isBlank()) continue;
            int v = Integer.parseInt(p.trim());
            vals.add(v);
        }

        int[] arr = new int[vals.size()];
        for (int i = 0; i < vals.size(); i++) arr[i] = vals.get(i);
        return arr;
    }

    private void resetSteps() {
        stopTimerIfRunning();
        steps = List.of();
        stepIndex = 0;
        countersLabel.setText("Comparisons: 0 | Swaps: 0");
        arrayPanel.setState(currentArray, -1, -1);
    }

    private void toggleStartStop() {
        if (timer != null && timer.isRunning()) {
            stopTimerIfRunning();
            startBtn.setText("Start");
            return;
        }

        if (steps.isEmpty()) {
            if (!prepareSteps()) return;
        }

        int delayMs = Math.max(5, 1000 - speedSlider.getValue() * 5);
        timer = new Timer(delayMs, e -> {
            if (stepIndex >= steps.size()) {
                stopTimerIfRunning();
                startBtn.setText("Start");
            } else {
                applyStep(steps.get(stepIndex));
                stepIndex++;
            }
        });
        timer.start();
        startBtn.setText("Stop");
    }

    private void stopTimerIfRunning() {
        if (timer != null) timer.stop();
        timer = null;
    }

    private void doOneStep() {
        stopTimerIfRunning();
        startBtn.setText("Start");

        if (steps.isEmpty()) {
            if (!prepareSteps()) return;
        }

        if (stepIndex < steps.size()) {
            applyStep(steps.get(stepIndex));
            stepIndex++;
        } else {
            narrationArea.append("\nNo more steps.\n");
        }
    }

    private boolean prepareSteps() {
        Algorithm algo = (Algorithm) algoBox.getSelectedItem();
        if (algo == null) return false;
        Integer target = null;
        if (algo == Algorithm.LINEAR_SEARCH || algo == Algorithm.BINARY_SEARCH) {
            try {
                target = Integer.parseInt(targetField.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid integer target for search.");
                return false;
            }
        }

        if (algo == Algorithm.BINARY_SEARCH && !Engine.isSortedNonDecreasing(currentArray)) {
            JOptionPane.showMessageDialog(this,
                    "Binary Search requires a sorted array.\n\n" +
                    "Options:\n" +
                    "1) Enter a sorted custom array, OR\n" +
                    "2) Click Generate with 'Generate Sorted' enabled, OR\n" +
                    "3) Choose a different algorithm.");
            return false;
        }

        steps = Engine.generateSteps(algo, currentArray, target);
        stepIndex = 0;

        narrationArea.append("\n--- " + algo.displayName + " started ---\n");
        narrationArea.append("Time and Space Complexity\n");
        narrationArea.append(getMiniComplexity(algo) + "\n\n");
        return true;
    }

    private void applyStep(Step s) {
        arrayPanel.setState(s.snapshot, s.highlightA, s.highlightB);
        countersLabel.setText("Comparisons: " + s.comparisons + " | Swaps: " + s.swaps);
        narrationArea.append(s.message + "\n");
        narrationArea.setCaretPosition(narrationArea.getDocument().getLength());
    }

    // ✅ NEW: Complexity explainer dialog
    private void showComplexityExplainer() {
        Algorithm algo = (Algorithm) algoBox.getSelectedItem();
        if (algo == null) return;
        String msg = buildComplexityExplanation(algo);
        JTextArea area = new JTextArea(msg, 18, 60);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                algo.displayName + " — Complexity Explainer",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String buildComplexityExplanation(Algorithm algo) {
        switch (algo) {
            case LINEAR_SEARCH:
                return """
Linear Search — Why the complexity is O(n)

Idea:
- You check elements one-by-one from the start until you find the target or reach the end.

Best case: O(1)
- Target is at index 0 (first comparison succeeds).

Average/Worst case: O(n)
- On average you scan about n/2 elements, and worst case you scan all n elements.

Space: O(1)
- Only a few variables are used (index, comparisons counter, etc.).

What dominates runtime?
- Number of comparisons with array elements.
""";

            case BINARY_SEARCH:
                return """
Binary Search — Why the complexity is O(log n)

Precondition:
- The array must be sorted (non-decreasing). Otherwise halving logic breaks.

Idea:
- Compare target with the middle element.
- Discard half the remaining search space each step.

Worst/Average: O(log n)
- Each step cuts the search space roughly in half: n → n/2 → n/4 → ... → 1
- That takes about log2(n) steps.

Best case: O(1)
- Target equals the first mid element.

Space: O(1) (iterative)
- We store lo, hi, mid (no recursion stack).

What dominates runtime?
- The number of mid comparisons (one per loop).
""";

            case BUBBLE_SORT:
                return """
Bubble Sort — Why it's O(n^2) and sometimes O(n)

Idea:
- Repeatedly compare adjacent pairs (j and j+1) and swap if out of order.
- After each pass, the largest remaining value “bubbles” to the end.

Worst case: O(n^2)
- Nested loops: about (n-1) + (n-2) + ... + 1 comparisons ≈ n(n-1)/2

Average case: O(n^2)
- Still does ~n^2 comparisons; swaps depend on how shuffled the array is.

Best case: O(n) (ONLY if early-exit is used)
- If a pass makes no swaps, array is already sorted → stop early.
- Then only one pass of ~n comparisons occurs.

Space: O(1)
- Sorts in-place.

What dominates runtime?
- Comparisons of adjacent elements, across multiple passes.
""";

            case SELECTION_SORT:
                return """
Selection Sort — Why it's always O(n^2)

Idea:
- For each position i, find the minimum element in the unsorted suffix (i..n-1)
- Swap it into position i.

Best/Average/Worst: O(n^2)
- You always scan the remaining elements to find the minimum.
- Comparisons: (n-1) + (n-2) + ... + 1 ≈ n(n-1)/2
- This happens regardless of initial order.

Swaps: O(n)
- At most one swap per outer loop iteration.

Space: O(1)
- Sorts in-place.

What dominates runtime?
- The repeated scans to find the min each time.
""";

            default:
                return "No explanation available.";
        }
    }

    // ----- Visual Panel -----
    private static class ArrayPanel extends JPanel {
        private int[] arr = new int[0];
        private int a = -1, b = -1;

        public void setState(int[] arr, int a, int b) {
            this.arr = arr;
            this.a = a;
            this.b = b;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (arr == null || arr.length == 0) return;

            int w = getWidth();
            int h = getHeight();

            int barWidth = Math.max(2, w / arr.length);
            int maxAbs = 1;
            for (int v : arr) maxAbs = Math.max(maxAbs, Math.abs(v));

            int baseY = h - 35;

            for (int i = 0; i < arr.length; i++) {
                int barHeight = (int) ((Math.abs(arr[i]) / (double) maxAbs) * (h - 90));
                int x = i * barWidth + 10;
                int y = baseY - barHeight;

                if (i == a || i == b) g.setColor(Color.ORANGE);
                else g.setColor(new Color(80, 140, 220));

                g.fillRect(x, y, barWidth - 2, barHeight);

                g.setColor(Color.DARK_GRAY);
                if (arr.length <= 30) {
                    g.drawString(String.valueOf(arr[i]), x, baseY + 18);
                }
            }

            g.setColor(Color.DARK_GRAY);
            g.drawString("Highlighted bars = current comparison/swap indices", 10, 20);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
