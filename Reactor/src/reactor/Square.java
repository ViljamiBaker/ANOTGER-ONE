package reactor;

public class Square {
    Unit u;
    int x;
    int y;
    int z;
    double temperature;

    Square nextSquare;

    public Square(Unit u, int x, int y, int z){
        this.temperature = 0;
        this.u = u;
        this.x = x;
        this.y = y;
        this.z = z;
        nextSquare = new Square(this);
    }

    private Square(Square s){
        this.temperature = s.temperature;
        this.u = new Unit(s.u);
        this.x = s.x;
        this.y = s.y;
        this.z = s.z;
        nextSquare = null;
    }

    public void update(){
        this.temperature = nextSquare.temperature;
        this.u = new Unit(nextSquare.u);
    }

    @Override
    public String toString(){
        return "x: " + x + " y: " + y + " z: " + z + " u.type:" + u.type + " u.subtype:" + u.subtype;
    }
}
