package de.ebuchner.vocab.fx.common;

import de.ebuchner.toolbox.lang.Equals;
import de.ebuchner.toolbox.lang.HashCode;

public class Point {
    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public int hashCode() {
        HashCode hc = new HashCode(this);
        hc.addDouble(x);
        hc.addDouble(y);
        return hc.getResult();
    }

    @Override
    public boolean equals(Object obj) {
        Equals equals = new Equals(this);
        return equals.compareWith(obj);
    }
}
