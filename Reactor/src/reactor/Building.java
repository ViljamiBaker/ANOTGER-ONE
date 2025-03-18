package reactor;

import java.awt.Color;
import java.util.ArrayList;

public class Building {

    Square[][][] reactor;

    ArrayList3D<Neut> neutCounts;

    ReactorRenderer rr;

    ArrayList<Neut> neuts = new ArrayList<>();
    ArrayList<Neut> neutsToRemove = new ArrayList<>();
    ArrayList<Neut> neutsToAdd = new ArrayList<>();

    int xsize;
    int ysize;
    int zsize;

    double money = 0;

    double rodOverride = 0.0;

    public Building(String[][][] template){
        this.xsize = template[0][0].length;
        this.ysize = template[0].length;
        this.zsize = template.length;
        reactor = new Square[xsize][ysize][zsize];
        neutCounts = new ArrayList3D<Neut>(xsize,ysize,zsize);
        UnitCode.building = this;
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                for (int z = 0; z < zsize; z++) {
                    for (int i = 0; i < uts.length; i++) {
                        if(uts[i].subtype().equals(template[z][y][x])){
                            reactor[x][y][z] = new Square(new Unit(uts[i]), x, y, z);
                        }
                    }
                }
            }
        }
        rr = new ReactorRenderer(this);
    }

    public void updateNeut(Neut n){
        for (double i = 0; i < n.lifetime; i++) {
            if((getSquareAt(n.origin.add(n.dir.mult(i))).u.global[0]==1)){
                Square s = getSquareAt(n.origin.add(n.dir.mult(i)));
                System.out.println(s);
                AABBIntersection intersection = n.rayAABBIntersection(s);
                if(intersection.happened()){
                    if(Math.random()<=s.u.global[3]){
                        neutCounts.add(s.x, s.y, s.z, n);
                        System.out.println(s);
                        break;
                    }
                };
            }
        }
        neutsToRemove.add(n);
    }

    public Square getSquareAt(int x, int y, int z){
        if(x<0||x>=xsize||y<0||y>=ysize||z<0||z>=zsize){
            return new Square(new Unit(uts[0]),-1,-1,-1);
        }
        return reactor[x][y][z];
    }

    public Square getSquareAt(Point3 p){
        return getSquareAt((int)p.x,(int)p.y,(int)p.z);
    }

    public Neut[] getNeutCountAt(int x, int y, int z){
        if(x<0||x>=xsize||y<0||y>=ysize||z<0||z>=zsize){
            return new Neut[0];
        }
        return neutCounts.get(x, y, z).toArray(new Neut[0]);
    }

    private void updateNeutCounts(){
        neutCounts.clear();
    }

    public void spawnNeut(int x, int y, int z, double xv, double yv, double zv, double speed, int lifetime){
        neutsToAdd.add(new Neut(new Point3(x+0.5, y+0.5, z+0.5), new Point3(xv, yv, zv), speed, lifetime));
    }

    int[][] dirs = {
        {1,0,-1},{1,1,-1},{0,1,-1},{-1,1,-1},{-1,0,-1},{-1,-1,-1},{0,-1,-1},{1,-1,-1},
        {1,0,0},{1,1,0},{0,1,0},{-1,1,0},{-1,0,0},{-1,-1,0},{0,-1,0},{1,-1,0},
        {1,0,1},{1,1,1},{0,1,1},{-1,1,1},{-1,0,1},{-1,-1,1},{0,-1,1},{1,-1,1}
    };
    private void updateTemperatures(){
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                for (int z = 0; z < zsize; z++) {
                    Square s = reactor[x][y][z];
                    double heatLost = s.temperature*s.u.global[1];
                    for (int[] dir : dirs) {
                        Square s2 = getSquareAt(x+dir[0],y+dir[1],z+dir[2]);
                        s2.nextSquare.temperature += heatLost*s2.u.global[2];
                        s.nextSquare.temperature -= heatLost*s2.u.global[2];
                    }
                    s.nextSquare.temperature *= s.u.global[2];
                }
            }
        }
    }

    public void frame(){
        for (int i = 0; i < neuts.size(); i++) {
            updateNeut(neuts.get(i));
        }
        double t = System.nanoTime()/1000000.0;
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                for (int z = 0; z < zsize; z++) {
                    if(reactor[x][y] == null){
                        System.out.println(x);
                        System.out.println(y);
                    }
                    UnitCode.runCode(reactor[x][y][z]);
                }
            }
        }
        updateTemperatures();
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                for (int z = 0; z < zsize; z++) {
                    reactor[x][y][z].update();
                }
            }
        }
        rr.strings.add(String.valueOf(money));
        rr.strings.add(String.valueOf(neuts.size()));
        rr.strings.add(String.valueOf((double)System.nanoTime()/1000000.0-t));
        rr.strings.add(String.valueOf(rodOverride));
        rr.paint(rr.g);
        updateNeutCounts();
        neuts.removeAll(neutsToRemove);
        neuts.addAll(neutsToAdd);
        neutsToRemove.clear();
        neutsToAdd.clear();
    }

    UnitTemplate[] uts = {
        new UnitTemplate("F", "U", new double[] {0.1, 0.4, 10, 0.1, 4, 20, 6}, Color.GREEN,new double[]{0,0.005,0.9999,0.2}),
        new UnitTemplate("F", "P", new double[] {0.05, 0.4, 10, 0.0, 4, 20, 3}, Color.MAGENTA,new double[]{0,0.005,0.9999,0.00205}),
        new UnitTemplate("R", "B", new double[] {1}, Color.GRAY,new double[]{1,0.005,0.9999,0.75}),
        new UnitTemplate("M", "W", new double[] {1,0.03,0.2}, Color.CYAN,new double[]{0,0.005,0.9999,0.3}),
        new UnitTemplate("C", "C", new double[] {0.5,0,1.0,4000,5000,0.001}, Color.ORANGE,new double[]{0,0.005,0.9999,0.1}),
        new UnitTemplate("C", "L", new double[] {0.5,0,1.0,150,150,0.001}, Color.YELLOW,new double[]{0,0.005,0.9999,0.1}),
        new UnitTemplate("S", "S", new double[] {0,0,10,5,1000,1000}, Color.PINK,new double[]{1,0.005,0.9999,0.0005}),
        new UnitTemplate("N", "A", new double[] {}, Color.WHITE,new double[]{0,1.0,0.0}),
    };

    public static void main(String[] args) {
        Building b = new Building(new String[][][]
        {
            {
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
            },
            {
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","U","C","W","C","U","C","W","C","U","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","W","C","U","C","W","C","U","C","W","B"},
                {"B","C","B","C","B","L","B","C","B","C","B"},
                {"B","U","C","W","L","P","L","W","C","U","B"},
                {"B","C","B","C","B","L","B","C","B","C","B"},
                {"B","W","C","U","C","W","C","U","C","W","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","U","C","W","C","U","C","W","C","U","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
            },
            {
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","B","C","B","C","B","C","B","C","B","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","B","C","B","C","B","C","B","C","B","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","B","C","B","C","B","C","B","C","B","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","B","C","B","C","B","C","B","C","B","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
            },
            {
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","W","C","U","C","W","C","U","C","W","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","U","C","W","C","U","C","W","C","U","B"},
                {"B","C","B","C","B","L","B","C","B","C","B"},
                {"B","W","C","U","L","P","L","U","C","W","B"},
                {"B","C","B","C","B","L","B","C","B","C","B"},
                {"B","U","C","W","C","U","C","W","C","U","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","W","C","U","C","W","C","U","C","W","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
            },
            {
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","B","C","B","C","B","C","B","C","B","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","B","C","B","C","B","C","B","C","B","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","B","C","B","C","B","C","B","C","B","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","B","C","B","C","B","C","B","C","B","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
            },
            {
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","U","C","W","C","U","C","W","C","U","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","W","C","U","C","W","C","U","C","W","B"},
                {"B","C","B","C","B","L","B","C","B","C","B"},
                {"B","U","C","W","L","P","L","W","C","U","B"},
                {"B","C","B","C","B","L","B","C","B","C","B"},
                {"B","W","C","U","C","W","C","U","C","W","B"},
                {"B","C","B","C","B","C","B","C","B","C","B"},
                {"B","U","C","W","C","U","C","W","C","U","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
            },
            {
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
                {"B","B","B","B","B","B","B","B","B","B","B"},
            }
        }
        );
        while (true) {
            double t = System.nanoTime()/1000000.0;
            b.frame();
            double t2 = System.nanoTime()/1000000.0;
            try {Thread.sleep((int)Math.max(16.0-(t2-t),0.0));}catch(InterruptedException e){}
        }
    }
}
