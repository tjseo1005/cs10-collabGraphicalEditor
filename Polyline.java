import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 */
public class Polyline implements Shape {
	// TODO: YOUR CODE HERE

	private List<Point> points = new ArrayList<Point>();
	private Color color;
	private int polylineId;

	/**
	 * Initial 0-length Polyline at a point
	 */
	public Polyline(int x1, int y1, Color color) {
		points.add(new Point(x1, y1));
		this.color = color;
	}

	public Polyline(List<Point> listPoints, Color color) {
		points = listPoints;
		this.color = color;
	}

	@Override
	public void moveBy(int dx, int dy) {
		for (Point point: points) {
			point.x = point.x + dx;
			point.y = point.y + dy;
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public int getShapeId() {return polylineId;}

	public void setShapeId(int i) {polylineId = i;}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	public void addPoint(int x, int y) {
		points.add(new Point(x, y));
	}

	@Override
	public boolean contains(int x, int y) {
		if (points.contains(new Point(x, y))) {return true;}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		System.out.println(points);
		g.setColor(color);
		for (int i = 0; i < points.size() -1; i++) {
			g.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y);
		}
	}

	@Override
	public String toString() {
		String result = "polyline /";
		for (Point point: points) {
			result += (point.x + " " + point.y + " ");
		}
		result = result + "/" + color.getRGB();
		return result;
	}
}
