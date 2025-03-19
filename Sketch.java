import java.util.*;

public class Sketch {
    private TreeMap<Integer, Shape> shapes;

    public Sketch() {
        shapes = new TreeMap<Integer, Shape>();
    }

    public TreeMap<Integer, Shape> getShapes() {return shapes;}



    /**
     * method for adding shape to editor, takes ID
     */

    public void addShapeEditor(Integer id, Shape shape) {
        shapes.put(id, shape);
    }

    /**
     * method for adding shape to server, does NOT take ID
     */

    public void addShapeServer(Shape shape) {
        shape.setShapeId(shapes.size());
        shapes.put(shapes.size(), shape);
    }
}
