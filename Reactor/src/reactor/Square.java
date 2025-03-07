package reactor;

public class Square {
    Unit u;
    int x;
    int y;
    double temperature;

    Square nextSquare;

    public Square(Unit u, int x, int y){
        this.temperature = 0;
        this.u = u;
        this.x = x;
        this.y = y;
        nextSquare = new Square(this);
    }

    private Square(Square s){
        this.temperature = s.temperature;
        this.u = new Unit(s.u);
        this.x = s.x;
        this.y = s.y;
        nextSquare = null;
    }

    public void update(){
        this.temperature = nextSquare.temperature;
        this.u = new Unit(nextSquare.u);
    }


}
