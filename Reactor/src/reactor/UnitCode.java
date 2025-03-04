package reactor;

public class UnitCode {
    public static Building building;
    public static void runCode(Unit u){
        switch (u.type) {
            case "F"://fissile
                if(Math.random()<=u.temp1){
                    double dir = Math.random()*Math.PI*2;
                    building.spawnNeut(u.x, u.y, (Math.sin(dir)*u.temp2),(Math.cos(dir)*u.temp2),(int)u.temp3);
                }
                for (int i = 0; i < building.getNeutCountAt(u.x, u.y); i++) {
                    if(Math.random()<=u.temp1){
                        double dir = Math.random()*Math.PI*2;
                        building.spawnNeut(u.x, u.y, (Math.sin(dir)*u.temp2),(Math.cos(dir)*u.temp2),(int)u.temp3);
                    }
                }
                break;
        
            default:
                break;
        }
    }
}
