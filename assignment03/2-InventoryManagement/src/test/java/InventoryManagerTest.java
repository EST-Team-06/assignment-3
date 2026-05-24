import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryManagerTest {

    private InventoryDatabaseConnector databaseConnector;
    private InventoryManager inventoryManager;

    @BeforeEach
    void setUp() {
        databaseConnector = mock(InventoryDatabaseConnector.class);
        inventoryManager = new InventoryManager(databaseConnector);
    }

    @Test
    void getLowStockProducts_onPoint_returnsProduct() {
        Product product = new Product("1", "El Tony Mate Mate & Mint", "Beverage", 9, 1.90);
        when(databaseConnector.getAllProducts()).thenReturn(List.of(product));

        List<Product> result = inventoryManager.getLowStockProducts();

        assertEquals(List.of(product), result);
    }

    @Test
    void getLowStockProducts_offPoint_doesNotReturnProduct() {
        Product product = new Product("2", "El Tony Mate Mate & Ginger", "Beverage", 10, 1.90);
        when(databaseConnector.getAllProducts()).thenReturn(List.of(product));

        List<Product> result = inventoryManager.getLowStockProducts();

        assertTrue(result.isEmpty());
    }

    @Test
    void getLowStockProducts_multipleProducts_returnsOnlyLowStock() {
        Product lowStock = new Product("1", "El Tony Mate Mate & Mint", "Beverage", 9, 1.90);
        Product inStock  = new Product("2", "El Tony Mate Mate & Ginger", "Beverage", 10, 1.90);
        when(databaseConnector.getAllProducts()).thenReturn(List.of(lowStock, inStock));

        List<Product> result = inventoryManager.getLowStockProducts();

        assertEquals(List.of(lowStock), result);
    }

    @Test
    void getLowStockProducts_multipleProducts_allLowStock_returnsAll() {
        Product p1 = new Product("1", "El Tony Mate Mate & Mint", "Beverage", 3, 1.90);
        Product p2 = new Product("2", "El Tony Mate Mate & Ginger", "Beverage", 7, 1.90);
        when(databaseConnector.getAllProducts()).thenReturn(List.of(p1, p2));

        List<Product> result = inventoryManager.getLowStockProducts();

        assertEquals(List.of(p1, p2), result);
    }

    @Test
    void getLowStockProducts_emptyInventory_returnsEmptyList() {
        when(databaseConnector.getAllProducts()).thenReturn(List.of());

        List<Product> result = inventoryManager.getLowStockProducts();

        assertTrue(result.isEmpty());
    }

    @Test
    void getLowStockProducts_alwaysClosesConnection() {
        when(databaseConnector.getAllProducts()).thenReturn(List.of());

        inventoryManager.getLowStockProducts();

        verify(databaseConnector).close();
    }

    @Test
    void getProductsByCategory_singleProduct_returnsProduct() {
        Product product = new Product("1", "El Tony Mate Mate & Mint", "Beverage", 1, 1.90);
        when(databaseConnector.getProductsByCategory("Beverage")).thenReturn(List.of(product));

        List<Product> result = inventoryManager.getProductsByCategory("Beverage");

        assertEquals(List.of(product), result);
    }

    @Test
    void getProductsByCategory_singleProduct_doesNotReturnProduct() {
        when(databaseConnector.getProductsByCategory("Beverage")).thenReturn(List.of());

        List<Product> result = inventoryManager.getProductsByCategory("Beverage");

        assertEquals(List.of(), result);
    }

    @Test
    void getProductsByCategory_multipleProduct_returnsProduct() {
        Product p1 = new Product("1", "El Tony Mate Mate & Mint", "Beverage", 1, 1.90);
        when(databaseConnector.getProductsByCategory("Beverage")).thenReturn(List.of(p1));

        List<Product> result = inventoryManager.getProductsByCategory("Beverage");

        assertEquals(List.of(p1), result);
    }

    @Test
    void getProductsByCategory_multipleProduct_returnsAll() {
        Product p1 = new Product("1", "El Tony Mate Mate & Mint", "Beverage", 1, 1.90);
        Product p2  = new Product("2", "El Tony Mate Mate & Ginger", "Beverage", 1, 1.90);
        when(databaseConnector.getProductsByCategory("Beverage")).thenReturn(List.of(p1, p2));

        List<Product> result = inventoryManager.getProductsByCategory("Beverage");

        assertEquals(List.of(p1, p2), result);
    }

    @Test
    void getProductsByCategory_emptyCategory_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> inventoryManager.getProductsByCategory(""));
    }

    @Test
    void getProductsByCategory_nullCategory_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> inventoryManager.getProductsByCategory(null));
    }

    @Test
    void getProductsByCategory_alwaysClosesConnection() {
        when(databaseConnector.getProductsByCategory("Beverage")).thenReturn(List.of());

        inventoryManager.getProductsByCategory("Beverage");

        verify(databaseConnector).close();
    }
}
