package dal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Product;

/**
 *
 * @author Robin
 */
public class ProductDaoMem implements IProductDao {

    private Map<Long, Product> products;

    public ProductDaoMem() {
        this(null);
    }

    public ProductDaoMem(Collection<Product> products) {
        this.products = new HashMap();
        if (products != null) {
            for (Product p : products) {
                addProduct(p);
            }
        }
    }

    @Override
    public void addProduct(Product product) {
        Long id = new Long(this.products.size());
        product.setId(id);
        products.putIfAbsent(id, product);
    }

    @Override
    public Product getProduct(Long id) {
        return products.get(id);
    }

    @Override
    public Collection<Product> getProducts() {
        return products.values();
    }

    @Override
    public void removeProduct(Long id) {
        products.remove(id);
    }
}
