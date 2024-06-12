package MazeSolver;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;



import java.util.LinkedList;
//import MazeSolver.RectanglePanel;
//import MazeSolver.MazeSolver;
public class MazeSolver extends JFrame {
	
	public MazeSolver() {
        setTitle("Centered Rectangle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        RectanglePanel panel = new RectanglePanel();
        getContentPane().add(panel);

        setUndecorated(true); // Remove window decorations
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
        setVisible(true);

        panel.requestFocusInWindow(); // Request focus for key events
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MazeSolver::new);
    }
}
class RectanglePanel extends JPanel {
	//static LinkedList<Rectangle> recs = new LinkedList<Rectangle>();	
	static int rows = 5;
	static int columns = 5;
	static int[][] maze = new int[columns][rows];
	//public static RectanglePanel panel = new RectanglePanel();
	static Color[][] colors = new Color[columns][rows];
	static Rectangle[][] recs = new Rectangle[columns][rows];
	static int gridsize = 1000;
	static int width = (gridsize / columns) - 10;
	static int height = (gridsize / rows) - 10;
	static Rectangle incCol;
	static Rectangle decCol;
	static Rectangle incRow;
	static Rectangle decRow;
	static Rectangle wall;
	static Rectangle start;
	static Rectangle end;
	static Rectangle grid;
	static Rectangle solve;
	static String selected = "grid";
	static boolean invalidity = false;
	static Node resultNode;
	static Text acoach;
	static String aString = "hello";
	static Rectangle whichOne;
	private Timer timer;
	private int count = 0;
	private static boolean mazeEnd = false;
	public static boolean solved = false;
	private static LinkedList<Coordinate> cords = new LinkedList<Coordinate>();
	public RectanglePanel() {
		setFocusable(true);
		requestFocusInWindow();
		try {
			
			//dragonImage = ImageIO.read(imageFile);
		}
		catch (Exception ex) {
			
		}
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				maze[i][j] = 0;
			}
		}
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				colors[i][j] = Color.black;
			}
		}
		addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0); // Exit the application
                }
            }
        });
		addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unused")
		    @Override
		    public void mouseClicked(MouseEvent e) {
				if (mazeEnd) {
					return;
				}
				if (solve.contains(e.getX(), e.getY())) {
					mazeEnd = true;
					aString = "Loading...";
					repaint();
					SwingUtilities.invokeLater(() -> {
					    solved = AI.solve(maze);
					    if (solved) {
							//aString = "Loading...";
							int[][] yes = resultNode.state;
							for (int i = 0; i < yes.length; i++) {
								for(int j = 0; j < yes[0].length; j++) {
									if (yes[i][j] == 10) {
//										colors[i][j] = Color.blue;
										Coordinate cc = new Coordinate(i, j);
										cords.add(cc);
									}
								}
							}
							//repaint();
							timer = new Timer(100, new ActionListener() {
					            @Override
					            public void actionPerformed(ActionEvent e) {
					            	if (cords.size() == 0) {
					            		System.out.println(3232);
					            		aString = "Maze Solved!";
					                	timer.stop();
					                	repaint();
					                	return;
					            	}
					                colors[cords.get(count).x][cords.get(count).y] = Color.blue;
					                count++;				                
					                repaint(); // Repaint the panel
					                if (count >= cords.size()) {
					                	aString = "Maze Solved!";
					                	timer.stop();
					                }
					            }
					        });
					        timer.setInitialDelay(0); // Start the timer immediately
					        timer.start(); // Start the timer
					        //repaint();
						}
						else {
							if (RectanglePanel.invalidity == true) {
								aString = "Invalid maze";
							}
							else {
								aString = "Unsolvable";
							}
							repaint();
						}
					});
					
				}
				if (wall.contains(e.getX(), e.getY())) {
					selected = "wall";
					repaint();
				}
				else if (start.contains(e.getX(), e.getY())) {
					selected = "start";
					repaint();
				}
				else if (end.contains(e.getX(), e.getY())) {
					selected = "end";
					repaint();
				}
				else if (grid.contains(e.getX(), e.getY())) {
					selected = "grid";
					repaint();
				}
		        for (int i = 0; i < recs.length; i++) {
		        	for (int j = 0; j < recs[0].length; j++) {
		        		Rectangle r = recs[i][j];
		        		if (e.getX() >= r.xPos && e.getX() <= r.xPos + r.width && e.getY() >= r.yPos && e.getY() <= r.yPos + r.height) {
		        			if (selected.equals("wall")) {
		        				colors[i][j] = Color.yellow;
				        		maze[i][j] = 1;
		        			}
		        			else if (selected.equals("start")) {
		        				colors[i][j] = Color.green;
		        				//System.out.println("in start");
		        				maze[i][j] = 2;
		        			}
		        			else if (selected.equals("end")) {
		        				colors[i][j] = Color.red;
		        				maze[i][j] = 3;
		        			}
		        			else if (selected.equals("grid")) {
		        				colors[i][j] = Color.black;
		        				maze[i][j] = 0;
		        			}
			        		repaint();
			        	}
		        		
		        	}
		        }
		        if (incCol.contains(e.getX(), e.getY())) {		        	
		        	rows +=1;
		        	colors = new Color[columns][rows];
		        	recs = new Rectangle[columns][rows];
		        	for (int i = 0; i < columns; i++) {
		    			for (int j = 0; j < rows; j++) {
		    				colors[i][j] = Color.black;
		    			}
		    		}
		        	height = (1000/rows) - 10;
		        	repaint();
		        	maze = new int[columns][rows];
		        	for (int i = 0; i < columns; i++) {
			    		for (int j = 0; j < rows; j++) {
			    			maze[i][j] = 0;
			    		}
			    	}
		        }
		        else if (decCol.contains(e.getX(), e.getY())) {
		        	rows --;
		        	colors = new Color[columns][rows];
		        	recs = new Rectangle[columns][rows];
		        	for (int i = 0; i < columns; i++) {
		    			for (int j = 0; j < rows; j++) {
		    				colors[i][j] = Color.black;
		    			}
		    		}
		        	try {
		        		height = (1000/rows) - 10;
		        	}
		        	catch (Exception ex) {
		        		
		        	}
		        	repaint();
		        	maze = new int[columns][rows];
		        	for (int i = 0; i < columns; i++) {
			    		for (int j = 0; j < rows; j++) {
			    			maze[i][j] = 0;
			    		}
			    	}
		        }
		        else if (incRow.contains(e.getX(), e.getY())) {
		        	columns+=1;
		        	colors = new Color[columns][rows];
		        	recs = new Rectangle[columns][rows];
		        	for (int i = 0; i < columns; i++) {
		    			for (int j = 0; j < rows; j++) {
		    				colors[i][j] = Color.black;
		    			}
		    		}
		        	width = (1000/columns) - 10;
		        	repaint();
		        	maze = new int[columns][rows];
		        	for (int i = 0; i < columns; i++) {
			    		for (int j = 0; j < rows; j++) {
			    			maze[i][j] = 0;
			    		}
			    	}
		        }
		        else if (decRow.contains(e.getX(), e.getY())) {
		        	columns--;
		        	colors = new Color[columns][rows];
		        	recs = new Rectangle[columns][rows];
		        	for (int i = 0; i < columns; i++) {
		    			for (int j = 0; j < rows; j++) {
		    				colors[i][j] = Color.black;
		    			}
		    		}
		        	try {
		        		width = (1000/columns) - 10;
		        	}
		        	catch (Exception ex) {
		        		
		        	}
		        	repaint();
		        	maze = new int[columns][rows];
		        	for (int i = 0; i < columns; i++) {
			    		for (int j = 0; j < rows; j++) {
			    			maze[i][j] = 0;
			    		}
			    	}
		        }
		        
		        
		    }
		});
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//System.out.println(selected);
		//g.drawImage(dragonImage, 100, 100, this);
		if (selected.equals("wall")) {
			whichOne = new Rectangle(340, 640, 20, 20, Color.black, g);
			whichOne.draw(g);
		}
		else if (selected.equals("start")) {
			whichOne = new Rectangle(340, 760, 20, 20, Color.black, g);
			whichOne.draw(g);
		}
		else if (selected.equals("end")) {
			whichOne = new Rectangle(340, 880, 20, 20, Color.black, g);
			whichOne.draw(g);
		}
		else if (selected.equals("grid")) {
			whichOne = new Rectangle(340, 1000, 20, 20, Color.black, g);
			whichOne.draw(g);
		}
		acoach = new Text(aString, 60, 100, 200, g);
		acoach.draw();
		
		Text ic = new Text("incCol", 25, 230, 390, g);
		ic.draw();
		Text dc = new Text("decCol", 25, 230, 548, g);
		dc.draw();
		Text ir = new Text("incRow", 25, 105, 390, g);
		ir.draw();
		Text dr = new Text("decRow", 25, 105, 548, g);
		dr.draw();

		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				Color c = colors[i][j];
				double padr = 0.15*Math.pow(rows, 1.2);
				double padc = 0.15*Math.pow(columns, 1.2);
				double p = gridsize, a = p/(columns+padc), b = p/(rows+padr);
