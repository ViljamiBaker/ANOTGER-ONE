package reactor;

import java.awt.Color;

public class Unit {
    String type;
    String subtype;
    double[] temp;
    Color color;
    double[] global;
    public Unit(UnitTemplate ut){
        this.type = ut.type();// what code to run
        this.subtype = ut.subtype();// what to display
        this.temp = ut.temp(); // variables for what code to run
        this.color = ut.color();// color
        this.global = ut.global();// {solid, tempLostPerFrame}
    }

    public Unit(Unit u){
        this.type = u.type;
        this.subtype = u.subtype;
        this.temp = u.temp;
        this.color = u.color;
        this.global = u.global;
    }
}
