package model;

/**
 *
 * @author Robin
 */
public abstract class Product implements Comparable<Product> {
    
    private String type;
    private Long id;
    private String name;

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public Product(Long id, String name) {
        this.type = this.getClass().getSimpleName();
        this.id = id;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return String.format("[%1$s] %2$s", type, name);
    }
    
    @Override
    public int compareTo(Product other) {
        int ret = this.type.compareTo(other.type);
        if (ret == 0) {
            ret = this.name.compareTo(other.name);
        }
        return ret;
    }
}
