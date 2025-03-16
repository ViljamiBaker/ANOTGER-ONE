package reactor;

import java.util.ArrayList;

public class ArrayList3D <C> {
    private Object[][][] list;

    private int xsize;
    private int ysize;
    private int zsize;

    public ArrayList3D(int xsize, int ysize, int zsize){
        this.xsize = xsize;
        this.ysize = ysize;
        this.zsize = zsize;
        list = new Object[xsize][ysize][zsize];
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                for (int z = 0; z < zsize; z++) {
                    list[x][y][z] = new ArrayList<C>();
                }
            }
        }
    }
    @SuppressWarnings("unchecked")
    public ArrayList<C>[][][] getList() {
        return (ArrayList<C>[][][])list;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<C> get(int x, int y, int z) {
        return (ArrayList<C>)list[x][y][z];
    }

    @SuppressWarnings("unchecked")
    public void add(int x, int y, int z , C item){
        ((ArrayList<C>)list[x][y][z]).add(item);
    }

    @SuppressWarnings("unchecked")
    public void remove(int x, int y, int z, C item){
        ((ArrayList<C>)list[x][y][z]).remove(item);
    }

    @SuppressWarnings("unchecked")
    public void clear(int x, int y, int z){
        ((ArrayList<C>)list[x][y][z]).clear();
    }

    public void clear(){
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                for (int z = 0; z < zsize; z++) {
                    list[x][y][z] = new ArrayList<C>();
                }
            }
        }
    }

    public int getXsize() {
        return xsize;
    }

    public int getYsize() {
        return ysize;
    }

    public int getZsize() {
        return zsize;
    }
}
