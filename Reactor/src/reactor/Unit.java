package reactor;

import java.awt.Color;

public class Unit {
    double temperature;
    int dir;
    String type;
    String subtype;
    double temp1;
    double temp2;
    double temp3;
    int x;
    int y;
    Color color;
    public Unit(UnitTemplate ut, int dir, int x, int y){
        this.type = ut.type();// what code to run
        this.subtype = ut.subtype();// what to display
        this.temp1 = ut.temp1();//  used for code to run
        this.temp2 = ut.temp2();//  used for code to run
        this.temp3 = ut.temp3();//  used for code to run
        this.dir = dir;//  used for code to run
        this.color = ut.color();
        this.x = x;// pos
        this.y = y;// pos
    }
}
