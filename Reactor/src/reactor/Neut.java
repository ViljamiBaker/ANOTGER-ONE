package reactor;

public class Neut {
    double x;
    double y;
    double z;
    double xd;
    double yd;
    double zd;
    int lifetime;
    public Neut(double x, double y, double z, double xd, double yd, double zd, int lifetime){
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.lifetime = lifetime;
    }
    public boolean intersection(Square s) {
        double tmin = -Double.MAX_VALUE; 
        double tmax = Double.MAX_VALUE;
        double[] boxpos = {s.x,s.y,s.z};
        double[] pos = {x,y,z};
        double[] dir = {xd,yd,zd};
        for (int d = 0; d < 3; ++d) {
            double t1 = (boxpos[d] - pos[d]) * dir[d];
            double t2 = (boxpos[d]+1 - pos[d]) * dir[d];
    
            tmin = Math.min(tmin, Math.min(t1, t2));
            tmax = Math.max(tmax, Math.max(t1, t2));
        }
    
        return tmin < tmax;
    }
}