//				Rectangle rect = new Rectangle(500 + (i*(width + a)),30 + (j * (height + b)), width+a/2, height+b/2, c, g);
				var rect =  Rectangle.create(500+i*(a+a/columns*padc),30+j*(b+b/rows*padr),a,b,c,g);
				recs[i][j] = rect;
				rect.draw(g);
			}
		}
		incCol = new Rectangle(100, 400, 100, 50, Color.green, g);
		incCol.draw(g);
		decCol = new Rectangle(100, 470, 100, 50, Color.red, g);
		decCol.draw(g);
		incRow = new Rectangle(220, 400, 100, 50, Color.green, g);
		incRow.draw(g);
		decRow = new Rectangle(220, 470, 100, 50, Color.red, g);
		decRow.draw(g);
		wall = new Rectangle(220, 600, 100, 100, Color.yellow, g);
		wall.draw(g);
		start = new Rectangle(220, 720, 100, 100, Color.green, g);
		start.draw(g);
		end = new Rectangle(220, 840, 100, 100, Color.red, g);
		end.draw(g);
		grid = new Rectangle(220, 960, 100, 100, Color.black, g);
		grid.draw(g);
		solve = new Rectangle(1600, 600, 200, 100, Color.cyan, g);
		solve.draw(g);
		Text wt = new Text("wall", 30, 242, 655, g);
		wt.color = Color.black;
		wt.draw();
		Text st = new Text("start", 30, 238, 775, g);
		st.color = Color.black;
		st.draw();
		Text et = new Text("end", 30, 242, 895, g);
		et.color = Color.black;
		et.draw();
		Text gt = new Text("grid", 30, 242, 1015, g);
		gt.color = Color.white;
		gt.draw();
		Text solve = new Text("solve", 30, 1660, 660, g);
		solve.color = Color.black;
		solve.draw();
	}
}
class Rectangle {
    int width;
    int height;
    int xPos;
    int yPos;
    Color color;
    Graphics g;

