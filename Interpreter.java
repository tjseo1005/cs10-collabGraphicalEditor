import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @Author: Taejun Seo and Stephen Adjei
 * @Title: Interpreter class for parsing through string-based communications between client and server,
 * and to effectuate programming actions.
 */

public class Interpreter {
    Sketch sketch;

    public Interpreter(Sketch sketch) {
        this.sketch = sketch;
    }

    public void interpretCommand(String command) {
        String[] tokens = command.split(" ");
        if (tokens[0].equals("draw")) {
            if (tokens[1].equals("ellipse")) {
                int x1 = Integer.parseInt(tokens[2]);
                int y1 = Integer.parseInt(tokens[3]);
                int x2 = Integer.parseInt(tokens[4]);
                int y2 = Integer.parseInt(tokens[5]);
                Color color = new Color(Integer.parseInt(tokens[6]));
                if (tokens.length == 7) {
                    sketch.addShapeServer(new Ellipse(x1, y1, x2, y2, color));
                }
                else if (tokens.length == 8) {
                    int idx = Integer.parseInt(tokens[7]);
                    sketch.addShapeEditor(idx, new Ellipse(x1, y1, x2, y2, color));
                }
            }
            if (tokens[1].equals("rectangle")) {
                int x1 = Integer.parseInt(tokens[2]);
                int y1 = Integer.parseInt(tokens[3]);
                int x2 = Integer.parseInt(tokens[4]);
                int y2 = Integer.parseInt(tokens[5]);
                Color color = new Color(Integer.parseInt(tokens[6]));
                if (tokens.length == 7) {
                    sketch.addShapeServer(new Rectangle(x1, y1, x2, y2, color));
                }
                else if (tokens.length == 8) {
                    int idx = Integer.parseInt(tokens[7]);
                    sketch.addShapeEditor(idx, new Rectangle(x1, y1, x2, y2, color));
                }
            }
            if (tokens[1].equals("segment")) {
                int x1 = Integer.parseInt(tokens[2]);
                int y1 = Integer.parseInt(tokens[3]);
                int x2 = Integer.parseInt(tokens[4]);
                int y2 = Integer.parseInt(tokens[5]);
                Color color = new Color(Integer.parseInt(tokens[6]));
                if (tokens.length == 7) {
                    sketch.addShapeServer(new Segment(x1, y1, x2, y2, color));
                }
                else if (tokens.length == 8) {
                    int idx = Integer.parseInt(tokens[7]);
                    sketch.addShapeEditor(idx, new Rectangle(x1, y1, x2, y2, color));
                }
            }
            if (tokens[1].equals("polyline")) {
                String[] polylineTokens = command.split("/");

                // Get points
                String[] coordinates = polylineTokens[1].split(" ");
                List<Point> listPoints = new ArrayList<Point>();
                for (int i = 0; i < coordinates.length; i += 2) {
                    if (tokens[i] != null && tokens[i+1] != null) {
                        int xCoord = Integer.parseInt(coordinates[i]);
                        int yCoord = Integer.parseInt(coordinates[i + 1]);
                        listPoints.add(new Point(xCoord, yCoord));
                    }
                }

                // Get color
                Color color = new Color(Integer.parseInt(polylineTokens[2]));

                // If editor --> server
                if (polylineTokens.length == 3) {
                    sketch.addShapeServer(new Polyline(listPoints, color));
                }
                // If server --> editor
                if (polylineTokens.length == 4) {
                    int idx = Integer.parseInt(polylineTokens[3]);
                    sketch.addShapeEditor(idx, new Polyline(listPoints, color));
                }
            }
        }
        if (tokens[0].equals("move")) {
            int id = Integer.parseInt(tokens[1]);
            int dx = Integer.parseInt(tokens[2]);
            int dy = Integer.parseInt(tokens[3]);
            System.out.println(sketch.getShapes());
            sketch.getShapes().get(id).moveBy(dx, dy);
        }
        if (tokens[0].equals("delete")) {
            int id = Integer.parseInt(tokens[1]);
            sketch.getShapes().remove(id);
        }
        if (tokens[0].equals("recolor")) {
            int id = Integer.parseInt(tokens[1]);
            Color color = new Color(Integer.parseInt(tokens[2]));
            sketch.getShapes().get(id).setColor(color);
        }
    }
}
