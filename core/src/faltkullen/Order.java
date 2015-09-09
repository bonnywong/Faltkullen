package faltkullen;

public class Order {

    double xTarget;
    double yTarget;
    int state;//ska innehålla en målposition

    //ska innehålla 1/3 olika aktivitetsgrader
    public Order(int a, int b, int c) {
        state = a;
        xTarget = b;
        yTarget = c;
    }
}
