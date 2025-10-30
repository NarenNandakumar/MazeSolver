package pack;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.awt.Point;
public class MazeSolver {
   // Cell values
   private static final int EMPTY = 0;
   private static final int WALL = 1;
   private static final int START = 2;
   private static final int END = 3;
   private static final int PATH = 4;
   private JFrame frame;
   private GridPanel gridPanel;
   private JTextField rowsField, colsField;
   private JButton resizeButton, solveButton, clearButton, clearWallsButton, stepButton;
   private JToggleButton startBtn, endBtn, wallBtn, eraseBtn;
   private ButtonGroup toolsGroup;
   private JLabel statusLabel, timerLabel;
   private JComboBox<String> heuristicBox;
   private JCheckBox stepModeCheck;
   // default sizes
   private int rows = 20;
   private int cols = 30;
   // current stepper (null when not in step mode or not initialized)
   private AI.Stepper stepper = null;
   public MazeSolver() {
       SwingUtilities.invokeLater(this::createAndShowGUI);
   }
   private void createAndShowGUI() {
       frame = new JFrame("Buildable Maze Solver (Step Mode)");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setLayout(new BorderLayout());
       // top control panel
       JPanel control = new JPanel(new BorderLayout());
       JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
       leftPanel.add(new JLabel("Rows:"));
       rowsField = new JTextField(String.valueOf(rows), 3);
       leftPanel.add(rowsField);
       leftPanel.add(new JLabel("Cols:"));
       colsField = new JTextField(String.valueOf(cols), 3);
       leftPanel.add(colsField);
       resizeButton = new JButton("Resize Grid");
       leftPanel.add(resizeButton);
       startBtn = new JToggleButton("Start");
       endBtn = new JToggleButton("End");
       wallBtn = new JToggleButton("Wall");
       eraseBtn = new JToggleButton("Erase");
       toolsGroup = new ButtonGroup();
       toolsGroup.add(startBtn);
       toolsGroup.add(endBtn);
       toolsGroup.add(wallBtn);
       toolsGroup.add(eraseBtn);
       wallBtn.setSelected(true);
       // Ensure toggle buttons wide enough
       Dimension toolSize = new Dimension(70, 28);
       startBtn.setPreferredSize(toolSize);
       endBtn.setPreferredSize(toolSize);
       wallBtn.setPreferredSize(toolSize);
       eraseBtn.setPreferredSize(toolSize);
       leftPanel.add(startBtn);
       leftPanel.add(endBtn);
       leftPanel.add(wallBtn);
       leftPanel.add(eraseBtn);
       solveButton = new JButton("Solve");
       clearButton = new JButton("Clear All");
       clearWallsButton = new JButton("Clear Walls");
       leftPanel.add(solveButton);
       leftPanel.add(clearWallsButton);
       leftPanel.add(clearButton);
       // heuristic chooser
       heuristicBox = new JComboBox<>(new String[] {"Manhattan", "Euclidean", "Chebyshev"});
       heuristicBox.setSelectedIndex(0);
       leftPanel.add(new JLabel("Heuristic:"));
       leftPanel.add(heuristicBox);
       // step mode toggle + step button
       stepModeCheck = new JCheckBox("Step Mode");
       stepButton = new JButton("Step ▶");
       stepButton.setEnabled(false);
       leftPanel.add(stepModeCheck);
       leftPanel.add(stepButton);
       // timer display (top-right)
       timerLabel = new JLabel("Time: —");
       timerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
       timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
       JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
       rightPanel.add(timerLabel);
       control.add(leftPanel, BorderLayout.CENTER);
       control.add(rightPanel, BorderLayout.EAST);
       frame.add(control, BorderLayout.NORTH);
       gridPanel = new GridPanel(rows, cols);
       frame.add(gridPanel, BorderLayout.CENTER);
       statusLabel = new JLabel("Tool: Wall    Grid auto-fits window size.");
       frame.add(statusLabel, BorderLayout.SOUTH);
       // listeners
       resizeButton.addActionListener(e -> resizeGrid());
       solveButton.addActionListener(e -> onSolve());
       clearButton.addActionListener(e -> {
           gridPanel.resetGrid();
           gridPanel.repaint();
           status("Cleared grid.");
           timerLabel.setText("Time: —");
           stepper = null;
           gridPanel.setStepper(null);
           stepButton.setEnabled(false);
       });
       clearWallsButton.addActionListener(e -> {
           gridPanel.clearWalls();
           gridPanel.repaint();
           status("Cleared walls and path.");
           timerLabel.setText("Time: —");
           stepper = null;
           gridPanel.setStepper(null);
           stepButton.setEnabled(false);
       });
       ItemListener toolListener = e -> {
           if (e.getStateChange() == ItemEvent.SELECTED) {
               status("Tool: " + ((JToggleButton)e.getItem()).getText());
           }
       };
       startBtn.addItemListener(toolListener);
       endBtn.addItemListener(toolListener);
       wallBtn.addItemListener(toolListener);
       eraseBtn.addItemListener(toolListener);
       stepModeCheck.addActionListener(e -> {
           boolean on = stepModeCheck.isSelected();
           stepButton.setEnabled(on && stepper != null);
           if (!on) {
               // disable stepper if turned off
               stepper = null;
               gridPanel.setStepper(null);
               gridPanel.repaint();
           }
       });
       stepButton.addActionListener(e -> doStep());
       // right-arrow keybinding for stepping
       InputMap im = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
       ActionMap am = frame.getRootPane().getActionMap();
       im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "stepAction");
       am.put("stepAction", new AbstractAction() {
           @Override
           public void actionPerformed(ActionEvent e) {
               if (stepModeCheck.isSelected()) doStep();
           }
       });
       frame.setSize(1000, 700);
       frame.setLocationRelativeTo(null);
       frame.setVisible(true);
   }
   private void status(String txt) {
       statusLabel.setText(txt);
   }
   private void resizeGrid() {
       try {
           int r = Integer.parseInt(rowsField.getText().trim());
           int c = Integer.parseInt(colsField.getText().trim());
           if (r <= 0 || c <= 0 || r > 200 || c > 200) {
               JOptionPane.showMessageDialog(frame, "Rows and columns must be between 1 and 200.", "Invalid size", JOptionPane.WARNING_MESSAGE);
               return;
           }
           rows = r;
           cols = c;
           gridPanel.setGridSize(rows, cols);
           gridPanel.revalidate();
           gridPanel.repaint();
           status("Resized grid to " + rows + " x " + cols);
           timerLabel.setText("Time: —");
           stepper = null;
           gridPanel.setStepper(null);
           stepButton.setEnabled(false);
       } catch (NumberFormatException ex) {
           JOptionPane.showMessageDialog(frame, "Please enter valid integers for rows and columns.", "Input error", JOptionPane.ERROR_MESSAGE);
       }
   }
   private AI.Heuristic heuristicFromBox() {
       String s = (String) heuristicBox.getSelectedItem();
       if ("Euclidean".equals(s)) return AI.Heuristic.EUCLIDEAN;
       if ("Chebyshev".equals(s)) return AI.Heuristic.CHEBYSHEV;
       return AI.Heuristic.MANHATTAN;
   }
   private void onSolve() {
       Point start = gridPanel.getStart();
       Point end = gridPanel.getEnd();
       if (start == null || end == null) {
           JOptionPane.showMessageDialog(frame, "Please place both a Start and an End before solving.", "Missing start/end", JOptionPane.WARNING_MESSAGE);
           return;
       }
       int[][] gridCopy = gridPanel.getGridCopyWithoutPath();
       AI.Heuristic heuristic = heuristicFromBox();
       if (stepModeCheck.isSelected()) {
           // initialize stepper
           stepper = new AI.Stepper(gridCopy, start, end, heuristic);
           gridPanel.setStepper(stepper);
           stepButton.setEnabled(true);
           status("Step mode initialized. Press Right Arrow or Step ▶ to advance.");
           timerLabel.setText("Time: —");
           gridPanel.clearPath();
           gridPanel.repaint();
           return;
       }
       // normal full solve (timed)
       long startTime = System.nanoTime();
       List<Point> path = AI.solve(gridCopy, start, end, heuristic);
       long endTime = System.nanoTime();
       long duration = endTime - startTime;
       if (path == null) {
           JOptionPane.showMessageDialog(frame, "No path found from Start to End.", "No path", JOptionPane.INFORMATION_MESSAGE);
           status("No path found.");
           gridPanel.clearPath();
           gridPanel.repaint();
           timerLabel.setText("Time: —");
           return;
       }
       gridPanel.clearPath();
       for (Point p : path) {
           if (!p.equals(start) && !p.equals(end)) {
               gridPanel.setCell(p.y, p.x, PATH);
           }
       }
       gridPanel.repaint();
       status("Path found! Length: " + path.size());
       timerLabel.setText("Time: " + formatTime(duration));
   }
   private void doStep() {
       if (stepper == null) {
           JOptionPane.showMessageDialog(frame, "Step mode is not initialized. Press Solve while Step Mode is checked to start.", "Step mode not ready", JOptionPane.INFORMATION_MESSAGE);
           return;
       }
       AI.Stepper.Status st = stepper.step(); // advance one node poll/expansion
       gridPanel.repaint();
       if (st == AI.Stepper.Status.FOUND) {
           // display the path
           List<Point> path = stepper.getPath();
           gridPanel.clearPath();
           Point start = gridPanel.getStart();
           Point end = gridPanel.getEnd();
           for (Point p : path) {
               if (!p.equals(start) && !p.equals(end)) gridPanel.setCell(p.y, p.x, PATH);
           }
           gridPanel.repaint();
           status("Path found! Length: " + path.size());
           timerLabel.setText("Steps: " + stepper.getSteps() + "  Visited: " + stepper.getVisitedCount());
           stepper = null;
           gridPanel.setStepper(null);
           stepButton.setEnabled(false);
           return;
       } else if (st == AI.Stepper.Status.FAILED) {
           status("No path exists (exhausted open set).");
           timerLabel.setText("Steps: " + stepper.getSteps() + "  Visited: " + stepper.getVisitedCount());
           stepper = null;
           gridPanel.setStepper(null);
           stepButton.setEnabled(false);
           return;
       } else {
           // still running
           timerLabel.setText("Steps: " + stepper.getSteps() + "  Visited: " + stepper.getVisitedCount() + "  Open: " + stepper.getOpenCount());
           status("Stepping... (Right Arrow / Step ▶)");
           stepButton.setEnabled(true);
       }
   }
   private String formatTime(long nanos) {
       double ms = nanos / 1_000_000.0;
       if (ms < 1000) {
           return String.format("%.2f ms", ms);
       } else if (ms < 60_000) {
           return String.format("%.2f s", ms / 1000);
       } else {
           return String.format("%.2f min", ms / 60_000);
       }
   }
   class GridPanel extends JPanel {
       private int[][] grid;
       private int rows, cols;
       private Point start = null;
       private Point end = null;
       // overlays from stepper
       private AI.Stepper currentStepper = null;
       GridPanel(int rows, int cols) {
           this.rows = rows;
           this.cols = cols;
           initGrid();
           MouseAdapter ma = new MouseAdapter() {
               @Override
               public void mousePressed(MouseEvent e) { handleMouse(e); }
               // no drag
           };
           addMouseListener(ma);
       }
       void setStepper(AI.Stepper s) {
           this.currentStepper = s;
       }
       void initGrid() {
           grid = new int[rows][cols];
       }
       void setGridSize(int newRows, int newCols) {
           this.rows = newRows;
           this.cols = newCols;
           initGrid();
           start = null;
           end = null;
           currentStepper = null;
       }
       void resetGrid() {
           initGrid();
           start = null;
           end = null;
           currentStepper = null;
       }
       void clearWalls() {
           for (int r = 0; r < rows; r++)
               for (int c = 0; c < cols; c++)
                   if (grid[r][c] == WALL || grid[r][c] == PATH)
                       grid[r][c] = EMPTY;
           currentStepper = null;
       }
       void clearPath() {
           for (int r = 0; r < rows; r++)
               for (int c = 0; c < cols; c++)
                   if (grid[r][c] == PATH) grid[r][c] = EMPTY;
       }
       int[][] getGridCopyWithoutPath() {
           int[][] copy = new int[rows][cols];
           for (int r = 0; r < rows; r++)
               System.arraycopy(grid[r], 0, copy[r], 0, cols);
           for (int r = 0; r < rows; r++)
               for (int c = 0; c < cols; c++)
                   if (copy[r][c] == PATH) copy[r][c] = EMPTY;
           return copy;
       }
       Point getStart() { return start; }
       Point getEnd() { return end; }
       void setCell(int row, int col, int value) {
           if (row < 0 || row >= rows || col < 0 || col >= cols) return;
           grid[row][col] = value;
           if (value == START) start = new Point(col, row);
           else if (value == END) end = new Point(col, row);
       }
       private void handleMouse(MouseEvent e) {
           int width = getWidth();
           int height = getHeight();
           double cellW = (double) width / cols;
           double cellH = (double) height / rows;
           int col = (int) (e.getX() / cellW);
           int row = (int) (e.getY() / cellH);
           if (row < 0 || row >= rows || col < 0 || col >= cols) return;
           if (startBtn.isSelected()) {
               if (start != null) grid[start.y][start.x] = EMPTY;
               grid[row][col] = START;
               start = new Point(col, row);
               clearPath();
               repaint();
           } else if (endBtn.isSelected()) {
               if (end != null) grid[end.y][end.x] = EMPTY;
               grid[row][col] = END;
               end = new Point(col, row);
               clearPath();
               repaint();
           } else if (wallBtn.isSelected()) {
               if (grid[row][col] == WALL) grid[row][col] = EMPTY;
               else {
                   if (grid[row][col] == START) start = null;
                   if (grid[row][col] == END) end = null;
                   grid[row][col] = WALL;
               }
               clearPath();
               repaint();
           } else if (eraseBtn.isSelected()) {
               if (grid[row][col] == START) start = null;
               if (grid[row][col] == END) end = null;
               grid[row][col] = EMPTY;
               clearPath();
               repaint();
           }
       }
       @Override
       protected void paintComponent(Graphics g) {
           super.paintComponent(g);
           int width = getWidth();
           int height = getHeight();
           double cellW = (double) width / cols;
           double cellH = (double) height / rows;
           // draw base cells
           for (int r = 0; r < rows; r++) {
               for (int c = 0; c < cols; c++) {
                   int x = (int) (c * cellW);
                   int y = (int) (r * cellH);
                   int w = (int) Math.ceil(cellW);
                   int h = (int) Math.ceil(cellH);
                   switch (grid[r][c]) {
                       case EMPTY -> g.setColor(Color.WHITE);
                       case WALL -> g.setColor(Color.BLACK);
                       case START -> g.setColor(Color.GREEN.darker());
                       case END -> g.setColor(Color.RED.darker());
                       case PATH -> g.setColor(Color.BLUE);
                   }
                   g.fillRect(x, y, w, h);
                   g.setColor(Color.LIGHT_GRAY);
                   g.drawRect(x, y, w, h);
               }
           }
           // overlays for stepper: closed, open, current
           if (currentStepper != null) {
               boolean[][] closed = currentStepper.getClosed();
               boolean[][] inOpen = currentStepper.getInOpen();
               Point cur = currentStepper.getCurrent();
               // closed: translucent gray
               g.setColor(new Color(150, 150, 150, 140));
               for (int r = 0; r < rows; r++) {
                   for (int c = 0; c < cols; c++) {
                       if (closed[r][c]) {
                           int x = (int) (c * cellW);
                           int y = (int) (r * cellH);
                           int w = (int) Math.ceil(cellW);
                           int h = (int) Math.ceil(cellH);
                           g.fillRect(x, y, w, h);
                       }
                   }
               }
               // open: translucent orange
               g.setColor(new Color(255, 165, 0, 120));
               for (int r = 0; r < rows; r++) {
                   for (int c = 0; c < cols; c++) {
                       if (inOpen[r][c]) {
                           int x = (int) (c * cellW);
                           int y = (int) (r * cellH);
                           int w = (int) Math.ceil(cellW);
                           int h = (int) Math.ceil(cellH);
                           g.fillRect(x, y, w, h);
                       }
                   }
               }
               // current node: magenta and draw centered cost text
               if (cur != null) {
                   Graphics2D g2 = (Graphics2D) g.create();
                   int x = (int) (cur.x * cellW);
                   int y = (int) (cur.y * cellH);
                   int w = (int) Math.ceil(cellW);
                   int h = (int) Math.ceil(cellH);
                   // fill current cell
                   g2.setColor(new Color(200, 0, 200, 180));
                   g2.fillRect(x, y, w, h);
                   // prepare cost text
                   double gCost = currentStepper.getCurrentG();
                   double fCost = currentStepper.getCurrentF();
                   String text = String.format("g=%.2f f=%.2f", gCost, fCost);
                   // font sizing: aim for readable but fit
                   int fontSize = Math.max(10, (int) Math.min(w, h) / 4);
                   Font font = g2.getFont().deriveFont(Font.BOLD, fontSize);
                   g2.setFont(font);
                   g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                   FontMetrics fm = g2.getFontMetrics();
                   // center string
                   int textWidth = fm.stringWidth(text);
                   int textHeight = fm.getAscent(); // use ascent for baseline offset
                   int tx = x + (w - textWidth) / 2;
                   int ty = y + (h + textHeight) / 2 - fm.getDescent();
                   // choose contrasting color depending on background brightness
                   g2.setColor(Color.WHITE);
                   g2.drawString(text, tx, ty);
                   g2.dispose();
               }
           }
       }
   }
   public static void main(String[] args) {
       new MazeSolver();
   }
}

