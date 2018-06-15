package dal;

import java.util.Collection;
import model.Product;

/**
 *
 * @author Robin
 */
public interface IProductDao {
    void addProduct(Product product);
    Product getProduct(Long id);
    Collection<Product> getProducts();
    void removeProduct(Long id);
}
