package MazeSolver;
import java.util.LinkedList;
import java.util.Queue;
public class AI {
	//static int[][] actions = new int[RectanglePanel.maze.length][RectanglePanel.maze[0].length];
	static LinkedList<Node> frontier = new LinkedList<Node>();
	public static boolean solve(int[][] maze) {		
		LinkedList<Node> exploredSet = new LinkedList<Node>();
		RectanglePanel.aString = "loading";
		//RectanglePanel.panel.repaint();
		//LinkedList<Integer> mDists = new LinkedList<Integer>();
		Node init = new Node(maze);
		init.current = positionOf("start", init.state);
		init.m = Math.abs(Node.endPos.x - init.current.x) + Math.abs(Node.endPos.y - init.current.y);
		init.steps = 0;
		init.mdist = init.m+init.steps;
		frontier.add(init);
		Coordinate c = positionOf("start", init.state);
		int counter = 0;
		exploredSet.add(init);
		if (checkValidity(maze) != true) {
			//System.out.println(2);
			RectanglePanel.invalidity = true;
			return false;
		}
		while (!frontier.isEmpty()) {
			//System.out.println(2);
			int index = 0;
			int least = 999999999;
			for (int i = 0; i < frontier.size(); i++) {
				Node nn = frontier.get(i);
				if (nn.m < least) {
					least = nn.m;
					index = i;
				}
			}
			//System.out.println(index);
			Node n = frontier.remove(index);
//			System.out.println(n.m);
//			System.out.println(n.current.x + " , " + n.current.y);
			//System.out.println(n.current.y);
			//System.out.println(n.state[3][4]);
			if (GoalTest(n.state)) {
				//System.out.println(2);
				RectanglePanel.resultNode = n;
				System.out.println(counter);
				return true;
			}
			else {
				if (counter != 0) {
					c = n.current;
				}
				counter++;
//				if (counter > (maze.length * maze[0].length)*2) { 
//					return false;
//				}
//				System.out.println(c.x);
//				System.out.println(c.y);
				int xymin;
				int xyplus;
				int xminy;
				int xplusy;
				try {
					xymin = n.state[c.x][c.y-1];
				}
				catch (Exception ex) {
					xymin = 1;
				}
				try {
					xyplus = n.state[c.x][c.y+1];
				}
				catch (Exception ex) {
					xyplus = 1;
				}
				try {
					xminy = n.state[c.x-1][c.y];
				}
				catch (Exception ex) {
					xminy = 1;
				}
				try {
					xplusy = n.state[c.x+1][c.y];
				}
				catch (Exception ex) {
					xplusy = 1;
				}
				if (xymin != 1) {
					//System.out.println(2);
					int[][] temp = new int[n.state.length][n.state[0].length];
					for (int i = 0; i < temp.length; i++) {
						for (int j = 0; j < temp[0].length; j++) {
							temp[i][j] = n.state[i][j];
						}
					}
					if (temp[c.x][c.y-1] == 3) {
						temp[c.x][c.y-1] = 13;
					}
					else if (temp[c.x][c.y-1] != 10 && temp[c.x][c.y-1] != 2) {
						temp[c.x][c.y-1] = 10;
					}
					
					Node n2 = new Node(temp);
					n2.steps = n.steps;
					if (temp[c.x][c.y-1] != 20 && temp[c.x][c.y-1] != 12) {
						n2.current = new Coordinate(c.x, c.y-1);
						n2.m = Math.abs(Node.endPos.x - n2.current.x) + Math.abs(Node.endPos.y - n2.current.y);
						n2.steps++;
						n2.mdist = n2.m + n2.steps;
						if (!inExplored(n2, exploredSet)) {
							exploredSet.add(n2);
							frontier.add(n2);
							//System.out.println(n2.mdist);
						}
						
					}					
				}
				if (xyplus != 1) {					
					int[][] temp = new int[n.state.length][n.state[0].length];
					for (int i = 0; i < temp.length; i++) {
						for (int j = 0; j < temp[0].length; j++) {
							temp[i][j] = n.state[i][j];
						}
					}
					if (temp[c.x][c.y+1] == 3) {
						temp[c.x][c.y+1] = 13;
					}
					else if (temp[c.x][c.y+1] != 10 && temp[c.x][c.y+1] != 2) {
						temp[c.x][c.y+1] = 10;
					}
					Node n2 = new Node(temp);
					n2.steps = n.steps;
					if (temp[c.x][c.y+1] != 20 && temp[c.x][c.y+1] != 12) {
						n2.current = new Coordinate(c.x, c.y+1);
						n2.m = Math.abs(Node.endPos.x - n2.current.x) + Math.abs(Node.endPos.y - n2.current.y);
						n2.steps++;
						n2.mdist = n2.m + n2.steps;
						if (!inExplored(n2, exploredSet)) {
							exploredSet.add(n2);
							frontier.add(n2);
							//System.out.println(n2.mdist);
						}
					}					
				}
				if (xminy != 1) {					
					int[][] temp = new int[n.state.length][n.state[0].length];
					for (int i = 0; i < temp.length; i++) {
						for (int j = 0; j < temp[0].length; j++) {
							temp[i][j] = n.state[i][j];
						}
					}
					if (temp[c.x-1][c.y] == 3) {
						temp[c.x-1][c.y] = 13;
					}
					else if (temp[c.x-1][c.y] != 10 && temp[c.x-1][c.y] != 2) {
						temp[c.x-1][c.y] = 10;
					}
					Node n2 = new Node(temp);
					n2.steps = n.steps;
					if (temp[c.x-1][c.y] != 20 && temp[c.x-1][c.y] != 12) {
						n2.current = new Coordinate(c.x-1, c.y);
						//System.out.println(333);
						n2.m = Math.abs(Node.endPos.x - n2.current.x) + Math.abs(Node.endPos.y - n2.current.y);
						n2.steps++;
						n2.mdist = n2.m + n2.steps;
						if (!inExplored(n2, exploredSet)) {
							exploredSet.add(n2);
							frontier.add(n2);
							//System.out.println(n2.mdist);
						}
					}					
				}
				if (xplusy != 1) {					
					int[][] temp = new int[n.state.length][n.state[0].length];
					for (int i = 0; i < temp.length; i++) {
						for (int j = 0; j < temp[0].length; j++) {
							temp[i][j] = n.state[i][j];
						}
					}
					if (temp[c.x+1][c.y] == 3) {
						temp[c.x+1][c.y] = 13;
					}
					else if (temp[c.x+1][c.y] != 10 && temp[c.x+1][c.y] != 2) {
						temp[c.x+1][c.y] = 10;
					}
					Node n2 = new Node(temp);
					n2.steps = n.steps;
					if (temp[c.x+1][c.y] != 20 && temp[c.x+1][c.y] != 12) {
						n2.current = new Coordinate(c.x+1, c.y);
						//System.out.println(2);
						n2.m = Math.abs(Node.endPos.x - n2.current.x) + Math.abs(Node.endPos.y - n2.current.y);
						n2.steps++;
						n2.mdist = n2.m + n2.steps;
						if (!inExplored(n2, exploredSet)) {
							exploredSet.add(n2);
							frontier.add(n2);
							//System.out.println(n2.mdist);
						}
					}					
				}
				if (GoalTest(n.state)) {
					//System.out.println(2);
					RectanglePanel.resultNode = n;
					return true;
				}
			}
		}
		return false;
	}
	public static void mSort() {
        if (frontier == null || frontier.size() <= 1) {
            return;
        }
        int middle = frontier.size() / 2;
        LinkedList<Node> leftList = new LinkedList<>(frontier.subList(0, middle));
        LinkedList<Node> rightList = new LinkedList<>(frontier.subList(middle, frontier.size()));
        
        mergeSort(leftList);
        mergeSort(rightList);
        
        merge(frontier, leftList, rightList);
    }
	public static void mergeSort(LinkedList<Node> frontier) {
        if (frontier == null || frontier.size() <= 1) {
            return;
        }
        int middle = frontier.size() / 2;
        LinkedList<Node> leftList = new LinkedList<>(frontier.subList(0, middle));
        LinkedList<Node> rightList = new LinkedList<>(frontier.subList(middle, frontier.size()));
        
        mergeSort(leftList);
        mergeSort(rightList);
        
        merge(frontier, leftList, rightList);
    }
    private static void merge(LinkedList<Node> frontier, LinkedList<Node> leftList, LinkedList<Node> rightList) {
        int leftIndex = 0, rightIndex = 0, frontierIndex = 0;
        
        while (leftIndex < leftList.size() && rightIndex < rightList.size()) {
            if (leftList.get(leftIndex).m <= rightList.get(rightIndex).m) {
                frontier.set(frontierIndex++, leftList.get(leftIndex++));
            } else {
                frontier.set(frontierIndex++, rightList.get(rightIndex++));
            }
        }
        
        while (leftIndex < leftList.size()) {
            frontier.set(frontierIndex++, leftList.get(leftIndex++));
        }
        
        while (rightIndex < rightList.size()) {
            frontier.set(frontierIndex++, rightList.get(rightIndex++));
        }
    }
	public static boolean checkValidity(int[][] maze) {
		int numStart = 0;
		int numEnd = 0;
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[0].length; j++) {
				if (maze[i][j] == 2) {
					numStart++;
				}
				else if (maze[i][j] == 3) {
					numEnd++;
				}
			}
		}
		if (numStart != 1 || numEnd != 1) {
			//System.out.println(numStart);
			return false;
		}
		return true;
	}
	public static boolean GoalTest(int[][] maze) {
		for (int i = 0; i < maze.length; i++) {
			for(int j = 0; j < maze[0].length; j++) {
				if (maze[i][j] == 13) {
					return true;
				}
			}
		}
		return false;
		
	}
	public static boolean inExplored(Node n, LinkedList<Node> exploredSet) {
		for (int i = 0; i < exploredSet.size(); i++) {
			int[][] arr1 = exploredSet.get(i).state;
			int[][] arr2 = n.state;
			int size = 0;
			int b = 0;
			for (int j = 0; j < arr1.length; j++) {
				for (int k = 0; k < arr1[0].length; k++) {
					size++;
					if (arr1[j][k] == arr2[j][k]) {
						b++;
					}
				}
			}
			if (size == b) {
			
				return true;
			}
		}
		
		return false;
	}
	public static Coordinate positionOf(String s, int[][] maze) {
		int xPos = 0;
		int yPos = 0;
		if (s.equals("start")) {
			for (int i = 0; i < maze.length; i++) {
				for (int j = 0; j < maze[0].length; j++) {
					if (maze[i][j] == 2) {
						xPos = i;
						yPos = j;
					}
				}
			}
		}
		else if (s.equals("end")) {
			for (int i = 0; i < maze.length; i++) {
				for (int j = 0; j < maze[0].length; j++) {
					if (maze[i][j] == 3) {
						xPos = i;
						yPos = j;
					}
				}
			}
		}
		Coordinate c = new Coordinate(xPos, yPos);
		return c;
	}
//	public static void clear() {
//		for (int i = 0; i < actions.length; i++) {
//			for (int j = 0; j < actions[0].length; j++) {
//				actions[i][j] = 0;
//			}
//		}
//	}
}
class Node {
	static Coordinate endPos = AI.positionOf("end", RectanglePanel.maze);
	int[][] state;
	Node parentNode;
	Coordinate current;
	int m;
	int mdist;
	int steps;
	public Node(int[][] s, Node n) {
		state = s;
		parentNode = n;
		steps = 0;
		
	}
	public Node(int[][] s) {
		state = s;
	}
	public int[][] getState() {
		return state;
	}
	public Node getNode() {
		return parentNode;
	}
}
//record Coordinate(int x, int y) {};
class Coordinate {
	int x;
	int y;
	public Coordinate (int x, int y) {
		this.x = x;
		this.y = y;
	}
}
