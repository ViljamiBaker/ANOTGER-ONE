package reactor;

public class UnitCode {
    public static Building building;
    public static void runCode(Square s){
        Neut[] neuts = building.getNeutCountAt(s.x, s.y, s.z);
        switch (s.u.type) {
            case "F"://fissile {randomNeutChance, neutspeed, neutlifetime, neutCollSpawnChance, neutSpeedExponent}
                if(Math.random()<=s.u.temp[0]){
                    spawnNeut(s);
                }
                for (Neut n : neuts) {
                    if(Math.random()>=1.0-s.u.temp[3]/Math.pow(n.speed,s.u.temp[4])){
                        for (int i = 0; i < s.u.temp[6]; i++) {
                            spawnNeut(s);
                        }
                    }
                }
                break;
            case "R"://Reflector {neutCollChance}
                for (Neut n : neuts) {
                    if(Math.random()<s.u.temp[0]){
                        Neut n2 = new Neut(n);
                        int face = n2.rayAABBIntersection(s);
                        switch (face) {
                            case 0:
                                n2.xd = Math.abs(n2.xd);
                                n2.x = s.x + 0.6;
                                n2.y = s.y;
                                n2.z = s.z;
                                break;
                            case 1:
                                n2.xd = -Math.abs(n2.xd);
                                n2.x = s.x - 0.6;
                                n2.y = s.y;
                                n2.z = s.z;
                                break;
                            case 2:
                                n2.yd = Math.abs(n2.yd);
                                n2.x = s.x;
                                n2.y = s.y + 0.6;
                                n2.z = s.z;
                                break;
                            case 3:
                                n2.yd = -Math.abs(n2.yd);
                                n2.x = s.x;
                                n2.y = s.y - 0.6;
                                n2.z = s.z;
                                break;
                            case 4:
                                n2.zd = Math.abs(n2.zd);
                                n2.x = s.x;
                                n2.y = s.y;
                                n2.z = s.z + 0.6;  
                                break;
                            case 5:
                                n2.zd = -Math.abs(n2.zd);
                                n2.x = s.x;
                                n2.y = s.y;
                                n2.z = s.z - 0.6;
                                break;
                        }
                        System.out.println(s.x);
                        System.out.println(n2.x);
                        building.neutsToAdd.add(n2);
                    }
                }
                break;
            case "M"://Moderator {neutCollChance, desiredSpeed, changeSpeed}
                for (Neut n : neuts) {
                    if(Math.random()<s.u.temp[0]){
                        double speed = n.speed;
                        double diff = s.u.temp[1]-speed;
                        if(Math.signum(diff)>0) continue;
                        if(Math.abs(diff)<=s.u.temp[2]){
                            Neut newNeut = new Neut(n);
                            newNeut.speed = s.u.temp[1];
                            newNeut.x = s.x;
                            newNeut.y = s.y;
                            newNeut.z = s.z;
                            building.neutsToAdd.add(newNeut);
                        }else{
                            double deltaSpeed = s.u.temp[2] * Math.signum(diff);
                            Neut newNeut = new Neut(n);
                            newNeut.speed = speed+deltaSpeed;
                            newNeut.x = s.x;
                            newNeut.y = s.y;
                            newNeut.z = s.z;
                            building.neutsToAdd.add(newNeut);
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
    //https://math.stackexchange.com/questions/44689/how-to-find-a-random-axis-or-unit-vector-in-3d
    public static void spawnNeut(Square s){
        s.nextSquare.temperature+= s.u.temp[5];
        double dir = Math.random()*Math.PI*2;
        double z = Math.random()*2-1;
        double z2 = Math.sqrt(1-z*z);
        building.spawnNeut(s.x, s.y, s.z, (z2*Math.sin(dir)),(z2*Math.cos(dir)), z, s.u.temp[1], (int)s.u.temp[2]);
    }
}
