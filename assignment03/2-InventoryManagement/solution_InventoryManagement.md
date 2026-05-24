# InventoryManagement

## A. Finding Low Stock Products
`getLowStockProducts` uses the database connector defined on the class to get all products. It then filters the products based on the stock level, returning only those with stock below 10 as a list. Regardless of the products have been received and filtered from the database, instruct the database connector to close the connection.

We have multiple partitions to test; either the database returns no products, one product or multiple products. Furthermore, need to test the boundaries (*on-point: 9, off-point: 10*). Instinctively, we would also have tested against negative stock levels. However, in that case we don't think this is the concern/responsibility of this method, since a negative stock level means either a database bug or something in the logistics department has gone wrong and the product must be ordered regardless.

- **`getLowStockProducts_onPoint_returnsProduct`**: A single product with quantity 9 (on-point) is returned because it falls strictly below the threshold of 10.
- **`getLowStockProducts_offPoint_doesNotReturnProduct`**: A single product with quantity 10 (off-point) is not returned because it is exactly at the threshold and does not satisfy `< 10`.
- **`getLowStockProducts_multipleProducts_returnsOnlyLowStock`**: Given a mix of one low-stock and one in-stock product, only the low-stock product is returned, confirming the filter applies correctly across a set.
- **`getLowStockProducts_multipleProducts_allLowStock_returnsAll`**: When all products in the database are below the threshold, every product is returned, ruling out unintended truncation in the pipeline.
- **`getLowStockProducts_emptyInventory_returnsEmptyList`**: When the database returns no products, the method returns an empty list without throwing.
- **`getLowStockProducts_alwaysClosesConnection`**: Verifies that the database connection is always closed after calling `getLowStockProducts`, ensuring the `finally` block works as intended.

### Questions
1. What are the external dependencies? Which of these dependencies should be tested using doubles and which should not? Explain your rationale.

    *The single external dependency is the database connector. We need to mock it, specifically its `getAllProducts` method, so that it returns a predefined set of products for testing.*

2. For the dependencies that should be tested using doubles, should the production code be refactored to make it possible? If so, do the refactoring and implement the tests.

   *No, since the database connector is already a field in the class, we can just pass a mock database connector to the constructor during testing.*

3. What are the disadvantages of using doubles in your tests? Answer with examples from the `InventoryManager` class.

    *Mocking the database does not necessarily depict the reality in the sense that it does not reflect the actual database behavior, such as handling concurrent access or database errors. Furthermore, if the actual database connector is changed, we don't notice that automatically since we are using our own mock connector for testing.*

## B. Filtering Products by Category
We use TDD to implement the `getProductsByCategory` method.

### 1. Iteration
For the first iteration, we implement a simple happy test (`getProductsByCategory_singleProduct_returnsProduct`) that returns a product belonging to the specified category. Since `getProductsByCategory` seems similar to `getLowStockProducts`, we can reuse the implementation from the previous iteration.

```Java
public List<Product> getProductsByCategory(String category) {
   try {
      return this.databaseConnector.getProductsByCategory(category);
   } finally {
      this.databaseConnector.close();
   }
}
```

### 2. Iteration
For the second iteration, we implement a test (`getProductsByCategory_singleProduct_doesNotReturnProduct`) which ensures that if the database contains no products in the specified category, the method returns an empty list without throwing. The test passes without needing a refactor.

### 3. Iteration
For the third iteration, we implement a test (`getProductsByCategory_multipleProduct_returnsProduct`) which ensures that if the database contains multiple products across different categories, only the products belonging to the specified category are returned. The test passes without needing a refactor.

### 4. Iteration
For the third iteration, we implement a test (`getProductsByCategory_multipleProduct_returnsAll`) which ensures that if the database contains multiple products from the same category, all products are returned. The test passes without needing a refactor.

### 5. Iteration
It is not clear from the requirements what should happen if an empty category string is passed to the method. However, we think it is reasonable to throw an error, as this method is supposed to get products from a specific category. We expect the database connetor to return an empty list, since it tries to match a cateogry internall that does not exist. However, without any more details, these are just assumptions. We implement a test (`getProductsByCategory_emptyCategory_ThrowsException`) which ensures that if an empty string is passed, an `IllegalArgumentException` error is thrown. We also add a test for `null` input (`getProductsByCategory_nullCategory_ThrowsException`). Since the empty string test fails, we need to refactor our production code to reflect that.

```Java
public List<Product> getProductsByCategory(String category) {
   if (category == null || category.isEmpty()) {
      throw new IllegalArgumentException("Category must not be empty");
   }
   try {
      return this.databaseConnector.getProductsByCategory(category);
   } finally {
      this.databaseConnector.close();
   }
}
```

### 6. Iteration
We add a test (`getProductsByCategory_alwaysClosesConnection`) to verify that the database connection is always closed after calling `getProductsByCategory`, ensuring the `finally` block works as intended. The test passes without needing a refactor.

### Questions
1. What are the external dependencies? Which of these dependencies should be tested using doubles and which should not? Explain your rationale.

   *The single external dependency is the database connector. We need to mock it, specifically its `getProductsByCategory` method, so that it returns a predefined set of products for testing.*

2. For the dependencies that should be tested using doubles, should the production code be refactored to make it possible? If so, do the refactoring and implement the tests.

   *No, since the database connector is already a field in the class, we can just pass a mock database connector to the constructor during testing.*

3. What are the disadvantages of using doubles in your tests? Answer with examples from the `InventoryManager` class.

   *Mocking the database does not necessarily depict the reality in the sense that it does not reflect the actual database behavior, such as handling concurrent access or database errors. Furthermore, if the actual database connector is changed, we don't notice that automatically since we are using our own mock connector for testing.*