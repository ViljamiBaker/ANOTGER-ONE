package reactor;

public class UtilVB {
    public static double slopedLine(double min, double mint, double max, double maxt, double t){
        if(t<=mint){
            return min;
        }
        if(t>=maxt){
            return max;
        }
        if(maxt-mint==0){
            return (t>maxt ? max : min);
        }
        return ((max-min)/(maxt-mint))*t-((max-min)/(maxt-mint))*mint+min;
    }
}
