package reactor;

public class KeyValueList {
    private String[] keys;
    private double[] values;

    public KeyValueList(String[] keys, double[] values){
        if(keys.length != values.length){
            throw new IllegalArgumentException("Keys length not equal to values length " + keys.length + "!=" + values.length);
        }
        this.keys = keys;
        this.values = values;
    }

    public double get(String key){
        for (int i = 0; i < keys.length; i++) {
            if(keys[i].equals(key)){
                return values[i];
            }
        }
        System.out.println("Key " + key + " has no value!");
        return -1;
    }

    public void set(String key, double value){
        for (int i = 0; i < keys.length; i++) {
            if(keys[i].equals(key)){
                values[i] = value;
            }
        }
        System.out.println("Key " + key + " has no value!");
    }

    public void add(String key, double value){
        for (int i = 0; i < keys.length; i++) {
            if(keys[i].equals(key)){
                values[i] += value;
            }
        }
        System.out.println("Key " + key + " has no value!");
    }

    public double get(int index){
        return values[index];
    }

    public int getSize(){
        return keys.length;
    }
}
