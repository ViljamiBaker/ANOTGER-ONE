package reactor;

import java.awt.Color;

public class Unit {
    int dir;
    String type;
    String subtype;
    double[] temp;
    int x;
    int y;
    Color color;
    public Unit(UnitTemplate ut, int dir, int x, int y){
        this.type = ut.type();// what code to run
        this.subtype = ut.subtype();// what to display
        this.temp = ut.temp(); // variables for what code to run
        this.dir = dir;//  used for code to run
        this.color = ut.color();
        this.x = x;// pos
        this.y = y;// pos
    }
}
