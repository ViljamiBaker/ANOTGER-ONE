package reactor;

public class UnitCode {
    public static Building building;
    public static void runCode(Square s){
        Neut[] neuts = building.getNeutCountAt(s.x, s.y);
        switch (s.u.type) {
            case "F"://fissile
                if(Math.random()<=s.u.temp[0]){
                    spawnNeut(s);
                }
                for (Neut n : neuts) {
                    if(Math.random()>=1.0-s.u.temp[3]/Math.pow(Math.pow(n.xv+1,2)+Math.pow(n.yv+1,2),s.u.temp[4])){
                        building.neutsToRemove.add(n);
                        for (int i = 0; i < s.u.temp[6]; i++) {
                            spawnNeut(s);
                        }
                    }
                }
                break;
            case "R"://Reflector
                    //crazy
                break;
            case "M"://Moderator
                for (Neut n : neuts) {
                    if(Math.random()<s.u.temp[0]){
                        double speed = Math.sqrt(n.xv*n.xv+n.yv*n.yv);
                        n.xv/=speed;
                        n.yv/=speed;
                        n.xv*=s.u.temp[1];
                        n.yv*=s.u.temp[1];
                    }
                }
                break;
            case "C"://Control Rod
                double desired = UtilVB.slopedLine(s.u.temp[1], s.u.temp[3], s.u.temp[2], s.u.temp[4], building.neuts.size());
                if(desired<s.u.temp[0]){
                    s.u.temp[0]-=s.u.temp[5];
                }else if(desired>s.u.temp[0]){
                    s.u.temp[0]+=s.u.temp[5];
                }
                for (Neut n : neuts) {
                    if(Math.random()<s.u.temp[0]){
                        building.neutsToRemove.add(n);
                    }
                }
                break;
            default:
                break;
        }
    }
    
    public static void spawnNeut(Square s){
        s.nextSquare.temperature+= s.u.temp[5];
        double dir = Math.random()*Math.PI*2;
        building.spawnNeut(s.x, s.y, (Math.sin(dir)*s.u.temp[1]),(Math.cos(dir)*s.u.temp[1]),(int)s.u.temp[2]);
    }
}
