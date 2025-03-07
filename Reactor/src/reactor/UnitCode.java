package reactor;

public class UnitCode {
    public static Building building;
    public static void runCode(Square s){
        Neut[] neuts = building.getNeutCountAt(s.x, s.y);
        switch (s.u.type) {
            case "F"://fissile {randomNeutChance, neutspeed, neutlifetime, neutCollSpawnChance, neutSpeedExponent}
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
            case "R"://Reflector {damn}
                    //crazy
                break;
            case "M"://Moderator {neutCollChance, desiredSpeed, changeSpeed}
                for (Neut n : neuts) {
                    if(Math.random()<s.u.temp[0]){
                        double speed = Math.sqrt(n.xv*n.xv+n.yv*n.yv);
                        double diff = s.u.temp[1]-speed;
                        if(Math.signum(diff)>0) continue;
                        if(Math.abs(diff)<=s.u.temp[2]){
                            n.xv/=speed;
                            n.yv/=speed;
                            n.xv*=s.u.temp[1];
                            n.yv*=s.u.temp[1];
                        }else{
                            double deltaSpeed = s.u.temp[2] * Math.signum(diff);
                            n.xv/=speed;
                            n.yv/=speed;
                            n.xv*=speed+deltaSpeed;
                            n.yv*=speed+deltaSpeed;
                        }
                    }
                }
                break;
            case "C"://Control Rod {neutCollChance/Insertion, minInsertion, maxInsertion, minNeuts, maxNeuts, speed}
                double desired = 0;
                if(building.rodOverride>0.0){
                    desired = UtilVB.slopedLine(s.u.temp[1], 0, s.u.temp[2], 1, building.rodOverride);
                    
                }else{
                    desired = UtilVB.slopedLine(s.u.temp[1], s.u.temp[3], s.u.temp[2], s.u.temp[4], building.neuts.size());
                }
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
            case "S"://Steam column {water, steam, waterGainRate, steamSellRate, maxWater, maxSteam}
                s.u.temp[0]+=Math.min(s.u.temp[2],s.u.temp[4]-s.u.temp[0]);
                if(s.temperature>=100.0){
                    double deltaWater = Math.min(Math.min(Math.min(s.u.temp[0],s.u.temp[5]-s.u.temp[1]),s.temperature-100.0), s.u.temp[2]);
                    s.u.temp[0]-=deltaWater;
                    s.u.temp[1]+=deltaWater;
                    s.temperature-=deltaWater;
                    double deltaSteam = Math.min(s.u.temp[1], s.u.temp[3]);
                    s.u.temp[1]-=deltaSteam;
                    building.money += deltaSteam;
                }else{
                    if(s.u.temp[1]<0.0001) break;
                    double deltaSteam = Math.min(100.0-s.temperature,s.u.temp[1]);
                    s.u.temp[1]-=deltaSteam;
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
