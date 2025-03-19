package reactor;

public class UnitCode {
    public static Building building;
    public static void runCode(Square s){
        Neut[] neuts = building.getNeutCountAt(s.x, s.y, s.z);
        switch (s.u.type) {
            case "F"://fissile {randomNeutChance, neutspeed, neutlifetime, neutCollSpawnChance, neutSpeedExponent, fissionHeat, neutSpawnCount}
                if(Math.random()<=s.u.temp.get("randomNeutChance")){
                    spawnNeut(s);
                }
                for (Neut n : neuts) {
                    if(Math.random()>=1.0-s.u.temp.get("neutCollSpawnChance")/Math.pow(n.speed,s.u.temp.get("neutSpeedExponent"))){
                        for (int i = 0; i < s.u.temp.get("neutSpawnCount"); i++) {
                            spawnNeut(s);
                        }
                    }
                }
                break;
            case "R"://Reflector {neutCollChance}
                for (Neut n : neuts) {
                    Neut n2 = null;
                    AABBIntersection aabbint = n.rayAABBIntersection(s);
                    Point3 newpos = n.origin.add(n.dir.mult((n.lastAABBIntersection.tmin())));
                    switch (aabbint.face()) {
                        case 0:
                            n2 = new Neut(newpos.add(new Point3(+ 0.01, 0, 0)), new Point3(-n.dir.x, n.dir.y, n.dir.z), n.speed, n.lifetime);
                            break;
                        case 1:
                            n2 = new Neut(newpos.add(new Point3(- 0.01, 0, 0)), new Point3(-n.dir.x, n.dir.y, n.dir.z), n.speed, n.lifetime);
                            break;
                        case 2:
                            n2 = new Neut(newpos.add(new Point3(0, + 0.01, 0)),new Point3( n.dir.x, -n.dir.y, n.dir.z), n.speed, n.lifetime);
                            break;
                        case 3:
                            n2 = new Neut(newpos.add(new Point3(0, - 0.01, 0)), new Point3(n.dir.x, -n.dir.y, n.dir.z), n.speed, n.lifetime);
                            break;
                        case 4:
                            n2 = new Neut(newpos.add(new Point3(0, 0, + 0.01)), new Point3(n.dir.x, n.dir.y, -n.dir.z), n.speed, n.lifetime);
                            break;
                        case 5:
                            n2 = new Neut(newpos.add(new Point3(0, 0, - 0.01)), new Point3(n.dir.x, n.dir.y, -n.dir.z), n.speed, n.lifetime);
                            break;
                    }
                    building.neutsToAdd.add(n2);
                }
                break;
            case "M"://Moderator {desiredSpeed, changeSpeed}
                for (Neut n : neuts) {
                    double speed = n.speed;
                    double diff = s.u.temp.get("desiredSpeed")-speed;
                    Point3 newpos = n.origin.add(n.dir.mult((n.lastAABBIntersection.tmax())));
                    Neut newNeut = new Neut(n);
                    if(Math.signum(diff)>0) continue;
                    if(Math.abs(diff)<=s.u.temp.get("changeSpeed")){
                        newNeut.speed = s.u.temp.get("desiredSpeed");
                        newNeut.origin = newpos;
                    }else{
                        double deltaSpeed = s.u.temp.get("changeSpeed") * Math.signum(diff);
                        newNeut.speed = speed+deltaSpeed;
                        newNeut.origin = newpos;
                    }
                    building.neutsToAdd.add(newNeut);
                }
                break;
            case "C"://Control Rod {minInsertion, maxInsertion, minNeuts, maxNeuts, speed}
                double desired = 0;
                if(building.rodOverride>0.0){
                    desired = UtilVB.slopedLine(s.u.temp.get("minInsertion"), 0, s.u.temp.get("maxInsertion"), 1, building.rodOverride);
                    
                }else{
                    desired = UtilVB.slopedLine(s.u.temp.get("minInsertion"), s.u.temp.get("minNeuts"), s.u.temp.get("maxInsertion"), s.u.temp.get("maxNeuts"), building.neuts.size());
                }
                if(desired<s.u.global.get("NeutCollChance")){
                    s.u.global.add("NeutCollChance", -s.u.temp.get("speed"));
                }else if(desired>s.u.global.get("NeutCollChance")){
                    s.u.global.add("NeutCollChance", +s.u.temp.get("speed"));
                }
                break;
            case "S"://Steam column {water, steam, waterGainRate, steamSellRate, maxWater, maxSteam}
                s.u.temp.add("water", Math.min(s.u.temp.get("waterGainRate"),s.u.temp.get("maxWater")-s.u.temp.get("water")));
                if(s.temperature>=100.0){
                    double deltaWater = Math.min(Math.min(Math.min(s.u.temp.get("water"),s.u.temp.get("maxSteam")-s.u.temp.get("steam")),s.temperature-100.0), s.u.temp.get("waterGainRate"));
                    s.u.temp.add("water", -deltaWater);
                    s.u.temp.add("steam", deltaWater);
                    s.temperature-=deltaWater;
                    double deltaSteam = Math.min(s.u.temp.get("steam"), s.u.temp.get("steamSellRate"));
                    s.u.temp.add("steam", -deltaSteam);
                    building.money += deltaSteam;
                }else{
                    if(s.u.temp.get("steam")<0.0001) break;
                    double deltaSteam = Math.min(100.0-s.temperature,s.u.temp.get("steam"));
                    s.u.temp.add("steam", -deltaSteam);
                }
                break;
            default:
                break;
        }
    }
    //https://math.stackexchange.com/questions/44689/how-to-find-a-random-axis-or-unit-vector-in-3d
    public static void spawnNeut(Square s){
        s.nextSquare.temperature+= s.u.temp.get("fissionHeat");
        double dir = Math.random()*Math.PI*2;
        double z = 0;//Math.random()*2-1;
        double z2 = 1;//Math.sqrt(1-z*z);
        building.spawnNeut(s.x, s.y, s.z, (z2*Math.sin(dir)),(z2*Math.cos(dir)), z, s.u.temp.get("neutspeed"), (int)s.u.temp.get("neutlifetime"));
    }
}
