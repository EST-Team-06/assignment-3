# InventoryManagement

### A. Finding Low Stock Products

The `InventoryManager` class manages product inventory for an e-commerce system.
It communicates with a database through the `InventoryDatabase` interface, which provides access to product data.
The `getLowStockProducts` method returns products that have less than 10 items in stock.

Write *unit tests* for the `getLowStockProducts` method of the `InventoryManager` class.

Answer the following questions:
1. What are the external dependencies? Which of these dependencies should be tested using doubles and which should not? Explain your rationale.
2. For the dependencies that should be tested using doubles, should the production code be refactored to make it possible? If so, do the refactoring and implement the tests.
3. What are the disadvantages of using doubles in your tests? Answer with examples from the `InventoryManager` class.

### B. Filtering Products by Category

Implement a `getProductsByCategory` method in the `InventoryManager` class that will return all products belonging to a specific category. You must implement and test the method starting with Test-Driven Development (TDD). Document every step of your TDD iterative process in your `solution_[problem name].md` file, including:

- Each test you wrote
- Each small feature or code change you made to make the test pass
- Refactorings, if any

Afterwards, answer the questions from part A for this part as well.

Automate the test cases using the **JUnit5** plugin.

*Note 1*: Remember the techniques we learnt in the first chapters: test for empty cases, boundaries, etc. where applicable.

*Note 2*: Follow the **principles and best practices of good and maintainable test code**.