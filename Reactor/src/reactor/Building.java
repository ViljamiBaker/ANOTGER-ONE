package reactor;

import java.awt.Color;
import java.util.ArrayList;

public class Building {

    Square[][][] reactor;

    int[][][] neutCounts;

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
        neutCounts = new int[xsize][ysize][zsize];
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
            if((getSquareAt((int)(n.x+n.xd*i), (int)(n.y+n.yd*i), (int)(n.z+n.zd*i)).u.global[0]==1)){
                Square s = getSquareAt((int)(n.x+n.xd*i), (int)(n.y+n.yd*i), (int)(n.z+n.zd*i));
                boolean intersection = n.intersection(s);
                if(intersection){
                    if(Math.random()<=s.u.global[3]){
                        neutCounts[s.x][s.y][s.z]++;
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

    public int getNeutCountAt(int x, int y, int z){
        if(x<0||x>=xsize||y<0||y>=ysize||z<0||z>=zsize){
            return 0;
        }
        return neutCounts[x][y][z];
    }

    private void updateNeutCounts(){
        neutCounts = new int[xsize][ysize][zsize];
    }

    public void spawnNeut(int x, int y, int z, double xv, double yv, Double zv, int lifetime){
        neutsToAdd.add(new Neut(x+0.5, y+0.5, z+0.5, xv, yv, zv, lifetime));
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
                        if(s.x==7&&s.y==7&&s.z==1){
                        }
                        s2.nextSquare.temperature += heatLost*s2.u.global[2];
                        s.nextSquare.temperature -= heatLost*s2.u.global[2];
                    }
                    s.nextSquare.temperature *= s.u.global[2];
                }
            }
        }
    }

    public void frame(){
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
        for (int i = 0; i < neuts.size(); i++) {
            updateNeut(neuts.get(i));
        }
        neuts.removeAll(neutsToRemove);
        neuts.addAll(neutsToAdd);
        neutsToRemove.clear();
        neutsToAdd.clear();
        updateNeutCounts();
        rr.strings.add(String.valueOf(money));
        rr.strings.add(String.valueOf(neuts.size()));
        rr.strings.add(String.valueOf((double)System.nanoTime()/1000000.0-t));
        rr.strings.add(String.valueOf(rodOverride));
        rr.paint(rr.g);
    }

    UnitTemplate[] uts = {
        new UnitTemplate("F", "U", new double[] {0.0005, 0.4, 1000, 0.1, 4, 20, 3}, Color.GREEN,new double[]{0,0.005,0.9999,0.0005,0.0}),
        new UnitTemplate("F", "P", new double[] {0.05, 0.4, 10000, 0.0, 4, 20, 3}, Color.MAGENTA,new double[]{0,0.005,0.9999,0.0005,0.0}),
        new UnitTemplate("R", "B", new double[] {1}, Color.GRAY,new double[]{1,0.005,0.9999,0.75,1.0}),
        new UnitTemplate("M", "W", new double[] {0.5,0.03,0.02}, Color.CYAN,new double[]{0,0.005,0.9999,0.3,0.0}),
        new UnitTemplate("C", "C", new double[] {0.5,0,0.25,4000,5000,0.001}, Color.ORANGE,new double[]{0,0.005,0.9999,0.1,0.2}),
        new UnitTemplate("C", "L", new double[] {0.5,0,1.0,150,150,0.001}, Color.YELLOW,new double[]{0,0.005,0.9999,0.1,0.2}),
        new UnitTemplate("S", "S", new double[] {0,0,10,5,1000,1000}, Color.PINK,new double[]{1,0.005,0.9999,0.0005,0.0}),
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
