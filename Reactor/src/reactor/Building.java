package reactor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Building {

    Unit[][] reactor;

    double[][] temperature;

    Neut[][][] neutCounts;

    ReactorRenderer rr;

    ArrayList<Neut> neuts = new ArrayList<>();
    ArrayList<Neut> neutsToRemove = new ArrayList<>();
    ArrayList<Neut> neutsToAdd = new ArrayList<>();

    UnitTemplate[] uts = {
        new UnitTemplate("F", "U", new double[] {0.05, 0.1, 1000, 0.05}, Color.GREEN,false),
        new UnitTemplate("R", "B", new double[] {1}, Color.GRAY,true),
        new UnitTemplate("M", "W", new double[] {0.5,0.01}, Color.CYAN,false),
        new UnitTemplate("C", "C", new double[] {0.5,0,1,500,600,0.05}, Color.ORANGE,false)
    };

    int xsize;
    int ysize;

    public Building(String[][] template){
        this.xsize = template.length;
        this.ysize = template[0].length;
        reactor = new Unit[xsize][ysize];
        temperature = new double[xsize][ysize];
        neutCounts = new Neut[xsize][ysize][0];
        UnitCode.building = this;
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                for (int i = 0; i < uts.length; i++) {
                    if(uts[i].subtype().equals(template[x][y].substring(0, 1))){
                        reactor[x][y] = new Unit(uts[i], Integer.parseInt(template[x][i].substring(1, 2)), x, y);
                    }
                }
            }
        }
        rr = new ReactorRenderer(this);
    }

    public void frame(){
        for (int x = 0; x < reactor.length; x++) {
            for (int y = 0; y < reactor[0].length; y++) {
                UnitCode.runCode(reactor[x][y]);
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
        rr.infoToDraw = new String[]{
            String.valueOf(neuts.size()),
        };
        rr.paint(rr.g);
    }

    public void updateNeut(Neut n){
        if(!getUnitAt((int)(n.x+n.xv), (int)(n.y+n.yv)).solid){
            n.x+=n.xv;
            n.y+=n.yv;
        }else{
            Unit u = getUnitAt((int)(n.x+n.xv), (int)(n.y+n.yv));
            if(Math.abs(n.x-u.x-0.5)>Math.abs(n.y-u.y-0.5)){
                if(n.x-u.x-0.5<0){
                    n.xv=Math.abs(n.xv)*-1;
                }else{
                    n.xv=Math.abs(n.xv)*1;
                }
            }else{
                if(n.y-u.y-0.5<0){
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

    public Unit getUnitAt(int x, int y){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return new Unit(uts[0], 0, 0, 0);
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

    public static void main(String[] args) {
        Building b = new Building(new String[][]
        {
            {"B0","B0","B0","B0","B0","B0","B0","B0","B0","B0","B0"},
            {"B0","U0","C0","W0","C0","U0","C0","W0","C0","U0","B0"},
            {"B0","C0","B0","C0","B0","C0","B0","C0","B0","C0","B0"},
            {"B0","U0","C0","W0","C0","U0","C0","W0","C0","U0","B0"},
            {"B0","C0","B0","C0","B0","C0","B0","C0","B0","C0","B0"},
            {"B0","U0","C0","W0","C0","U0","C0","W0","C0","U0","B0"},
            {"B0","B0","B0","B0","B0","B0","B0","B0","B0","B0","B0"},
            //{"B0","W0","W0","W0","B0","B0","B0","B0","B0","B0","B0"},
            //{"B0","W0","U0","W0","B0","B0","B0","B0","B0","B0","B0"},
            //{"B0","W0","W0","W0","B0","B0","B0","B0","B0","B0","B0"},
            //{"B0","B0","B0","B0","B0","B0","B0","B0","B0","B0","B0"},
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
