package model;

/**
 *
 * @author Robin
 */
public class Monitor extends Product {
    
    private int screenDiagonal;
    private int resolutionX;
    private int resolutionY;
    
    public Monitor(Long id, String name, int screenDiagonal, 
            int resolutionX, int resolutionY) {
        super(id, name);
        this.screenDiagonal = screenDiagonal;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
    }
}
