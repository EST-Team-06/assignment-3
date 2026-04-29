import java.util.*;
import java.util.stream.*;

public class InventoryManager {
    private final InventoryDatabaseConnector databaseConnector;

    /**
     * Constructor that receives a database connector variable
     * @param databaseConnector the DB connector variable
     */
    public InventoryManager(InventoryDatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    /**
     * Gets products that are low in stock (quantity < 10)
     * @return list of low stock products
     */
    public List<Product> getLowStockProducts() {
        try {
            List<Product> allProducts = this.databaseConnector.getAllProducts();
            return allProducts.stream()
                    .filter(product -> product.getQuantity() < 10)
                    .collect(Collectors.toList());
        } finally {
            this.databaseConnector.close();
        }
    }
    
    /**
     * Gets products by category (TDD implementation required)
     * @param category the category to filter by
     * @return list of products in the specified category
     */
    public List<Product> getProductsByCategory(String category) {
        // TODO: Implement using TDD
        throw new UnsupportedOperationException("Not yet implemented - implement using TDD");
    }
}