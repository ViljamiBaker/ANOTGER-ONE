package reactor;

public class Point3 {
    double x;
    double y;
    double z;
    public Point3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3 add(Point3 other){
        return new Point3(x+other.x, y+other.y, z+other.z);
    }

    public Point3 mult(double other){
        return new Point3(x*other, y*other, z*other);
    }
}
