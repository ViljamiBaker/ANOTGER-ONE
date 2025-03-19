package reactor;

public record AABBIntersection(boolean happened, double tmin, double tmax, int face) {
    @Override
    public String toString(){
        return "happened: " + happened() + " tmin: " + tmin() + " tmax: " + tmax + " face: " + face;
    }
}