    private static int r(double d) { return (int) Math.round(d); }
    public static Rectangle create(double a,double b, double c, double d, Color e, Graphics f) {
    	return new Rectangle(r(a),r(b),r(c),r(d),e,f);
    }
    public Rectangle(int xPos, int yPos, int width, int height, Color color, Graphics g) {
        this.width = width;
        this.height = height;
        this.xPos = xPos;
        this.yPos = yPos;
        this.color = color;
        this.g = g;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(xPos, yPos, width, height);
       
    }
    public void redraw(Graphics g, String direction, int num) {
    if (direction.equals("right")) {
    g.clearRect(xPos - 10, yPos, width, height);
    }
    else if (direction.equals("left")) {
    g.clearRect(xPos + 10, yPos, width, height);
    }
    else if (direction.equals("up")) {
    g.clearRect(xPos, yPos + 10, width, height);
    }
    else {
    g.clearRect(xPos, yPos - 10, width, height);
    }
   
    g.setColor(color);
        g.fillRect(xPos, yPos, width, height);
   
    }

    public void move(String direction, int num) {
        if (direction.equals("right")) {
        xPos += num;
        }
        else if (direction.equals("left")) {
        xPos -= num;
        }
        else if (direction.equals("up")) {
        yPos -= num;
        }
        else {
        yPos += num;
        }
    }
    public boolean contains(int x, int y) {
    if (x >= xPos && x <= xPos + width && y >= yPos && y <= yPos + height) {
    return true;
    }
    return false;
    }
    public void draww() {
    System.out.println("j");
    this.draw(g);
    }
    public void setColor(Color color) {
    this.color = color;
    }
}
class Text {
	private String content;
	private int size;
	private int xPos;
	private int yPos;
	private static Graphics2D g2d;
	private Graphics g;
	public Color color;
	public Text(String c, int s, int x, int y, Graphics g) {
		content = c;
		size = s;
		xPos = x;
		yPos = y;
		this.g = g;
		g2d = (Graphics2D) g;
	}
	public void draw() {
		Font font = new Font("Arial", Font.BOLD, size); // Creating a larger font
        g2d.setFont(font);
        g2d.setColor(color);
        g2d.drawString(content, xPos, yPos);
	}
	public void setText(String c) {
		this.content = c;
	}
}