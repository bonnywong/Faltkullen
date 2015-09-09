package faltkullen;

/**
 * Created by Anton on 2015-08-23.
 */
public class Vector2D {
    double x, y;

    public Vector2D(){
        x = 0;
        y = 0;
    }

    public Vector2D(double a, double b){
        x = a;
        y = b;
    }

    /*  Name : angle
        Input : Vector2D
        Returns : double
        Description:
            Returns the angle in radians between this Vector2D and the Vector2D in the input.
     */
    public double angle(Vector2D v){
        Vector2D diff = new Vector2D(v.x - x, v.y - y);
        diff.normalize();
        double a = Math.acos(diff.x);
        double b = Math.asin(diff.y);
        if(b < 0){
            a = Math.PI * 2 - a;
        }
        return a;
    }

    public void normalize(){
        double length = Math.sqrt(x*x + y*y);
        x = x / length;
        y = y / length;
    }
}
