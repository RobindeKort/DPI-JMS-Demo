package model;

/**
 *
 * @author Robin
 */
public class Smartphone extends Product {
    
    private String operatingSystem;
    private int memory;
    private int storage;
    
    public Smartphone(Long id, String name, int price, String operatingSystem,
            int memory, int storage) {
        super(id, name, price);
        this.operatingSystem = operatingSystem;
        this.memory = memory;
        this.storage = storage;
    }
}
