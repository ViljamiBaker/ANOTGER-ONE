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
    /*
        if((boxpos[0] - pos[0]) * dirs[0] == tmin) face = 0;// x
        if((boxpos[0]+1 - pos[0]) * dirs[0] == tmin) face = 1;// -x
        if((boxpos[1] - pos[1]) * dirs[1] == tmin) face = 2;// y
        if((boxpos[1]+1 - pos[1]) * dirs[1] == tmin) face = 3;// -y
        if((boxpos[2] - pos[2]) * dirs[2] == tmin) face = 4;// z
        if((boxpos[2]+1 - pos[2]) * dirs[2] == tmin) face = 5;// -z
    */
    public AABBIntersection rayAABBIntersection(Square s) {
        if(ignoreList.contains(s)) return new AABBIntersection(false, 0, 0, 0);
        double tEnter = Double.NEGATIVE_INFINITY;
        double tExit = Double.POSITIVE_INFINITY;
        int face = -1;
        // Check intersection with each pair of slabs
        for (int i = 0; i < 3; i++) {
            double axisOrigin = (i == 0) ? origin.x : (i == 1) ? origin.y : origin.z;
            double axisDirection = (i == 0) ? dir.x : (i == 1) ? dir.y : dir.z;
            double aabbMin = (i == 0) ? s.x : (i == 1) ? s.y : s.z;
            double aabbMax = (i == 0) ? s.x + 1 : (i == 1) ? s.y + 1 : s.z + 1;
    
            if (axisDirection == 0) {
                // Ray is parallel to the slab, check if it's inside
                if (axisOrigin < aabbMin || axisOrigin > aabbMax) {
                    return new AABBIntersection(false, tEnter, tExit, -1);
                }
            } else {
                double t1 = (aabbMin - axisOrigin) / axisDirection;
                double t2 = (aabbMax - axisOrigin) / axisDirection;
                double temp2 = t1;
                if (t1 > t2) {
                    // Swap t1 and t2 to ensure t1 is the entry point
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }
                boolean swapped = t1 == temp2;
                tEnter = Math.max(tEnter, t1);
                tExit = Math.min(tExit, t2);
                face = (swapped ? (tEnter==t1 ? i: face):(tEnter==t1 ? i+3: face));
                if (tEnter > tExit) {
                    return new AABBIntersection(false, tEnter, tExit, -1); // No overlap, ray misses the AABB
                }
            }
        }
    
        return new AABBIntersection(tEnter >= 0, tEnter, tExit, face); // Ray intersects the AABB
    }

    public Neut(Neut n){
        this.origin = n.origin;
        this.dir = n.dir;
        this.lifetime = n.lifetime;
    }
}
