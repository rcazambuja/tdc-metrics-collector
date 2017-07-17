package br.com.rcazambuja.metrics.model;

public class Property {
    private String name;
    private String value;
    
    public static final Property of(String name, String value) {
        return new Property(name, value);
    }
    
    private Property(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {        
        return this.getName();
    }
}
