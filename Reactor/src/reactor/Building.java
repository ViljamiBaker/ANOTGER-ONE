package reactor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Building {

    Square[][] reactor;

    Neut[][][] neutCounts;

    ReactorRenderer rr;

    ArrayList<Neut> neuts = new ArrayList<>();
    ArrayList<Neut> neutsToRemove = new ArrayList<>();
    ArrayList<Neut> neutsToAdd = new ArrayList<>();

    int xsize;
    int ysize;

    double money = 0;

    double rodOverride = 0.0;

    public Building(String[][] template){
        this.xsize = template.length;
        this.ysize = template[0].length;
        reactor = new Square[xsize][ysize];
        neutCounts = new Neut[xsize][ysize][0];
        UnitCode.building = this;
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                for (int i = 0; i < uts.length; i++) {
                    if(uts[i].subtype().equals(template[x][y])){
                        reactor[x][y] = new Square(new Unit(uts[i]), x, y);
                    }
                }
            }
        }
        rr = new ReactorRenderer(this);
    }

    public void updateNeut(Neut n){
        if(!(getSquareAt((int)(n.x+n.xv), (int)(n.y+n.yv)).u.global[0]==1)){
            n.x+=n.xv;
            n.y+=n.yv;
        }else{
            Square s = getSquareAt((int)(n.x+n.xv), (int)(n.y+n.yv));
            if(Math.abs(n.x-s.x-0.5)>Math.abs(n.y-s.y-0.5)){
                if(n.x-s.x-0.5<0){
                    n.xv=Math.abs(n.xv)*-1;
                }else{
                    n.xv=Math.abs(n.xv)*1;
                }
            }else{
                if(n.y-s.y-0.5<0){
                    n.yv=Math.abs(n.yv)*-1;
                }else{
                    n.yv=Math.abs(n.yv)*1;
                }
            }
        }
        n.lifetime--;
        if(n.lifetime<=0){
            neutsToRemove.add(n);
        }
    }

    public Square getSquareAt(int x, int y){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return new Square(new Unit(uts[0]),0,0);
        }
        return reactor[x][y];
    }

    public Neut[] getNeutCountAt(int x, int y){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return new Neut[0];
        }
        return neutCounts[x][y];
    }

    private void updateNeutCounts(){
        neutCounts = new Neut[xsize][ysize][0];
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                ArrayList<Neut> neuts2 = new ArrayList<>();
                for (int i = 0; i < neuts.size(); i++) {
                    if((int)neuts.get(i).x<0||(int)neuts.get(i).x>=xsize||(int)neuts.get(i).y<0||(int)neuts.get(i).y>=ysize){
                        continue;
                    }
                    if((int)neuts.get(i).x==x&&(int)neuts.get(i).y==y){
                        neuts2.add(neuts.get(i));
                    }
                }
                neutCounts[x][y] = neuts2.toArray(new Neut[0]);
            }
        }

    }

    public void spawnNeut(int x, int y, double xv, double yv, int lifetime){
        neutsToAdd.add(new Neut(x+0.5, y+0.5, xv, yv, lifetime));
    }

    int[][] dirs = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1},};
    private void updateTemperatures(){
        for (int x = 0; x < reactor.length; x++) {
            for (int y = 0; y < reactor[0].length; y++) {
                Square s = reactor[x][y];
                double heatLost = s.temperature*s.u.global[1];
                for (int[] dir : dirs) {
                    Square s2 = getSquareAt(x+dir[0],y+dir[1]);
                    s2.nextSquare.temperature += heatLost*s2.u.global[2];
                    s.nextSquare.temperature -= heatLost*s2.u.global[2];
                }
                s.nextSquare.temperature *= s.u.global[2];
            }
        }
    }

    public void frame(){
        double t = System.nanoTime()/1000000.0;
        for (int x = 0; x < reactor.length; x++) {
            for (int y = 0; y < reactor[0].length; y++) {
                if(reactor[x][y] == null){
                    System.out.println(x);
                    System.out.println(y);
                }
                UnitCode.runCode(reactor[x][y]);
            }
        }
        updateTemperatures();
        for (int x = 0; x < reactor.length; x++) {
            for (int y = 0; y < reactor[0].length; y++) {
                reactor[x][y].update();;
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
        new UnitTemplate("F", "U", new double[] {0.0005, 0.1, 1000, 0.1, 4, 20, 3}, Color.GREEN,new double[]{0,0.005,0.9999}),
        new UnitTemplate("F", "P", new double[] {0.05, 0.25, 10000, 0.0, 4, 20, 3}, Color.MAGENTA,new double[]{0,0.005,0.9999}),
        new UnitTemplate("R", "B", new double[] {1}, Color.GRAY,new double[]{1,0.005,0.9999}),
        new UnitTemplate("M", "W", new double[] {0.5,0.03,0.02}, Color.CYAN,new double[]{0,0.005,0.9999}),
        new UnitTemplate("C", "C", new double[] {0.5,0,0.25,4000,5000,0.001}, Color.ORANGE,new double[]{0,0.005,0.9999}),
        new UnitTemplate("C", "L", new double[] {0.5,0,1.0,150,150,0.001}, Color.YELLOW,new double[]{0,0.005,0.9999}),
        new UnitTemplate("S", "S", new double[] {0,0,10,5,1000,1000}, Color.PINK,new double[]{1,0.005,0.9999}),
        new UnitTemplate("N", "A", new double[] {}, Color.WHITE,new double[]{0,1.0,0.0}),
    };

    public static void main(String[] args) {
        Building b = new Building(new String[][]
        {
            {"B","B","B","B","B","B","B","B","B","B","B","B","B","B","B","B","B","B","B"},
            {"B","U","C","W","C","U","C","W","C","U","C","W","C","U","C","W","C","U","B"},
            {"B","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","B"},
            {"B","W","C","U","C","W","C","U","C","W","C","U","C","W","C","U","C","W","B"},
            {"B","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","B"},
            {"B","U","C","W","C","U","C","W","C","U","C","W","C","U","C","W","C","U","B"},
            {"B","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","B"},
            {"B","W","C","U","C","W","C","U","C","W","C","U","C","W","C","U","C","W","B"},
            {"B","C","S","C","S","C","S","C","S","L","S","C","S","C","S","C","S","C","B"},
            {"B","U","C","W","C","U","C","W","L","P","L","W","C","U","C","W","C","U","B"},
            {"B","C","S","C","S","C","S","C","S","L","S","C","S","C","S","C","S","C","B"},
            {"B","W","C","U","C","W","C","U","C","W","C","U","C","W","C","U","C","W","B"},
            {"B","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","B"},
            {"B","U","C","W","C","U","C","W","C","U","C","W","C","U","C","W","C","U","B"},
            {"B","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","B"},
            {"B","W","C","U","C","W","C","U","C","W","C","U","C","W","C","U","C","W","B"},
            {"B","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","S","C","B"},
            {"B","U","C","W","C","U","C","W","C","U","C","W","C","U","C","W","C","U","B"},
            {"B","B","B","B","B","B","B","B","B","B","B","B","B","B","B","B","B","B","B"},
            /*{"B","B","B","B","B","B","B","B","B","B","B"},
            {"B","A","A","A","A","A","A","A","A","A","B"},
            {"B","A","A","A","A","A","A","A","A","A","B"},
            {"B","A","A","W","W","W","A","A","A","A","B"},
            {"B","P","A","W","W","W","A","A","A","A","B"},
            {"B","A","A","W","W","W","A","A","A","A","B"},
            {"B","A","A","A","A","A","A","A","A","A","B"},
            {"B","A","A","A","A","A","A","A","A","A","B"},
            {"B","A","A","A","A","A","A","A","A","A","B"},
            {"B","B","B","B","B","B","B","B","B","B","B"},*/
        }
        );
        while (true) {
            long t = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            b.frame();
            long t2 = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            try {Thread.sleep((int)Math.max(16-(t2-t),0));}catch(InterruptedException e){}
        }
    }
}
