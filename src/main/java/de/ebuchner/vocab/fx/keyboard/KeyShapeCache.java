package de.ebuchner.vocab.fx.keyboard;

import de.ebuchner.vocab.fx.common.Point;
import de.ebuchner.vocab.fx.common.Rectangle;

import java.util.HashMap;
import java.util.Map;

public class KeyShapeCache {

    private Map<String, Rectangle> keyShapes = new HashMap<>();

    private String keyOf(int row, int column) {
        return row + " " + column;
    }

    public Rectangle keyShapeOf(int row, int column) {
        return keyShapes.get(keyOf(row, column));
    }

    public void clear() {
        keyShapes.clear();
    }

    public void addKeyShape(int row, int column, Rectangle shape) {
        keyShapes.put(keyOf(row, column), shape);
    }

    public int rowFrom(Point point, int defaultValue) {
        int row = defaultValue;

        for (String key : keyShapes.keySet()) {
            Rectangle r = keyShapes.get(key);
            if (r.contains(point))
                row = Integer.valueOf(key.split(" ")[0]);
        }
        return row;
    }

    public int columnFrom(Point point, int defaultValue) {
        int column = defaultValue;

        for (String key : keyShapes.keySet()) {
            Rectangle r = keyShapes.get(key);
            if (r.contains(point))
                column = Integer.valueOf(key.split(" ")[1]);
        }
        return column;
    }

}
