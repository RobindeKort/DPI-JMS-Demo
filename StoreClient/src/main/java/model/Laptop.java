package model;

/**
 *
 * @author Robin
 */
public class Laptop extends Product {
    
    private String brand;
    private String keyboardLayout;
    private boolean touchScreen;
    
    public Laptop(Long id, String name, int price, String brand, 
            String keyboardLayout, boolean touchScreen) {
        super(id, name, price);
        this.brand = brand;
        this.keyboardLayout = keyboardLayout;
        this.touchScreen = touchScreen;
    }
}
