package reactor;

public class UnitCode {
    public static Building building;
    public static void runCode(Unit u){
        switch (u.type) {
            case "F"://fissile
                if(Math.random()<=u.temp[0]){
                    spawnNeut(u);
                }
                for (int i = 0; i < building.getNeutCountAt(u.x, u.y).length; i++) {
                    if(Math.random()<=u.temp[3]){
                        spawnNeut(u);
                    }
                }
                break;
            case "R"://Reflector
                Neut[] neuts = building.getNeutCountAt(u.x, u.y);
                for (Neut neut : neuts) {
                    if(Math.random()<u.temp[0]){
                        if(Math.abs(neut.xv)>Math.abs(neut.yv)){
                            neut.xv *= -1;
                        }else{
                            neut.yv *= -1;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
    
    public static void spawnNeut(Unit u){
        building.temperature[u.x][u.y] ++;
        double dir = Math.random()*Math.PI*2;
        building.spawnNeut(u.x, u.y, (Math.sin(dir)*u.temp[1]),(Math.cos(dir)*u.temp[1]),(int)u.temp[2]);
    }
}
