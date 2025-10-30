package pack;
import java.awt.Point;
import java.util.*;
/**
* AI - A* solver with selectable heuristics (Manhattan, Euclidean, Chebyshev).
*
* Public API:
*   public static List<Point> solve(int[][] grid, Point start, Point end, Heuristic heuristic)
*   public static class Stepper { ... } // stepper for interactive stepping
*
* Notes:
* - grid is indexed as grid[row][col], where row = y, col = x.
* - Points are used with x=col, y=row (same as MazeSolver).
* - Return value is a List<Point> from start to end (inclusive).
* - Returns null if no path exists.
* - Movement: 4-directional (up/down/left/right) with uniform cost = 1 per step.
*/
public class AI {
   // Local wall constant (grid value used for walls in MazeSolver)
   private static final int WALL = 1;
   public enum Heuristic {
       MANHATTAN,
       EUCLIDEAN,
       CHEBYSHEV
   }
   private static class Node {
       int x, y;
       double g; // cost from start
       double f; // g + h
       Node parent;
       Node(int x, int y, double g, double f, Node parent) {
           this.x = x;
           this.y = y;
           this.g = g;
           this.f = f;
           this.parent = parent;
       }
       Point toPoint() {
           return new Point(x, y);
       }
   }
   /**
    * Regular A* that returns full path (used when not stepping).
    */
   public static List<Point> solve(int[][] grid, Point start, Point end, Heuristic heuristic) {
       if (grid == null || start == null || end == null || heuristic == null) return null;
       int rows = grid.length;
       if (rows == 0) return null;
       int cols = grid[0].length;
       if (!inBounds(start.x, start.y, cols, rows) || !inBounds(end.x, end.y, cols, rows)) return null;
       if (grid[start.y][start.x] == WALL || grid[end.y][end.x] == WALL) return null;
       boolean[][] closed = new boolean[rows][cols];
       Node[][] nodes = new Node[rows][cols];
       Comparator<Node> cmp = Comparator.comparingDouble((Node n) -> n.f).thenComparingDouble(n -> n.g);
       PriorityQueue<Node> open = new PriorityQueue<>(cmp);
       double startH = heuristicValue(start.x, start.y, end.x, end.y, heuristic);
       Node startNode = new Node(start.x, start.y, 0.0, startH, null);
       nodes[start.y][start.x] = startNode;
       open.add(startNode);
       int[] dx = {1, -1, 0, 0};
       int[] dy = {0, 0, 1, -1};
       while (!open.isEmpty()) {
           Node cur = open.poll();
           if (closed[cur.y][cur.x]) continue;
           closed[cur.y][cur.x] = true;
           if (cur.x == end.x && cur.y == end.y) {
               LinkedList<Point> path = new LinkedList<>();
               Node n = cur;
               while (n != null) {
                   path.addFirst(n.toPoint());
                   n = n.parent;
               }
               return path;
           }
           for (int k = 0; k < 4; k++) {
               int nx = cur.x + dx[k];
               int ny = cur.y + dy[k];
               if (!inBounds(nx, ny, cols, rows)) continue;
               if (grid[ny][nx] == WALL) continue;
               if (closed[ny][nx]) continue;
               double tentativeG = cur.g + 1.0;
               Node neighbor = nodes[ny][nx];
               double h = heuristicValue(nx, ny, end.x, end.y, heuristic);
               double f = tentativeG + h;
               if (neighbor == null) {
                   neighbor = new Node(nx, ny, tentativeG, f, cur);
                   nodes[ny][nx] = neighbor;
                   open.add(neighbor);
               } else {
                   if (tentativeG < neighbor.g) {
                       neighbor.g = tentativeG;
                       neighbor.f = f;
                       neighbor.parent = cur;
                       open.add(neighbor);
                   }
               }
           }
       }
       return null; // no path
   }
   private static boolean inBounds(int x, int y, int cols, int rows) {
       return x >= 0 && x < cols && y >= 0 && y < rows;
   }
   private static double heuristicValue(int x1, int y1, int x2, int y2, Heuristic h) {
       int dx = Math.abs(x1 - x2);
       int dy = Math.abs(y1 - y2);
       switch (h) {
           case EUCLIDEAN:
               return Math.hypot(dx, dy);
           case CHEBYSHEV:
               return (double) Math.max(dx, dy);
           default:
               return (double) (dx + dy);
       }
   }
   /**
    * Stepper class: exposes the A* search as single-step operations.
    * Call step() once per user action. Query overlays via getters.
    */
   public static class Stepper {
       public enum Status { RUNNING, FOUND, FAILED }
       private final int rows, cols;
       private final int[][] grid;
       private final Point start, end;
       private final Heuristic heuristic;
       private final boolean[][] closed;
       private final boolean[][] inOpen;
       private final Node[][] nodes;
       private final PriorityQueue<Node> open;
       private Node current = null;
       private int steps = 0;      // number of step() calls that processed a node
       private int visited = 0;    // number of nodes popped/closed
       public Stepper(int[][] grid, Point start, Point end, Heuristic heuristic) {
           this.rows = grid.length;
           this.cols = grid[0].length;
           this.grid = new int[rows][cols];
           for (int r = 0; r < rows; r++) System.arraycopy(grid[r], 0, this.grid[r], 0, cols);
           this.start = start;
           this.end = end;
           this.heuristic = heuristic;
           closed = new boolean[rows][cols];
           inOpen = new boolean[rows][cols];
           nodes = new Node[rows][cols];
           Comparator<Node> cmp = Comparator.comparingDouble((Node n) -> n.f).thenComparingDouble(n -> n.g);
           open = new PriorityQueue<>(cmp);
           double startH = heuristicValue(start.x, start.y, end.x, end.y, heuristic);
           Node s = new Node(start.x, start.y, 0.0, startH, null);
           nodes[start.y][start.x] = s;
           open.add(s);
           inOpen[start.y][start.x] = true;
       }
       /**
        * Perform one step: pop one node (skipping already-closed duplicates), mark closed,
        * and expand its neighbors (adding them into the open set).
        * Returns Status.FOUND if target found, Status.FAILED if open exhausted, otherwise RUNNING.
        */
       public Status step() {
           if (open.isEmpty()) return Status.FAILED;
           // Poll until we get a node that's not closed (to handle duplicates pushed)
           Node node = null;
           while (!open.isEmpty()) {
               node = open.poll();
               if (node == null) break;
               // skip nodes that are already closed (stale duplicates)
               if (closed[node.y][node.x]) continue;
               // valid node
               inOpen[node.y][node.x] = false;
               break;
           }
           if (node == null) return Status.FAILED;
           // mark as current/closed
           current = node;
           closed[current.y][current.x] = true;
           visited++;
           steps++;
           // if goal, reconstruct path
           if (current.x == end.x && current.y == end.y) {
               return Status.FOUND;
           }
           // expand neighbors (4-way)
           int[] dx = {1, -1, 0, 0};
           int[] dy = {0, 0, 1, -1};
           for (int k = 0; k < 4; k++) {
               int nx = current.x + dx[k];
               int ny = current.y + dy[k];
               if (!inBounds(nx, ny, cols, rows)) continue;
               if (grid[ny][nx] == WALL) continue;
               if (closed[ny][nx]) continue;
               double tentativeG = current.g + 1.0;
               Node neighbor = nodes[ny][nx];
               double h = heuristicValue(nx, ny, end.x, end.y, heuristic);
               double f = tentativeG + h;
               if (neighbor == null) {
                   neighbor = new Node(nx, ny, tentativeG, f, current);
                   nodes[ny][nx] = neighbor;
                   open.add(neighbor);
                   inOpen[ny][nx] = true;
               } else {
                   if (tentativeG < neighbor.g) {
                       neighbor.g = tentativeG;
                       neighbor.f = f;
                       neighbor.parent = current;
                       open.add(neighbor);
                       inOpen[ny][nx] = true;
                   }
               }
           }
           return open.isEmpty() ? Status.FAILED : Status.RUNNING;
       }
       /** Reconstruct path after a FOUND step; returns empty list if not found. */
       public List<Point> getPath() {
           if (current == null) return List.of();
           if (!(current.x == end.x && current.y == end.y)) return List.of();
           LinkedList<Point> path = new LinkedList<>();
           Node n = current;
           while (n != null) {
               path.addFirst(n.toPoint());
               n = n.parent;
           }
           return path;
       }
       public boolean[][] getClosed() { return closed; }
       public boolean[][] getInOpen() { return inOpen; }
       public Point getCurrent() { return current == null ? null : new Point(current.x, current.y); }
       public int getSteps() { return steps; }
       public int getVisitedCount() { return visited; }
       public int getOpenCount() { return open.size(); }
       /** Return current node's g (cost from start). NaN if none. */
       public double getCurrentG() { return current == null ? Double.NaN : current.g; }
       /** Return current node's f (g + h). NaN if none. */
       public double getCurrentF() { return current == null ? Double.NaN : current.f; }
   }
}

