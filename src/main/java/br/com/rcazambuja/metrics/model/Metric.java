package br.com.rcazambuja.metrics.model;

public class Metric {
    private String name;
    private Object value;
    
    public static final Metric of(String name, Object value) {
        return new Metric(name, value);
    }
    
    private Metric(String name, Object value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
    
    @Override
    public String toString() {        
        return this.getName();
    }
}
