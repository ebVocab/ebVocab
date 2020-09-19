package de.ebuchner.vocab.fx.common;

public class Rectangle {
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public Rectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(Point point) {
        return x <= point.getX()
                && y <= point.getY()
                && x + width >= point.getX()
                && y + height >= point.getY();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
