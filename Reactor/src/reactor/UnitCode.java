package reactor;

public class UnitCode {
    public static Building building;
    public static void runCode(Unit u){
        Neut[] neuts = building.getNeutCountAt(u.x, u.y);
        switch (u.type) {
            case "F"://fissile
                if(Math.random()<=u.temp[0]){
                    spawnNeut(u);
                }
                for (Neut n : neuts) {
                    if(Math.random()>=1.0-u.temp[3]/(n.xv+n.yv+1)){
                        spawnNeut(u);
                    }
                }
                break;
            case "R"://Reflector
                    //crazy
                break;
            case "M"://Moderator
                for (Neut n : neuts) {
                    if(Math.random()<u.temp[0]){
                        double speed = Math.sqrt(n.xv*n.xv+n.yv*n.yv);
                        n.xv/=speed;
                        n.yv/=speed;
                        n.xv*=u.temp[1];
                        n.yv*=u.temp[1];
                    }
                }
                break;
            case "C"://Control Rod
                if(building.neuts.size()<u.temp[3]){
                    u.temp[0]-=u.temp[5];
                    if(u.temp[0]<u.temp[1]){
                        u.temp[0]=u.temp[1];
                    }
                }else if(building.neuts.size()>u.temp[4]){
                    u.temp[0]+=u.temp[5];
                    if(u.temp[0]>u.temp[2]){
                        u.temp[0]=u.temp[2];
                    }
                }
                for (Neut n : neuts) {
                    if(Math.random()<u.temp[0]){
                        building.neutsToRemove.add(n);
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
