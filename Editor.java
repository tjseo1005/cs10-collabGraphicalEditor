import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Client-server graphical editor
 * 
 * @author Taejun Seo and Stephen Adjei
 * 
 */

public class Editor extends JFrame {	
	private static String serverIP = "localhost";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private int movingId = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged

	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch();

		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};
		
		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});		

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});
		
		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {
		// TODO: YOUR CODE HERE
		// Draw all shapes in sketch
		for (int index: sketch.getShapes().keySet()) {
			sketch.getShapes().get(index).draw(g);
		}

		// Draw current shape to see on the editor
		if (curr != null) {curr.draw(g);}
	}

	// Helpers for event handlers
	
	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {
		// TODO: YOUR CODE HERE
		if (mode == Mode.DRAW) {
			if (shapeType.equals("ellipse")) {
				// Start a new ellipse
				curr = new Ellipse(p.x, p.y, color);
				drawFrom = p;
			}
			else if (shapeType.equals("rectangle")) {
				// Start a new rectangle
				curr = new Rectangle(p.x, p.y, color);
				drawFrom = p;
			}
			else if (shapeType.equals("segment")) {
				curr = new Segment(p.x, p.y, color);
				((Segment)curr).setStart(p.x, p.y);
			}
			else if (shapeType.equals("freehand")) {
				curr = new Polyline(p.x, p.y, color);
			}
		}
		else if (mode == Mode.DELETE || mode == Mode.RECOLOR || mode == Mode.MOVE) {
			for (int idx: sketch.getShapes().keySet()) {
				Shape temp = sketch.getShapes().get(idx);
				if (temp.contains(p.x, p.y)) {
					curr = temp;
					movingId = idx; //curr.getShapeId(); // Get movingId
				}
			}

			if (curr != null) {
				if (mode == Mode.RECOLOR) {
					// Recolor the shape
					curr.setColor(color);
					comm.send(comm.requestToRecolor(movingId, color.getRGB()));
				}
				else if (mode == Mode.MOVE) {
					// Starting to drag
					moveFrom = p;
				}
				else if (mode == Mode.DELETE) {
					// Get rid of the shape
					curr = null;
					comm.send(comm.requestToDelete(movingId));
				}
			}
		}
		// Things have changed no matter what, so repaint
		repaint();
	}

	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {
		// TODO: YOUR CODE HERE
		if (mode == Mode.DRAW) {
			if (shapeType.equals("ellipse")) {
				((Ellipse)curr).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
				repaint();
			}
			else if (shapeType.equals("rectangle")) {
				((Rectangle)curr).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
				repaint();
			}
			else if (shapeType.equals("segment")) {
				((Segment)curr).setEnd(p.x, p.y);
				repaint();
			}
			else if (shapeType.equals("freehand")) {
				// ADD
				((Polyline)curr).addPoint(p.x, p.y);
				repaint();
			}
		}

		else if (mode == Mode.MOVE && moveFrom != null) {
			// Move curr and send to comm
			curr.moveBy(p.x - moveFrom.x, p.y - moveFrom.y);
			comm.send(comm.requestToMove(movingId, p.x - moveFrom.x, p.y - moveFrom.y));
			moveFrom = p;
			repaint();
		}
	}

	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it		
	 */
	private void handleRelease() {
		// TODO: YOUR CODE HERE
		if (mode == Mode.DRAW) {
			comm.send(comm.requestToAdd(curr));
		}
		curr = null; // Set back curr to null once draw, delete, recolor, or move requests sent
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});	
	}
}
