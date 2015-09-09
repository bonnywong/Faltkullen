package faltkullen;

import java.io.Serializable;

public class Position implements Serializable {
    public double x;
    public double y;

    public Position(double a, double b) {
        x = a;
        y = b;
    }

    public Position(Position p){
        x = p.x;
        y = p.y;
    }

    public Position normalize() {
        if (x != 0 && y != 0) {
            double xUnit = x / Math.sqrt((x * x) + (y * y));
            double yUnit = y / Math.sqrt((x * x) + (y * y));
            return new Position(xUnit, yUnit);
        } else if (x == 0 && y != 0) {
            y = 1;
        } else if (y == 0 && x != 0) {
            x = 1;
        } else {
            x = 0;
            y = 0;
        }
        return new Position(x, y);
    }

    public double length() {
        return Math.sqrt((x * x) + (y * y));
    }

    public Position minus(Position p) {
        double xSub = x - p.x;
        double ySub = y - p.y;
        return new Position(xSub, ySub);
    }

    public Position minus(double d) {
        double xSub = x - d;
        double ySub = y - d;
        return new Position(xSub, ySub);
    }

    public Position plus(Position p) {
        double xSub = x + p.x;
        double ySub = y + p.y;
        return new Position(xSub, ySub);
    }

    public Position plus(double d) {
        double xSub = x + d;
        double ySub = y + d;
        return new Position(xSub, ySub);
    }

    public void add(double d) {
        x = x + d;
        y = y + d;
    }

    public void add(Position p) {
        x = x + p.x;
        y = y + p.y;
    }

    public Position times(double d) {
        double xSub = (double) x * d;
        double ySub = (double) y * d;
        return new Position(xSub, ySub);
    }

    public Position getNormal() {
        return new Position(-y, x);
    }

    public double distance(Position p) {
        double distance = Math.sqrt(((x - p.x) * (x - p.x))
                + ((y - p.y) * (y - p.y)));
        return distance;
    }

    /*
     * Calculates the unit dotproduct, a double between 1 and -1, reflecting how
     * much the two vectors differ in direction. 1 is same direction, -1 is
     * opposite directions, 0 is orthogonal.
     */
    public double unitDotProduct(Position b) {
        Position an = new Position(x, y).normalize();
        Position bn = b.normalize();
        double dotProduct = (an.x * bn.x) + (an.y * bn.y);
        return dotProduct;
    }

    public void subtract(Position times) {

        x = x - times.x;
        y = y - times.y;

    }

    public String toString(){
        return x + "/" + y;
    }
}
