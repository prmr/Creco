package ca.mcgill.cs.creco.data;

import java.util.Collection;

/**
 * Provides methods to access the data from the database.
 */
public interface IDataStore 
{
	
	/**
	 * Gets a category from the database.
	 * @param pIndex The category's ID.
	 * @return The category object.
	 */
	Category getCategory(String pIndex);
	
	/**
	 * Gets all categories from the database.
	 * @return An iterable set of all the categories.
	 */
	Collection<Category> getCategories();
	
	/**
	 * Gets all products from the database.
	 * @return An iterable set of all the products.
	 */
	Collection<Product> getProducts();
}
