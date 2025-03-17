package reactor;

public class Neut {
    double x;
    double y;
    double z;
    double xd;
    double yd;
    double zd;
    double speed;
    int lifetime;
    public Neut(double x, double y, double z, double xd, double yd, double zd, double speed, int lifetime){
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

    public int rayAABBIntersection(Square s) {
        double tmin = -Double.MAX_VALUE; 
        double tmax = Double.MAX_VALUE;
        double[] boxpos = {s.x,s.y,s.z};
        double[] pos = {x,y,z};
        double[] dir = {xd,yd,zd};
        int face = -1;
        for (int d = 0; d < 3; ++d) {
            double t1 = (boxpos[d] - pos[d]) * dir[d];
            double t2 = (boxpos[d]+1 - pos[d]) * dir[d];
    
            tmin = Math.min(tmin, Math.min(t1, t2));
            tmax = Math.max(tmax, Math.max(t1, t2));
        }

        if((boxpos[0] - pos[0]) * dir[0] == tmin) face = 0;// x
        if((boxpos[0]+1 - pos[0]) * dir[0] == tmin) face = 1;// -x
        if((boxpos[0] - pos[0]) * dir[0] == tmin) face = 2;// y
        if((boxpos[0]+1 - pos[0]) * dir[0] == tmin) face = 3;// -y
        if((boxpos[0] - pos[0]) * dir[0] == tmin) face = 4;// z
        if((boxpos[0]+1 - pos[0]) * dir[0] == tmin) face = 5;// -z
        return face;
    }

    public Neut(Neut n){
        this.x = n.x;
        this.y = n.y;
        this.z = n.z;
        this.xd = n.xd;
        this.yd = n.yd;
        this.zd = n.zd;
        this.lifetime = n.lifetime;
    }
}
