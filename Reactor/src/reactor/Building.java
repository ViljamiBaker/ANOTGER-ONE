package reactor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Building {

    Unit[][] reactor;

    ReactorRenderer rr;

    ArrayList<Neut> neuts = new ArrayList<>();

    UnitTemplate[] uts = {
        new UnitTemplate("F", "U", 0.02, 1, 100, Color.GREEN)
    };

    int xsize;
    int ysize;

    public Building(String[][] template){
        this.xsize = template.length;
        this.ysize = template[0].length;
        reactor = new Unit[xsize][ysize];
        UnitCode.building = this;
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                for (int i = 0; i < uts.length; i++) {
                    if(uts[i].subtype().equals(template[x][i])){
                        reactor[x][y] = new Unit(uts[i], i, x, y);
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
        rr.infoToDraw = new String[]{
            String.valueOf(neuts.size()),
        };
        rr.paint(rr.g);
    }

    public void updateNeut(Neut n){
        n.x+=(int)n.xv;
        n.y+=(int)n.yv;
        n.lifetime--;
        if(n.lifetime<=0)neuts.remove(n);
    }

    public Unit getUnitAt(int x, int y){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return null;
        }
        return reactor[x][y];
    }

    public int getNeutCountAt(int x, int y){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return 0;
        }
        int count = 0;
        for (int x2 = 0; x2 < xsize; x2++) {
            for (int y2 = 0; y2 < ysize; y2++) {
                for (int i = 0; i < neuts.size(); i++) {
                    if(x==(int)neuts.get(i).x&&y==(int)neuts.get(i).y){
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public void spawnNeut(int x, int y, double xv, double yv, int lifetime){
        neuts.add(new Neut(x, y, xv, yv, lifetime));
    }

    public static void main(String[] args) {
        Building b = new Building(new String[][]
        {
            {"U","U","U","U","U","U","U","U","U","U","U","U"},
            {"U","U","U","U","U","U","U","U","U","U","U","U"},
            {"U","U","U","U","U","U","U","U","U","U","U","U"},
            {"U","U","U","U","U","U","U","U","U","U","U","U"},
        }
        );
        while (true) {
            long t = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            b.frame();
            System.out.println(b.neuts.size());
            long t2 = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            try {Thread.sleep((int)Math.max(16-(t2-t),0));}catch(InterruptedException e){}
        }
    }
}
