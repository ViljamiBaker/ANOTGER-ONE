package reactor;

import java.util.ArrayList;

public class Neut {
    Point3 origin;
    Point3 dir;
    double speed;
    int lifetime;
    AABBIntersection lastAABBIntersection = new AABBIntersection(false, 1, 1, 1);
    ArrayList<Square> ignoreList = new ArrayList<>();
    public Neut(Point3 origin, Point3 dir, double speed, int lifetime){
        this.origin = origin;
        this.dir = dir;
        this.lifetime = lifetime;
    }

    public AABBIntersection rayAABBIntersection(Square s) {
        if(ignoreList.contains(s)) return new AABBIntersection(false, 0, 0, 0);
        double tmin = Double.MAX_VALUE; 
        double tmax = -Double.MAX_VALUE;
        double[] boxpos = {s.x,s.y,s.z};
        double[] pos = {origin.x,origin.y,origin.z};
        double[] dirs = {dir.x,dir.y,dir.z};
        int face = -1;
        for (int d = 0; d < 3; ++d) {
            double t1 = (boxpos[d] - pos[d]) * dirs[d];
            double t2 = (boxpos[d]+1 - pos[d]) * dirs[d];
    
            tmin = Math.min(tmin, Math.min(t1, t2));
            tmax = Math.max(tmax, Math.max(t1, t2));
        }
        if((boxpos[0] - pos[0]) * dirs[0] == tmin) face = 0;// x
        if((boxpos[0]+1 - pos[0]) * dirs[0] == tmin) face = 1;// -x
        if((boxpos[1] - pos[1]) * dirs[1] == tmin) face = 2;// y
        if((boxpos[1]+1 - pos[1]) * dirs[1] == tmin) face = 3;// -y
        if((boxpos[2] - pos[2]) * dirs[2] == tmin) face = 4;// z
        if((boxpos[2]+1 - pos[2]) * dirs[2] == tmin) face = 5;// -z
        AABBIntersection aabbIntersection = new AABBIntersection(tmin < tmax && tmin > 0.0, tmin, tmax, face);
        lastAABBIntersection = aabbIntersection;
        return aabbIntersection;
    }

    public Neut(Neut n){
        this.origin = n.origin;
        this.dir = n.dir;
        this.lifetime = n.lifetime;
    }
}
