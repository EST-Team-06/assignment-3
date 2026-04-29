import java.util.List;

public class InventoryDatabaseConnector {
    private InventoryDatabase database;

    /**
     * Initializes class with database connection
     * @param db the database variable passed to this implementation
     */
    public InventoryDatabaseConnector(InventoryDatabase db) {
        this.database = db;
    }

    /**
     * Gets all products from the database
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        // This would fetch all products in the inventory
        // Skipping implementation
        return null;
    }
    
    /**
     * Gets products by category
     * @param category the category to filter by
     * @return list of products in the specified category
     */
    public List<Product> getProductsByCategory(String category) {
        // This would fetch all products in a specific inventory category
        // Skipping implementation
        return null;
    }

    /**
     * Close the database connection
     */
    public void close() {
        this.database.close();
    }
}
